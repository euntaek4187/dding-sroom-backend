package com.example.ddingsroom.reservation.controller;

import com.example.ddingsroom.reservation.dto.BaseResponseDTO;
import com.example.ddingsroom.reservation.dto.ReservationCancelRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import com.example.ddingsroom.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO> createReservation(@RequestBody ReservationRequestDTO requestDTO) {
        logger.info("예약 생성 요청: roomId={}, startTime={}, endTime={}", 
                requestDTO.getRoomId(),
                requestDTO.getReservationStartTime(), 
                requestDTO.getReservationEndTime());
        
        BaseResponseDTO response = reservationService.createReservation(requestDTO);
        
        if (response.getMessage().contains("불가능") || response.getMessage().contains("찾을 수 없습니다")) {
            logger.warn("예약 생성 실패: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        logger.info("예약 생성 성공: {}", response.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<BaseResponseDTO> cancelReservation(@RequestBody ReservationCancelRequestDTO requestDTO) {
        logger.info("예약 취소 요청: userId={}, reservationId={}", requestDTO.getUserId(), requestDTO.getReservationId());
        
        BaseResponseDTO response = reservationService.cancelReservation(requestDTO);
        
        if (response.getMessage().contains("본인의 예약만") || response.getMessage().contains("이미 취소된") || 
            response.getMessage().contains("찾을 수 없습니다")) {
            logger.warn("예약 취소 실패: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        logger.info("예약 취소 성공: {}", response.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ReservationResponseDTO> getUserReservations(@PathVariable int userId) {
        logger.info("사용자 예약 조회 요청: userId={}", userId);
        
        ReservationResponseDTO response = reservationService.getUserReservations(userId);
        
        logger.info("사용자 예약 조회 완료: count={}", 
                response.getReservations() != null ? response.getReservations().size() : 0);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all-reservation")
    public ResponseEntity<ReservationResponseDTO> getAllReservations() {
        logger.info("전체 예약 조회 요청");

        ReservationResponseDTO response = reservationService.getAllReservations();

        logger.info("전체 예약 조회 완료: count={}",
                response.getReservations() != null ? response.getReservations().size() : 0);
        return ResponseEntity.ok(response);
    }
}