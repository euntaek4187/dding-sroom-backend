package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.service.AdminSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system")
@Tag(name = "관리자 - 시스템 관리", description = "관리자 전용 시스템 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSystemController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminSystemController.class);
    private final AdminSystemService adminSystemService;
    
    @Autowired
    public AdminSystemController(AdminSystemService adminSystemService) {
        this.adminSystemService = adminSystemService;
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "관리자 대시보드", description = "전체 시스템 현황을 한눈에 볼 수 있는 대시보드 정보를 제공합니다.")
    public ResponseEntity<?> getAdminDashboard() {
        logger.info("관리자 - 대시보드 조회 요청");
        return adminSystemService.getAdminDashboard();
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "전체 시스템 통계", description = "사용자, 예약, 스터디룸 등 전체 시스템 통계를 조회합니다.")
    public ResponseEntity<?> getSystemStatistics() {
        logger.info("관리자 - 전체 시스템 통계 조회 요청");
        return adminSystemService.getSystemStatistics();
    }
    
    @GetMapping("/health")
    @Operation(summary = "시스템 상태 확인", description = "시스템의 전반적인 상태를 확인합니다.")
    public ResponseEntity<?> getSystemHealth() {
        logger.info("관리자 - 시스템 상태 확인 요청");
        return adminSystemService.getSystemHealth();
    }
} 