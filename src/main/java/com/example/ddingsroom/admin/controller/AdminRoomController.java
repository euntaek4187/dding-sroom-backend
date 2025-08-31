package com.example.ddingsroom.admin.controller;

import com.example.ddingsroom.admin.service.AdminRoomService;
import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminRoomRequestDTO;
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
@RequestMapping("/admin/rooms")
@Tag(name = "관리자 - 스터디룸 관리", description = "관리자 전용 스터디룸 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminRoomController.class);
    private final AdminRoomService adminRoomService;
    
    @Autowired
    public AdminRoomController(AdminRoomService adminRoomService) {
        this.adminRoomService = adminRoomService;
    }
    
    @GetMapping
    @Operation(summary = "모든 스터디룸 조회", description = "시스템의 모든 스터디룸 목록을 조회합니다.")
    public ResponseEntity<?> getAllRooms() {
        logger.info("관리자 - 전체 스터디룸 목록 조회 요청");
        return adminRoomService.getAllRooms();
    }
    
    @GetMapping("/{roomId}")
    @Operation(summary = "특정 스터디룸 조회", description = "룸 ID로 특정 스터디룸의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getRoomById(
            @Parameter(description = "스터디룸 ID", required = true)
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸 상세 조회 요청: roomId={}", roomId);
        return adminRoomService.getRoomById(roomId);
    }
    
    @PostMapping
    @Operation(summary = "스터디룸 생성", description = "새로운 스터디룸을 생성합니다.")
    public ResponseEntity<AdminResponseDTO> createRoom(@RequestBody AdminRoomRequestDTO roomRequestDTO) {
        logger.info("관리자 - 스터디룸 생성 요청: roomName={}", roomRequestDTO.getRoomName());
        return adminRoomService.createRoom(roomRequestDTO);
    }
    
    @PutMapping("/{roomId}")
    @Operation(summary = "스터디룸 정보 수정", description = "기존 스터디룸의 정보를 수정합니다.")
    public ResponseEntity<AdminResponseDTO> updateRoom(
            @Parameter(description = "스터디룸 ID", required = true)
            @PathVariable Long roomId,
            @RequestBody AdminRoomRequestDTO roomRequestDTO) {
        logger.info("관리자 - 스터디룸 수정 요청: roomId={}, roomName={}", roomId, roomRequestDTO.getRoomName());
        return adminRoomService.updateRoom(roomId, roomRequestDTO);
    }
    
    @PutMapping("/{roomId}/status")
    @Operation(summary = "스터디룸 상태 변경", description = "스터디룸의 상태를 변경합니다 (IDLE/OCCUPIED/MAINTENANCE)")
    public ResponseEntity<AdminResponseDTO> updateRoomStatus(
            @Parameter(description = "스터디룸 ID", required = true)
            @PathVariable Long roomId,
            @Parameter(description = "변경할 상태 (IDLE/OCCUPIED/MAINTENANCE)", required = true)
            @RequestParam String status) {
        logger.info("관리자 - 스터디룸 상태 변경 요청: roomId={}, status={}", roomId, status);
        return adminRoomService.updateRoomStatus(roomId, status);
    }
    
    @DeleteMapping("/{roomId}")
    @Operation(summary = "스터디룸 삭제", description = "스터디룸을 시스템에서 삭제합니다.")
    public ResponseEntity<AdminResponseDTO> deleteRoom(
            @Parameter(description = "스터디룸 ID", required = true)
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸 삭제 요청: roomId={}", roomId);
        return adminRoomService.deleteRoom(roomId);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "스터디룸 통계", description = "스터디룸 관련 통계 정보를 조회합니다.")
    public ResponseEntity<?> getRoomStatistics() {
        logger.info("관리자 - 스터디룸 통계 조회 요청");
        return adminRoomService.getRoomStatistics();
    }
    
    @GetMapping("/{roomId}/reservations")
    @Operation(summary = "스터디룸별 예약 현황", description = "특정 스터디룸의 예약 현황을 조회합니다.")
    public ResponseEntity<?> getRoomReservations(
            @Parameter(description = "스터디룸 ID", required = true)
            @PathVariable Long roomId) {
        logger.info("관리자 - 스터디룸별 예약 현황 조회 요청: roomId={}", roomId);
        return adminRoomService.getRoomReservations(roomId);
    }
} 