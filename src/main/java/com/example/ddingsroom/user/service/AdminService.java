package com.example.ddingsroom.user.service;

import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.repository.RoomRepository;
import com.example.ddingsroom.user.dto.ResponseDTO;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
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

@Service
public class AdminService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    
    @Autowired
    public AdminService(UserRepository userRepository, 
                       ReservationRepository reservationRepository,
                       RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }
    
    /**
     * 모든 사용자 목록 조회
     */
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "사용자 목록 조회 성공");
            response.put("users", users);
            response.put("totalCount", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("사용자 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("사용자 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 상태 변경 (활성화/비활성화)
     */
    @Transactional
    public ResponseEntity<ResponseDTO> updateUserStatus(Integer userId, String status) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO("올바른 사용자 ID를 입력해주세요."));
            }
            
            if (!"normal".equals(status) && !"blocked".equals(status)) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO("상태는 'normal' 또는 'blocked'만 가능합니다."));
            }
            
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO("해당 사용자를 찾을 수 없습니다."));
            }
            
            UserEntity user = userOptional.get();
            user.setState(status);
            userRepository.save(user);
            
            String statusText = "normal".equals(status) ? "활성화" : "비활성화";
            return ResponseEntity.ok(new ResponseDTO(
                    String.format("사용자 %s가 %s되었습니다.", user.getUsername(), statusText)));
            
        } catch (Exception e) {
            logger.error("사용자 상태 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("사용자 상태 변경 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 관리자 통계 정보 조회
     */
    public ResponseEntity<?> getAdminStatistics() {
        try {
            long totalUsers = userRepository.count();
            long totalReservations = reservationRepository.count();
            long totalRooms = roomRepository.count();
            
            // 활성 예약 수 (RESERVED 상태)
            List<ReservationEntity> allReservations = reservationRepository.findAll();
            long activeReservations = allReservations.stream()
                    .filter(r -> "RESERVED".equals(r.getStatus()))
                    .count();
            
            // 오늘 예약 수
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime tomorrow = today.plusDays(1);
            long todayReservations = allReservations.stream()
                    .filter(r -> r.getCreatedAt().isAfter(today) && r.getCreatedAt().isBefore(tomorrow))
                    .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("totalReservations", totalReservations);
            statistics.put("totalRooms", totalRooms);
            statistics.put("activeReservations", activeReservations);
            statistics.put("todayReservations", todayReservations);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "통계 정보 조회 성공");
            response.put("statistics", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("통계 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("통계 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 예약 강제 취소 (관리자 권한)
     */
    @Transactional
    public ResponseEntity<ResponseDTO> forceCancel(Integer reservationId) {
        try {
            if (reservationId == null || reservationId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO("올바른 예약 ID를 입력해주세요."));
            }
            
            Optional<ReservationEntity> reservationOptional = reservationRepository.findById(reservationId);
            if (reservationOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO("해당 예약을 찾을 수 없습니다."));
            }
            
            ReservationEntity reservation = reservationOptional.get();
            
            if ("CANCELLED".equals(reservation.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO("이미 취소된 예약입니다."));
            }
            
            reservation.setStatus("CANCELLED");
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
            
            return ResponseEntity.ok(new ResponseDTO("예약이 관리자에 의해 강제 취소되었습니다."));
            
        } catch (Exception e) {
            logger.error("예약 강제 취소 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("예약 강제 취소 중 오류가 발생했습니다."));
        }
    }
} 