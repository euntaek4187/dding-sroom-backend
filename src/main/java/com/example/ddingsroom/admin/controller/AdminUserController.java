package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.service.AdminUserService;
import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@Tag(name = "관리자 - 사용자 관리", description = "관리자 전용 사용자 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
    private final AdminUserService adminUserService;
    
    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
    
    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "시스템에 등록된 모든 사용자 목록을 조회합니다.")
    public ResponseEntity<?> getAllUsers() {
        logger.info("관리자 - 전체 사용자 목록 조회 요청");
        return adminUserService.getAllUsers();
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "특정 사용자 조회", description = "사용자 ID로 특정 사용자의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId) {
        logger.info("관리자 - 사용자 상세 조회 요청: userId={}", userId);
        return adminUserService.getUserById(userId);
    }
    
    @PutMapping("/{userId}/status")
    @Operation(summary = "사용자 상태 변경", description = "특정 사용자의 상태를 변경합니다 (normal/blocked)")
    public ResponseEntity<AdminResponseDTO> updateUserStatus(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "변경할 상태 (normal/blocked)", required = true)
            @RequestParam String status) {
        logger.info("관리자 - 사용자 상태 변경 요청: userId={}, status={}", userId, status);
        return adminUserService.updateUserStatus(userId, status);
    }
    
    @PutMapping("/{userId}/role")
    @Operation(summary = "사용자 권한 변경", description = "특정 사용자의 권한을 변경합니다 (ROLE_USER/ROLE_ADMIN)")
    public ResponseEntity<AdminResponseDTO> updateUserRole(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "변경할 권한 (ROLE_USER/ROLE_ADMIN)", required = true)
            @RequestParam String role) {
        logger.info("관리자 - 사용자 권한 변경 요청: userId={}, role={}", userId, role);
        return adminUserService.updateUserRole(userId, role);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "사용자 삭제", description = "특정 사용자를 시스템에서 삭제합니다.")
    public ResponseEntity<AdminResponseDTO> deleteUser(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId) {
        logger.info("관리자 - 사용자 삭제 요청: userId={}", userId);
        return adminUserService.deleteUser(userId);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "사용자 통계", description = "사용자 관련 통계 정보를 조회합니다.")
    public ResponseEntity<?> getUserStatistics() {
        logger.info("관리자 - 사용자 통계 조회 요청");
        return adminUserService.getUserStatistics();
    }
} 