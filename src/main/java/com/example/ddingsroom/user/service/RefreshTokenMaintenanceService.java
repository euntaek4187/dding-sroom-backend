package com.example.ddingsroom.user.service;

import com.example.ddingsroom.user.entity.RefreshEntity;
import com.example.ddingsroom.user.jwt.JWTUtil;
import com.example.ddingsroom.user.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Refresh 토큰 테이블 유지보수 로직.
 * - 만료 토큰 일괄 삭제 (무한 누적 방지)
 * - 레거시 행(만료시각 미설정)을 JWT의 실제 만료시각으로 백필 → 활성 세션 보존하며 정리
 * - refresh 컬럼 prefix 인덱스 보장 (조회 성능, best-effort)
 *
 * 로그인/재발급/로그아웃의 외부 동작(요청·응답·헤더·쿠키)에는 전혀 관여하지 않는다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenMaintenanceService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final String TABLE = "refresh_entity";        // 기본 네이밍 전략 기준 테이블명
    private static final String REFRESH_INDEX = "idx_refresh_value";

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;

    /** A: 만료된 refresh 행 일괄 삭제. 삭제 건수 반환 */
    @Transactional
    public int cleanupExpired() {
        return refreshRepository.deleteAllByExpirationAtBefore(LocalDateTime.now(ZONE));
    }

    /**
     * B(1회성 마이그레이션): 만료시각이 비어있는 레거시 행 한 배치를 처리한다.
     * 저장된 JWT의 실제 만료시각을 읽어 채우고(활성 세션 보존), 만료/위조 토큰은 삭제한다.
     * 처리한 건수를 반환(0이면 더 이상 레거시 행이 없음).
     */
    @Transactional
    public int backfillLegacyBatch() {
        List<RefreshEntity> batch = refreshRepository.findTop500ByExpirationAtIsNull();
        if (batch.isEmpty()) {
            return 0;
        }
        List<RefreshEntity> toSave = new ArrayList<>();
        List<RefreshEntity> toDelete = new ArrayList<>();
        for (RefreshEntity e : batch) {
            try {
                e.setExpirationAt(jwtUtil.getExpiration(e.getRefresh()));
                toSave.add(e);
            } catch (Exception ex) {
                // 만료/위조/파싱 불가 → 죽은 토큰 → 제거
                toDelete.add(e);
            }
        }
        refreshRepository.deleteAll(toDelete);
        refreshRepository.saveAll(toSave);
        return batch.size();
    }

    /** C: refresh 컬럼 prefix 인덱스 보장 (1024자 컬럼이라 prefix 사용). 실패해도 앱은 정상 동작 */
    public void ensureRefreshPrefixIndex() {
        try {
            Integer cnt = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM information_schema.statistics " +
                            "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?",
                    Integer.class, TABLE, REFRESH_INDEX);
            if (cnt == null || cnt == 0) {
                jdbcTemplate.execute("CREATE INDEX " + REFRESH_INDEX + " ON " + TABLE + " (refresh(191))");
                log.info("[RefreshToken] refresh prefix 인덱스 생성 완료");
            }
        } catch (Exception e) {
            log.warn("[RefreshToken] refresh prefix 인덱스 생성 생략(환경/권한): {}", e.getMessage());
        }
    }
}
