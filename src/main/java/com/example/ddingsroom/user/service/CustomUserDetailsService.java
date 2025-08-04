package com.example.ddingsroom.user.service;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("CustomUserDetailsService: loadUserByUsername 메소드 시작. 검색할 Email: " + email); // 시작 로그

        UserEntity userData = userRepository.findByEmail(email); // 이메일로 사용자 조회

        if (userData != null) {
            System.out.println("CustomUserDetailsService: 사용자 발견 - Username: " + userData.getUsername() + ", Role: " + userData.getRole()); // 사용자 발견 로그
            return new CustomUserDetails(userData);
        } else {
            System.out.println("CustomUserDetailsService: 사용자 발견 실패. Email: " + email); // 사용자 미발견 로그
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}