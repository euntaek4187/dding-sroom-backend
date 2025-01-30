package com.example.ddingsroom.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 6)
    private String code;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private LocalDateTime publishedDate;
    public VerificationCode(String code, String email) {
        this.code = code;
        this.email = email;
        this.publishedDate = LocalDateTime.now();
    }
}