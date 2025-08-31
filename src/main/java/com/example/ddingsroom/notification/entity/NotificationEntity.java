package com.example.ddingsroom.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.ddingsroom.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        createdAt = now;
        updatedAt = now;
        if (viewCount == null) {
            viewCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUserId(Long userId) {
    }
}