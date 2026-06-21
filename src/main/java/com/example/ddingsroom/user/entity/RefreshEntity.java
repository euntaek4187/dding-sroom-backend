package com.example.ddingsroom.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_refresh_username", columnList = "username"),
        @Index(name = "idx_refresh_expiration_at", columnList = "expiration_at")
})
@Getter
@Setter
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Column(length = 1024)
    private String refresh;

    // 기존 expiration(VARCHAR, 비파싱 문자열) 대신 파싱·비교 가능한 DATETIME 컬럼 사용
    @Column(name = "expiration_at")
    private LocalDateTime expirationAt;
}