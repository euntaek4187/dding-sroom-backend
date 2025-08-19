package com.example.ddingsroom.notification.repository;

import com.example.ddingsroom.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {

    List<NotificationEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.createdAt >= :threeDaysAgo")
    Long countNotificationsInLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);

    List<NotificationEntity> findByTitleContaining(String title);

    List<NotificationEntity> findByContentContaining(String content);

    // 특정 사용자의 모든 공지사항 삭제
    void deleteByUserId(Integer userId);
}