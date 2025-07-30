package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.service.AdminReservationService;
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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/reservations")
@Tag(name = "관리자 - 예약 관리", description = "관리자 전용 예약 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminReservationController.class);
    private final AdminReservationService adminReservationService;
    
    @Autowired
    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }
    
    @GetMapping
    @Operation(summary = "모든 예약 조회", description = "시스템의 모든 예약 목록을 조회합니다.")
    public ResponseEntity<?> getAllReservations() {
        logger.info("관리자 - 전체 예약 목록 조회 요청");
        return adminReservationService.getAllReservations();
    }
    
    @GetMapping("/{reservationId}")
    @Operation(summary = "특정 예약 조회", description = "예약 ID로 특정 예약의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getReservationById(
            @Parameter(description = "예약 ID", required = true)
            @PathVariable Integer reservationId) {
        logger.info("관리자 - 예약 상세 조회 요청: reservationId={}", reservationId);
        return adminReservationService.getReservationById(reservationId);
    }
    
    @PostMapping("/{reservationId}/force-cancel")
    @Operation(summary = "예약 강제 취소", description = "관리자 권한으로 특정 예약을 강제 취소합니다.")
    public ResponseEntity<AdminResponseDTO> forceCancelReservation(
            @Parameter(description = "예약 ID", required = true)
            @PathVariable Integer reservationId) {
        logger.info("관리자 - 예약 강제 취소 요청: reservationId={}", reservationId);
        return adminReservationService.forceCancelReservation(reservationId);
    }
    
    @GetMapping("/date")
    @Operation(summary = "날짜별 예약 조회", description = "특정 날짜의 모든 예약을 조회합니다.")
    public ResponseEntity<?> getReservationsByDate(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("관리자 - 날짜별 예약 조회 요청: date={}", date);
        return adminReservationService.getReservationsByDate(date);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "기간별 예약 조회", description = "특정 기간의 모든 예약을 조회합니다.")
    public ResponseEntity<?> getReservationsByDateRange(
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("관리자 - 기간별 예약 조회 요청: startDate={}, endDate={}", startDate, endDate);
        return adminReservationService.getReservationsByDateRange(startDate, endDate);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "예약 통계", description = "예약 관련 통계 정보를 조회합니다.")
    public ResponseEntity<?> getReservationStatistics() {
        logger.info("관리자 - 예약 통계 조회 요청");
        return adminReservationService.getReservationStatistics();
    }
    
    @GetMapping("/statistics/daily")
    @Operation(summary = "일별 예약 통계", description = "일별 예약 현황 통계를 조회합니다.")
    public ResponseEntity<?> getDailyReservationStatistics(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("관리자 - 일별 예약 통계 조회 요청: date={}", date);
        return adminReservationService.getDailyReservationStatistics(date);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 사용자 예약 조회", description = "특정 사용자의 모든 예약을 조회합니다.")
    public ResponseEntity<?> getReservationsByUserId(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Integer userId) {
        logger.info("관리자 - 사용자별 예약 조회 요청: userId={}", userId);
        return adminReservationService.getReservationsByUserId(userId);
    }
} 