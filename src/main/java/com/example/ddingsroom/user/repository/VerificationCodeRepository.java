package com.example.ddingsroom.user.repository;

import com.example.ddingsroom.user.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCode(String code); // 코드로 조회
    Optional<VerificationCode> findByEmail(String email); // 유저이름으로 조회
}

