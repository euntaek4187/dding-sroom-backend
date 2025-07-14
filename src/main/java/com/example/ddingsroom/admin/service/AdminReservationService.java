package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminReservationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminReservationService.class);
    
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public AdminReservationService(ReservationRepository reservationRepository, 
                                 UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 모든 예약 목록 조회
     */
    public ResponseEntity<?> getAllReservations() {
        try {
            List<ReservationEntity> reservations = reservationRepository.findAll();
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = reservations.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "전체 예약 조회 성공");
            response.put("reservations", reservationDTOs);
            response.put("totalCount", reservationDTOs.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("전체 예약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("예약 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 예약 상세 조회
     */
    public ResponseEntity<?> getReservationById(Integer reservationId) {
        try {
            if (reservationId == null || reservationId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 예약 ID를 입력해주세요."));
            }
            
            Optional<ReservationEntity> reservationOptional = reservationRepository.findById(reservationId);
            if (reservationOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 예약을 찾을 수 없습니다."));
            }
            
            ReservationResponseDTO.ReservationDTO reservationDTO = 
                    ReservationResponseDTO.ReservationDTO.fromEntity(reservationOptional.get());
            
            return ResponseEntity.ok(AdminResponseDTO.success("예약 조회 성공", reservationDTO));
            
        } catch (Exception e) {
            logger.error("예약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("예약 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 예약 강제 취소 (관리자 권한)
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> forceCancelReservation(Integer reservationId) {
        try {
            if (reservationId == null || reservationId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 예약 ID를 입력해주세요."));
            }
            
            Optional<ReservationEntity> reservationOptional = reservationRepository.findById(reservationId);
            if (reservationOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 예약을 찾을 수 없습니다."));
            }
            
            ReservationEntity reservation = reservationOptional.get();
            
            if ("CANCELLED".equals(reservation.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("이미 취소된 예약입니다."));
            }
            
            reservation.setStatus("CANCELLED");
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
            
            return ResponseEntity.ok(AdminResponseDTO.success("예약이 관리자에 의해 강제 취소되었습니다."));
            
        } catch (Exception e) {
            logger.error("예약 강제 취소 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("예약 강제 취소 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 날짜별 예약 조회
     */
    public ResponseEntity<?> getReservationsByDate(LocalDate date) {
        try {
            if (date == null) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("날짜를 입력해주세요."));
            }
            
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            List<ReservationEntity> reservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getStartTime().isAfter(startOfDay) && 
                               r.getStartTime().isBefore(endOfDay))
                    .collect(Collectors.toList());
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = reservations.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("%s 날짜의 예약 조회 성공", date));
            response.put("date", date);
            response.put("reservations", reservationDTOs);
            response.put("totalCount", reservationDTOs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("날짜별 예약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("날짜별 예약 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 기간별 예약 조회
     */
    public ResponseEntity<?> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            if (startDate == null || endDate == null) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("시작 날짜와 종료 날짜를 모두 입력해주세요."));
            }
            
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("시작 날짜는 종료 날짜보다 이전이어야 합니다."));
            }
            
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            List<ReservationEntity> reservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getStartTime().isAfter(startDateTime) && 
                               r.getStartTime().isBefore(endDateTime))
                    .collect(Collectors.toList());
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = reservations.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("%s ~ %s 기간의 예약 조회 성공", startDate, endDate));
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("reservations", reservationDTOs);
            response.put("totalCount", reservationDTOs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("기간별 예약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("기간별 예약 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 예약 통계 정보 조회
     */
    public ResponseEntity<?> getReservationStatistics() {
        try {
            List<ReservationEntity> allReservations = reservationRepository.findAll();
            
            long totalReservations = allReservations.size();
            long activeReservations = allReservations.stream()
                    .filter(r -> "RESERVED".equals(r.getStatus()))
                    .count();
            long cancelledReservations = allReservations.stream()
                    .filter(r -> "CANCELLED".equals(r.getStatus()))
                    .count();
            
            // 오늘 예약 수
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
            long todayReservations = allReservations.stream()
                    .filter(r -> r.getCreatedAt().isAfter(todayStart) && 
                               r.getCreatedAt().isBefore(todayEnd))
                    .count();
            
            // 이번 주 예약 수
            LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
            long weeklyReservations = allReservations.stream()
                    .filter(r -> r.getCreatedAt().isAfter(weekStart))
                    .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalReservations", totalReservations);
            statistics.put("activeReservations", activeReservations);
            statistics.put("cancelledReservations", cancelledReservations);
            statistics.put("todayReservations", todayReservations);
            statistics.put("weeklyReservations", weeklyReservations);
            
            return ResponseEntity.ok(AdminResponseDTO.success("예약 통계 조회 성공", statistics));
            
        } catch (Exception e) {
            logger.error("예약 통계 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("예약 통계 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 일별 예약 통계 조회
     */
    public ResponseEntity<?> getDailyReservationStatistics(LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now(); // 날짜가 없으면 오늘 날짜 사용
            }
            
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            List<ReservationEntity> dayReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getStartTime().isAfter(startOfDay) && 
                               r.getStartTime().isBefore(endOfDay))
                    .collect(Collectors.toList());
            
            long totalDayReservations = dayReservations.size();
            long activeDayReservations = dayReservations.stream()
                    .filter(r -> "RESERVED".equals(r.getStatus()))
                    .count();
            long cancelledDayReservations = dayReservations.stream()
                    .filter(r -> "CANCELLED".equals(r.getStatus()))
                    .count();
            
            // 시간대별 예약 현황 (9시~22시)
            Map<String, Long> hourlyStats = new HashMap<>();
            for (int hour = 9; hour <= 22; hour++) {
                final int currentHour = hour;
                long hourlyCount = dayReservations.stream()
                        .filter(r -> r.getStartTime().getHour() == currentHour)
                        .count();
                hourlyStats.put(String.format("%02d:00", hour), hourlyCount);
            }
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("date", date);
            statistics.put("totalReservations", totalDayReservations);
            statistics.put("activeReservations", activeDayReservations);
            statistics.put("cancelledReservations", cancelledDayReservations);
            statistics.put("hourlyStatistics", hourlyStats);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("%s 일별 예약 통계 조회 성공", date), statistics));
            
        } catch (Exception e) {
            logger.error("일별 예약 통계 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("일별 예약 통계 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 사용자의 예약 목록 조회
     */
    public ResponseEntity<?> getReservationsByUserId(Integer userId) {
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
            List<ReservationEntity> userReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getUser().getId() == userId)
                    .collect(Collectors.toList());
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = userReservations.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("사용자 '%s'의 예약 조회 성공", user.getUsername()));
            response.put("user", user.getUsername());
            response.put("userId", userId);
            response.put("reservations", reservationDTOs);
            response.put("totalCount", reservationDTOs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("사용자별 예약 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("사용자별 예약 조회 중 오류가 발생했습니다."));
        }
    }
} 