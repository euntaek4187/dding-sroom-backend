package com.example.ddingsroom.reservation.service;
import com.example.ddingsroom.reservation.dto.BaseResponseDTO;
import com.example.ddingsroom.reservation.dto.ReservationCancelRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationRequestDTO;
import com.example.ddingsroom.reservation.dto.ReservationResponseDTO;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.reservation.repository.ReservationRepository;
import com.example.ddingsroom.reservation.repository.RoomRepository;
import com.example.ddingsroom.reservation.util.ReservationTimeValidator;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, 
                             RoomRepository roomRepository, 
                             UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BaseResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        try {
            UserEntity user = userRepository.findById(requestDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            
            RoomEntity room = roomRepository.findById(requestDTO.getRoomId())
                    .orElseThrow(() -> new EntityNotFoundException("스터디룸을 찾을 수 없습니다."));
            
            if (!"IDLE".equals(room.getRoomStatus())) {
                return new BaseResponseDTO("현재 사용이 불가능한 스터디룸입니다.");
            }
            
            if (!ReservationTimeValidator.isValidReservationDuration(
                    requestDTO.getReservationStartTime(), requestDTO.getReservationEndTime())) {
                return new BaseResponseDTO("예약은 1시간 또는 2시간 단위로만 가능합니다.");
            }

            if (requestDTO.getReservationStartTime().isBefore(LocalDateTime.now())) {
                return new BaseResponseDTO("과거 시간으로 예약할 수 없습니다.");
            }
    
            List<ReservationEntity> overlappingReservations;
            try {
                overlappingReservations = reservationRepository.findOverlappingReservations(
                        room, 
                        requestDTO.getReservationStartTime(),
                        requestDTO.getReservationEndTime());
                
                if (!overlappingReservations.isEmpty()) {
                    logger.warn("겹치는 예약이 발견됨: {}", overlappingReservations);
                    return new BaseResponseDTO("현재 사용이 불가능한 스터디룸입니다.");
                }
            } catch (Exception e) {
                logger.error("시간 겹침 검증 실패: {}", e.getMessage(), e);
                return new BaseResponseDTO("예약 시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            }
            
            List<ReservationEntity> continuousReservations;
            try {
                continuousReservations = reservationRepository.findContinuousReservations(
                        user,
                        requestDTO.getRoomId(),
                        requestDTO.getReservationStartTime());
                
                if (!continuousReservations.isEmpty()) {
                    logger.warn("연속된 예약 시도 감지: userId={}, roomId={}, 이전예약종료={}, 현재예약시작={}",
                            user.getId(), requestDTO.getRoomId(), 
                            continuousReservations.get(0).getEndTime(), requestDTO.getReservationStartTime());
                    return new BaseResponseDTO("같은 스터디룸에서 연속된 시간 예약은 불가능합니다.");
                }
            } catch (Exception e) {
                logger.error("연속 예약 검증 실패: {}", e.getMessage(), e);
                return new BaseResponseDTO("예약 시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            }
            
            ReservationEntity reservation = new ReservationEntity();
            reservation.setUser(user);
            reservation.setRoom(room);
            reservation.setStartTime(requestDTO.getReservationStartTime());
            reservation.setEndTime(requestDTO.getReservationEndTime());
            reservation.setStatus("RESERVED");
            
            LocalDateTime now = LocalDateTime.now();
            reservation.setCreatedAt(now);
            reservation.setUpdatedAt(now);
            
            reservationRepository.save(reservation);
            
            return new BaseResponseDTO("예약이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            logger.error("예약 생성 중 오류 발생: {}", e.getMessage(), e);
            return new BaseResponseDTO("예약 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }
    
    @Transactional
    public BaseResponseDTO cancelReservation(ReservationCancelRequestDTO requestDTO) {
        try {
            ReservationEntity reservation = reservationRepository.findById(requestDTO.getReservationId())
                    .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다."));
            
            if (reservation.getUser().getId() != requestDTO.getUserId()) {
                return new BaseResponseDTO("본인의 예약만 취소할 수 있습니다.");
            }
            
            if ("CANCELLED".equals(reservation.getStatus())) {
                return new BaseResponseDTO("이미 취소된 예약입니다.");
            }
            
            reservation.setStatus("CANCELLED");
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
            
            return new BaseResponseDTO("예약취소가 잘 되었습니다.");
        } catch (EntityNotFoundException e) {
            logger.warn("예약 취소 중 엔티티 찾을 수 없음: {}", e.getMessage());
            return new BaseResponseDTO(e.getMessage());
        } catch (Exception e) {
            logger.error("예약 취소 중 오류 발생: {}", e.getMessage(), e);
            return new BaseResponseDTO("예약 취소 중 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }
    
    @Transactional(readOnly = true)
    public ReservationResponseDTO getUserReservations(int userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            Pageable pageable = PageRequest.of(0, 20);
            List<ReservationEntity> reservationEntities = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
            
            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = reservationEntities.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());
            
            ReservationResponseDTO responseDTO = new ReservationResponseDTO();
            responseDTO.setMessage("데이터 조회가 성공적으로 진행되었습니다.");
            responseDTO.setReservations(reservationDTOs);
            
            return responseDTO;
        } catch (EntityNotFoundException e) {
            logger.warn("예약 조회 중 사용자 찾을 수 없음: {}", e.getMessage());
            ReservationResponseDTO responseDTO = new ReservationResponseDTO();
            responseDTO.setMessage(e.getMessage());
            responseDTO.setReservations(List.of());
            return responseDTO;
        } catch (Exception e) {
            logger.error("예약 조회 중 오류 발생: {}", e.getMessage(), e);
            ReservationResponseDTO responseDTO = new ReservationResponseDTO();
            responseDTO.setMessage("예약 조회 중 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            responseDTO.setReservations(List.of());
            return responseDTO;
        }
    }
    @Transactional(readOnly = true)
    public ReservationResponseDTO getAllReservations() {
        try {
            List<ReservationEntity> reservationEntities = reservationRepository.findAll();

            List<ReservationResponseDTO.ReservationDTO> reservationDTOs = reservationEntities.stream()
                    .map(ReservationResponseDTO.ReservationDTO::fromEntity)
                    .collect(Collectors.toList());

            ReservationResponseDTO responseDTO = new ReservationResponseDTO();
            responseDTO.setMessage("데이터 조회가 성공적으로 진행되었습니다.");
            responseDTO.setReservations(reservationDTOs);

            return responseDTO;
        } catch (Exception e) {
            logger.error("전체 예약 조회 중 오류 발생: {}", e.getMessage(), e);
            ReservationResponseDTO responseDTO = new ReservationResponseDTO();
            responseDTO.setMessage("예약 조회 중 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            responseDTO.setReservations(List.of());
            return responseDTO;
        }
    }

}