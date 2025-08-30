package com.example.ddingsroom.user.repository;

import com.example.ddingsroom.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByUsername(String username);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
    Boolean existsByEmail(String email);
}