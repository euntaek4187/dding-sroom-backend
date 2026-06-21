package com.example.ddingsroom.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refresh 토큰 정리 스케줄러 + 시작 시 1회 마이그레이션 실행기.
 * 외부 API 동작에는 영향을 주지 않는 백그라운드 작업이다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenScheduler implements ApplicationRunner {

    private final RefreshTokenMaintenanceService maintenanceService;

    /** 앱 시작 시 1회: prefix 인덱스 보장 → 레거시 만료시각 백필 → 만료 토큰 정리 */
    @Override
    public void run(ApplicationArguments args) {
        maintenanceService.ensureRefreshPrefixIndex();

        int processed = 0;
        int guard = 0;
        int batch;
        while ((batch = maintenanceService.backfillLegacyBatch()) > 0 && guard++ < 100_000) {
            processed += batch;
        }
        if (processed > 0) {
            log.info("[RefreshToken] 레거시 만료시각 백필 완료: {}건 처리", processed);
        }

        int purged = maintenanceService.cleanupExpired();
        log.info("[RefreshToken] 시작 정리 완료: 만료 {}건 삭제", purged);
    }

    /** 매시 정각에 만료된 refresh 토큰 정리 */
    @Scheduled(cron = "0 0 * * * *")
    public void scheduledCleanup() {
        int purged = maintenanceService.cleanupExpired();
        if (purged > 0) {
            log.info("[RefreshToken] 정기 정리: 만료 {}건 삭제", purged);
        }
    }
}
