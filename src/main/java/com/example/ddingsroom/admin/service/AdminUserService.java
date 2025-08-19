package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminUserDTO;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.community_post.service.CommunityPostService;
import com.example.ddingsroom.CommunityPostComment.service.CommunityPostCommentService;
import com.example.ddingsroom.suggest_post.service.SuggestPostSevice;
import com.example.ddingsroom.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);
    private final UserRepository userRepository;
    private final CommunityPostService communityPostService;
    private final CommunityPostCommentService communityPostCommentService;
    private final SuggestPostSevice suggestPostService;
    private final NotificationService notificationService;
    
    @Autowired
    public AdminUserService(UserRepository userRepository,
                          CommunityPostService communityPostService,
                          CommunityPostCommentService communityPostCommentService,
                          SuggestPostSevice suggestPostService,
                          NotificationService notificationService) {
        this.userRepository = userRepository;
        this.communityPostService = communityPostService;
        this.communityPostCommentService = communityPostCommentService;
        this.suggestPostService = suggestPostService;
        this.notificationService = notificationService;
    }
    
    /**
     * 모든 사용자 목록 조회
     */
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            List<AdminUserDTO> userDTOs = users.stream()
                    .map(AdminUserDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "사용자 목록 조회 성공");
            response.put("users", userDTOs);
            response.put("totalCount", userDTOs.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("사용자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 사용자 상세 조회
     */
    public ResponseEntity<?> getUserById(Integer userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 사용자 ID를 입력해주세요."));
            }
            
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 사용자를 찾을 수 없습니다."));
            }
            
            AdminUserDTO userDTO = AdminUserDTO.fromEntity(userOptional.get());
            return ResponseEntity.ok(AdminResponseDTO.success("사용자 조회 성공", userDTO));
            
        } catch (Exception e) {
            logger.error("사용자 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 상태 변경 (활성화/비활성화)
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> updateUserStatus(Integer userId, String status) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 사용자 ID를 입력해주세요."));
            }
            
            if (!"normal".equals(status) && !"blocked".equals(status)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("상태는 'normal' 또는 'blocked'만 가능합니다."));
            }
            
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 사용자를 찾을 수 없습니다."));
            }
            
            UserEntity user = userOptional.get();
            user.setState(status);
            userRepository.save(user);
            
            String statusText = "normal".equals(status) ? "활성화" : "비활성화";
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("사용자 '%s'가 %s되었습니다.", user.getUsername(), statusText)));
            
        } catch (Exception e) {
            logger.error("사용자 상태 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 상태 변경 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 권한 변경
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> updateUserRole(Integer userId, String role) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 사용자 ID를 입력해주세요."));
            }
            
            if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("권한은 'ROLE_USER' 또는 'ROLE_ADMIN'만 가능합니다."));
            }
            
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 사용자를 찾을 수 없습니다."));
            }
            
            UserEntity user = userOptional.get();
            user.setRole(role);
            userRepository.save(user);
            
            String roleText = "ROLE_ADMIN".equals(role) ? "관리자" : "일반 사용자";
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("사용자 '%s'의 권한이 %s로 변경되었습니다.", user.getUsername(), roleText)));
            
        } catch (Exception e) {
            logger.error("사용자 권한 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 권한 변경 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 삭제 (관련 게시글, 댓글도 함께 삭제)
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> deleteUser(Integer userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 사용자 ID를 입력해주세요."));
            }
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 사용자를 찾을 수 없습니다."));
            }
            
            UserEntity user = userOptional.get();
            String username = user.getUsername();
            Long userIdLong = userId.longValue();
            
            // 1. 먼저 사용자의 모든 댓글 삭제 (커뮤니티 + 건의)
            communityPostCommentService.deleteAllUserComments(userIdLong);
            suggestPostService.deleteAllUserSuggestComments(userIdLong);
            
            // 2. 사용자의 모든 게시글과 관련 댓글 삭제 (커뮤니티 + 건의)
            communityPostService.deleteAllUserPosts(userIdLong);
            suggestPostService.deleteAllUserSuggestPosts(userIdLong);
            
            // 3. 사용자의 모든 공지사항 삭제
            notificationService.deleteAllUserNotifications(userId);
            
            // 4. 마지막으로 사용자 계정 삭제
            userRepository.delete(user);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("사용자 '%s'와 관련된 모든 데이터가 삭제되었습니다.", username)));
            
        } catch (Exception e) {
            logger.error("사용자 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 통계 정보 조회
     */
    public ResponseEntity<?> getUserStatistics() {
        try {
            long totalUsers = userRepository.count();
            
            List<UserEntity> allUsers = userRepository.findAll();
            
            long adminCount = allUsers.stream()
                    .filter(user -> "ROLE_ADMIN".equals(user.getRole()))
                    .count();
            
            long normalUserCount = allUsers.stream()
                    .filter(user -> "ROLE_USER".equals(user.getRole()))
                    .count();
            
            long activeUsers = allUsers.stream()
                    .filter(user -> "normal".equals(user.getState()))
                    .count();
            
            long blockedUsers = allUsers.stream()
                    .filter(user -> "blocked".equals(user.getState()))
                    .count();
            
            // 최근 가입자 (7일 이내)
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            long recentUsers = allUsers.stream()
                    .filter(user -> user.getRegistrationDate() != null && 
                                  user.getRegistrationDate().isAfter(sevenDaysAgo))
                    .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("adminCount", adminCount);
            statistics.put("normalUserCount", normalUserCount);
            statistics.put("activeUsers", activeUsers);
            statistics.put("blockedUsers", blockedUsers);
            statistics.put("recentUsers", recentUsers);
            
            return ResponseEntity.ok(AdminResponseDTO.success("사용자 통계 조회 성공", statistics));
            
        } catch (Exception e) {
            logger.error("사용자 통계 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자 통계 조회 중 오류가 발생했습니다."));
        }
    }
} 