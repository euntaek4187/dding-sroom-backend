package com.example.ddingsroom.user.repository;

import com.example.ddingsroom.user.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    @Transactional
    void deleteByUsername(String username);

    // 만료된 토큰 일괄 삭제 (스케줄러/시작 정리용)
    @Modifying(clearAutomatically = true)
    @Query("delete from RefreshEntity r where r.expirationAt < :now")
    int deleteAllByExpirationAtBefore(@Param("now") LocalDateTime now);

    // 레거시(만료시각 미설정) 행 배치 조회 (1회성 백필용)
    List<RefreshEntity> findTop500ByExpirationAtIsNull();
}