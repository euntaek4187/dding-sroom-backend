package com.example.ddingsroom.admin.service;

import com.example.ddingsroom.admin.dto.AdminResponseDTO;
import com.example.ddingsroom.admin.dto.AdminRoomRequestDTO;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.repository.RoomRepository;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminRoomService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminRoomService.class);
    
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    
    @Autowired
    public AdminRoomService(RoomRepository roomRepository, 
                           ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }
    
    /**
     * 모든 스터디룸 목록 조회
     */
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomEntity> rooms = roomRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "스터디룸 목록 조회 성공");
            response.put("rooms", rooms);
            response.put("totalCount", rooms.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("스터디룸 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 스터디룸 상세 조회
     */
    public ResponseEntity<?> getRoomById(Integer roomId) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 스터디룸 ID를 입력해주세요."));
            }
            
            Optional<RoomEntity> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 스터디룸을 찾을 수 없습니다."));
            }
            
            return ResponseEntity.ok(AdminResponseDTO.success("스터디룸 조회 성공", roomOptional.get()));
            
        } catch (Exception e) {
            logger.error("스터디룸 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 새 스터디룸 생성
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> createRoom(AdminRoomRequestDTO roomRequestDTO) {
        try {
            if (!StringUtils.hasText(roomRequestDTO.getRoomName())) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("스터디룸 이름을 입력해주세요."));
            }
            
            // 중복 이름 체크
            List<RoomEntity> existingRooms = roomRepository.findAll();
            boolean duplicateName = existingRooms.stream()
                    .anyMatch(room -> room.getRoomName().equals(roomRequestDTO.getRoomName()));
            
            if (duplicateName) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("이미 존재하는 스터디룸 이름입니다."));
            }
            
            // 상태 유효성 검증
            String status = roomRequestDTO.getRoomStatus();
            if (!isValidRoomStatus(status)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 상태를 입력해주세요. (IDLE/OCCUPIED/MAINTENANCE)"));
            }
            
            RoomEntity newRoom = new RoomEntity();
            newRoom.setRoomName(roomRequestDTO.getRoomName());
            newRoom.setRoomStatus(status);
            
            roomRepository.save(newRoom);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("스터디룸 '%s'가 성공적으로 생성되었습니다.", newRoom.getRoomName())));
            
        } catch (Exception e) {
            logger.error("스터디룸 생성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 생성 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 스터디룸 정보 수정
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> updateRoom(Integer roomId, AdminRoomRequestDTO roomRequestDTO) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 스터디룸 ID를 입력해주세요."));
            }
            
            if (!StringUtils.hasText(roomRequestDTO.getRoomName())) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("스터디룸 이름을 입력해주세요."));
            }
            
            Optional<RoomEntity> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 스터디룸을 찾을 수 없습니다."));
            }
            
            RoomEntity room = roomOptional.get();
            
            // 다른 방과 이름 중복 체크 (자기 자신 제외)
            List<RoomEntity> otherRooms = roomRepository.findAll().stream()
                    .filter(r -> r.getId() != roomId)
                    .collect(Collectors.toList());
            
            boolean duplicateName = otherRooms.stream()
                    .anyMatch(r -> r.getRoomName().equals(roomRequestDTO.getRoomName()));
            
            if (duplicateName) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("이미 존재하는 스터디룸 이름입니다."));
            }
            
            // 상태 유효성 검증
            String status = roomRequestDTO.getRoomStatus();
            if (!isValidRoomStatus(status)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 상태를 입력해주세요. (IDLE/OCCUPIED/MAINTENANCE)"));
            }
            
            room.setRoomName(roomRequestDTO.getRoomName());
            room.setRoomStatus(status);
            roomRepository.save(room);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("스터디룸 '%s'가 성공적으로 수정되었습니다.", room.getRoomName())));
            
        } catch (Exception e) {
            logger.error("스터디룸 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 스터디룸 상태 변경
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> updateRoomStatus(Integer roomId, String status) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 스터디룸 ID를 입력해주세요."));
            }
            
            if (!isValidRoomStatus(status)) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 상태를 입력해주세요. (IDLE/OCCUPIED/MAINTENANCE)"));
            }
            
            Optional<RoomEntity> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 스터디룸을 찾을 수 없습니다."));
            }
            
            RoomEntity room = roomOptional.get();
            room.setRoomStatus(status);
            roomRepository.save(room);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("스터디룸 '%s'의 상태가 '%s'로 변경되었습니다.", 
                            room.getRoomName(), status)));
            
        } catch (Exception e) {
            logger.error("스터디룸 상태 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 상태 변경 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 스터디룸 삭제
     */
    @Transactional
    public ResponseEntity<AdminResponseDTO> deleteRoom(Integer roomId) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 스터디룸 ID를 입력해주세요."));
            }
            
            Optional<RoomEntity> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 스터디룸을 찾을 수 없습니다."));
            }
            
            RoomEntity room = roomOptional.get();
            
            // 해당 방에 활성 예약이 있는지 확인
            List<ReservationEntity> activeReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRoom().getId() == roomId && "RESERVED".equals(r.getStatus()))
                    .collect(Collectors.toList());
            
            if (!activeReservations.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("활성 예약이 있는 스터디룸은 삭제할 수 없습니다."));
            }
            
            String roomName = room.getRoomName();
            roomRepository.delete(room);
            
            return ResponseEntity.ok(AdminResponseDTO.success(
                    String.format("스터디룸 '%s'가 삭제되었습니다.", roomName)));
            
        } catch (Exception e) {
            logger.error("스터디룸 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 스터디룸 통계 정보 조회
     */
    public ResponseEntity<?> getRoomStatistics() {
        try {
            List<RoomEntity> allRooms = roomRepository.findAll();
            long totalRooms = allRooms.size();
            
            long idleRooms = allRooms.stream()
                    .filter(room -> "IDLE".equals(room.getRoomStatus()))
                    .count();
            
            long occupiedRooms = allRooms.stream()
                    .filter(room -> "OCCUPIED".equals(room.getRoomStatus()))
                    .count();
            
            long maintenanceRooms = allRooms.stream()
                    .filter(room -> "MAINTENANCE".equals(room.getRoomStatus()))
                    .count();
            
            // 각 방별 예약 수 통계
            List<ReservationEntity> allReservations = reservationRepository.findAll();
            Map<String, Long> roomReservationCounts = new HashMap<>();
            
            for (RoomEntity room : allRooms) {
                long reservationCount = allReservations.stream()
                        .filter(r -> r.getRoom().getId() == room.getId())
                        .count();
                roomReservationCounts.put(room.getRoomName(), reservationCount);
            }
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalRooms", totalRooms);
            statistics.put("idleRooms", idleRooms);
            statistics.put("occupiedRooms", occupiedRooms);
            statistics.put("maintenanceRooms", maintenanceRooms);
            statistics.put("roomReservationCounts", roomReservationCounts);
            
            return ResponseEntity.ok(AdminResponseDTO.success("스터디룸 통계 조회 성공", statistics));
            
        } catch (Exception e) {
            logger.error("스터디룸 통계 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸 통계 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 스터디룸의 예약 현황 조회
     */
    public ResponseEntity<?> getRoomReservations(Integer roomId) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                        .body(AdminResponseDTO.error("올바른 스터디룸 ID를 입력해주세요."));
            }
            
            Optional<RoomEntity> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AdminResponseDTO.error("해당 스터디룸을 찾을 수 없습니다."));
            }
            
            RoomEntity room = roomOptional.get();
            List<ReservationEntity> roomReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRoom().getId() == roomId)
                    .collect(Collectors.toList());
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = roomReservations.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("스터디룸 '%s'의 예약 현황 조회 성공", room.getRoomName()));
            response.put("room", room);
            response.put("reservations", reservationDTOs);
            response.put("totalCount", reservationDTOs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("스터디룸별 예약 현황 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminResponseDTO.error("스터디룸별 예약 현황 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 스터디룸 상태 유효성 검증
     */
    private boolean isValidRoomStatus(String status) {
        return "IDLE".equals(status) || "OCCUPIED".equals(status) || "MAINTENANCE".equals(status);
    }
} 