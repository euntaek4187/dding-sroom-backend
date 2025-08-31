package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.repository.RoomRepository;
import com.example.ddingsroom.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Service
public class AdminSystemService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminSystemService.class);
    
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    
    @Autowired
    public AdminSystemService(UserRepository userRepository,
                             ReservationRepository reservationRepository,
                             RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }
    
    /**
     * 관리자 대시보드 정보 조회
     */
    public ResponseEntity<?> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 기본 통계
            long totalUsers = userRepository.count();
            long totalReservations = reservationRepository.count();
            long totalRooms = roomRepository.count();
            
            // 오늘 통계
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            long todayReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getCreatedAt().isAfter(todayStart) && 
                               r.getCreatedAt().isBefore(todayEnd))
                    .count();
            
            // 활성 예약 수
            long activeReservations = reservationRepository.findAll().stream()
                    .filter(r -> "RESERVED".equals(r.getStatus()))
                    .count();
            
            // 시스템 상태
            Map<String, Object> systemStatus = new HashMap<>();
            systemStatus.put("database", "healthy");
            systemStatus.put("server", "running");
            systemStatus.put("lastUpdate", LocalDateTime.now());
            
            dashboard.put("totalUsers", totalUsers);
            dashboard.put("totalReservations", totalReservations);
            dashboard.put("totalRooms", totalRooms);
            dashboard.put("todayReservations", todayReservations);
            dashboard.put("activeReservations", activeReservations);
            dashboard.put("systemStatus", systemStatus);
            
            return ResponseEntity.ok(AdminResponseDTO.success("대시보드 조회 성공", dashboard));
            
        } catch (Exception e) {
            logger.error("대시보드 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("대시보드 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 전체 시스템 통계 조회
     */
    public ResponseEntity<?> getSystemStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 사용자 통계
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("total", userRepository.count());
            userStats.put("admins", userRepository.findAll().stream()
                    .filter(u -> "ROLE_ADMIN".equals(u.getRole())).count());
            userStats.put("users", userRepository.findAll().stream()
                    .filter(u -> "ROLE_USER".equals(u.getRole())).count());
            userStats.put("active", userRepository.findAll().stream()
                    .filter(u -> "normal".equals(u.getState())).count());
            userStats.put("blocked", userRepository.findAll().stream()
                    .filter(u -> "blocked".equals(u.getState())).count());
            
            // 예약 통계
            Map<String, Object> reservationStats = new HashMap<>();
            reservationStats.put("total", reservationRepository.count());
            reservationStats.put("active", reservationRepository.findAll().stream()
                    .filter(r -> "RESERVED".equals(r.getStatus())).count());
            reservationStats.put("cancelled", reservationRepository.findAll().stream()
                    .filter(r -> "CANCELLED".equals(r.getStatus())).count());
            
            // 스터디룸 통계
            Map<String, Object> roomStats = new HashMap<>();
            roomStats.put("total", roomRepository.count());
            roomStats.put("idle", roomRepository.findAll().stream()
                    .filter(r -> "IDLE".equals(r.getRoomStatus())).count());
            roomStats.put("occupied", roomRepository.findAll().stream()
                    .filter(r -> "OCCUPIED".equals(r.getRoomStatus())).count());
            roomStats.put("maintenance", roomRepository.findAll().stream()
                    .filter(r -> "MAINTENANCE".equals(r.getRoomStatus())).count());
            
            statistics.put("users", userStats);
            statistics.put("reservations", reservationStats);
            statistics.put("rooms", roomStats);
            statistics.put("generatedAt", LocalDateTime.now());
            
            return ResponseEntity.ok(AdminResponseDTO.success("시스템 통계 조회 성공", statistics));
            
        } catch (Exception e) {
            logger.error("시스템 통계 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("시스템 통계 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 시스템 상태 확인
     */
    public ResponseEntity<?> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // 데이터베이스 연결 확인
            try {
                userRepository.count(); // 단순 카운트로 DB 연결 확인
                health.put("database", "healthy");
            } catch (Exception e) {
                health.put("database", "error");
                logger.error("데이터베이스 연결 오류: {}", e.getMessage());
            }
            
            // 메모리 사용량
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            Map<String, Object> memoryInfo = new HashMap<>();
            memoryInfo.put("total", totalMemory);
            memoryInfo.put("used", usedMemory);
            memoryInfo.put("free", freeMemory);
            memoryInfo.put("usagePercentage", (double) usedMemory / totalMemory * 100);
            
            health.put("memory", memoryInfo);
            health.put("server", "running");
            health.put("uptime", System.currentTimeMillis());
            health.put("checkTime", LocalDateTime.now());
            
            return ResponseEntity.ok(AdminResponseDTO.success("시스템 상태 확인 완료", health));
            
        } catch (Exception e) {
            logger.error("시스템 상태 확인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("시스템 상태 확인 중 오류가 발생했습니다."));
        }
    }
} 