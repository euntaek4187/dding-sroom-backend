package com.example.ddingsroom.user.controller;

import com.example.ddingsroom.user.dto.ResponseDTO;
import com.example.ddingsroom.user.service.AdminService;
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
@RequestMapping("/admin")
@Tag(name = "관리자 API", description = "관리자 전용 기능을 제공하는 API입니다.")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
    
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping
    @Operation(summary = "관리자 대시보드", description = "관리자 페이지 기본 정보를 반환합니다.")
    public ResponseEntity<ResponseDTO> getAdminDashboard() {
        logger.info("관리자 대시보드 접근");
        return ResponseEntity.ok(new ResponseDTO("관리자 페이지에 오신 것을 환영합니다."));
    }
    
    @GetMapping("/users")
    @Operation(summary = "모든 사용자 조회", description = "시스템에 등록된 모든 사용자 목록을 조회합니다.")
    public ResponseEntity<?> getAllUsers() {
        logger.info("관리자 - 전체 사용자 목록 조회 요청");
        return adminService.getAllUsers();
    }
    
    @PutMapping("/users/{userId}/status")
    @Operation(summary = "사용자 상태 변경", description = "특정 사용자의 상태를 변경합니다 (normal/blocked)")
    public ResponseEntity<ResponseDTO> updateUserStatus(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "변경할 상태 (normal/blocked)", required = true)
            @RequestParam String status) {
        logger.info("관리자 - 사용자 상태 변경 요청: userId={}, status={}", userId, status);
        return adminService.updateUserStatus(userId, status);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "관리자 통계 정보", description = "시스템 전반의 통계 정보를 조회합니다.")
    public ResponseEntity<?> getStatistics() {
        logger.info("관리자 - 통계 정보 조회 요청");
        return adminService.getAdminStatistics();
    }
    
    @PostMapping("/reservations/{reservationId}/force-cancel")
    @Operation(summary = "예약 강제 취소", description = "관리자 권한으로 특정 예약을 강제 취소합니다.")
    public ResponseEntity<ResponseDTO> forceCancel(
            @Parameter(description = "예약 ID", required = true)
            @PathVariable Integer reservationId) {
        logger.info("관리자 - 예약 강제 취소 요청: reservationId={}", reservationId);
        return adminService.forceCancel(reservationId);
    }
}