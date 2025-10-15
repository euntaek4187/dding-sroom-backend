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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
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
            UserEntity user;
            try {
                user = userRepository.findById(requestDTO.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
            } catch (EntityNotFoundException e) {
                logger.warn("존재하지 않는 사용자 ID로 예약 시도: userId={}", requestDTO.getUserId());
                return new BaseResponseDTO("해당 사용자가 존재하지 않습니다.");
            }
            
            RoomEntity room;
            try {
                room = roomRepository.findById(requestDTO.getRoomId())
                        .orElseThrow(() -> new EntityNotFoundException("해당 스터디룸이 존재하지 않습니다."));
            } catch (EntityNotFoundException e) {
                logger.warn("존재하지 않는 룸 ID로 예약 시도: roomId={}", requestDTO.getRoomId());
                return new BaseResponseDTO("해당 스터디룸이 존재하지 않습니다.");
            }
            
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

            // 동일 사용자의 동일 시간대 다른 룸 예약 방지 검증
            logger.info("사용자 중복 예약 검증 시작: userId={}, roomId={}, startTime={}, endTime={}", 
                    user.getId(), requestDTO.getRoomId(), requestDTO.getReservationStartTime(), requestDTO.getReservationEndTime());
            
            List<ReservationEntity> userOverlappingReservations;
            try {
                userOverlappingReservations = reservationRepository.findUserOverlappingReservations(
                        user,
                        requestDTO.getReservationStartTime(),
                        requestDTO.getReservationEndTime());
                
                logger.info("사용자 중복 예약 검증 결과: 겹치는 예약 개수={}", userOverlappingReservations.size());
                
                if (!userOverlappingReservations.isEmpty()) {
                    for (ReservationEntity existing : userOverlappingReservations) {
                        logger.warn("기존 예약 발견: 예약ID={}, 룸ID={}, 시작시간={}, 종료시간={}", 
                                existing.getId(), existing.getRoom().getId(), existing.getStartTime(), existing.getEndTime());
                    }
                    logger.warn("동일 시간대 다른 룸 예약 시도 감지: userId={}, 기존예약룸={}, 신규예약룸={}",
                            user.getId(), userOverlappingReservations.get(0).getRoom().getId(), requestDTO.getRoomId());
                    return new BaseResponseDTO("동일한 시간대에 다른 스터디룸 예약이 불가능합니다.");
                }
            } catch (Exception e) {
                logger.error("사용자 중복 예약 검증 실패: {}", e.getMessage(), e);
                return new BaseResponseDTO("예약 시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.");
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
            
            // ===== 추가 로직: 하루 2시간 예약 제한 검증 =====
            // 이 로직은 서비스 담당자 요청으로 추가했음. 이후 삭제될 가능성도 있음..
            // 새로 예약하려는 날짜 추출
            LocalDate reservationDate = requestDTO.getReservationStartTime().toLocalDate();
            
            // 해당 날짜의 시작과 끝시간 계산
            LocalDateTime startOfDay = reservationDate.atStartOfDay();
            LocalDateTime endOfDay = reservationDate.plusDays(1).atStartOfDay();
            
            // 해당 날짜의 기존 예약들 조회 (취소되지 않은 예약만)
            List<ReservationEntity> existingReservations = reservationRepository.findReservationsByUserAndDate(user, startOfDay, endOfDay);
            
            logger.info("하루 2시간 제한 검증: userId={}, 날짜={}, 기존예약수={}", 
                    user.getId(), reservationDate, existingReservations.size());
            
            // 기존 예약 시간 합계 계산
            long existingTotalMinutes = 0;
            for (ReservationEntity existing : existingReservations) {
                Duration duration = Duration.between(existing.getStartTime(), existing.getEndTime());
                long minutes = duration.toMinutes();
                existingTotalMinutes += minutes;
                logger.info("기존예약: ID={}, 시작={}, 종료={}, 시간={}분", 
                        existing.getId(), existing.getStartTime(), existing.getEndTime(), minutes);
            }
            
            // 새로운 예약 시간 계산
            Duration newReservationDuration = Duration.between(requestDTO.getReservationStartTime(), requestDTO.getReservationEndTime());
            long newReservationMinutes = newReservationDuration.toMinutes();
            
            // 총 예약 시간 계산 (기존 + 신규)
            long totalMinutes = existingTotalMinutes + newReservationMinutes;
            long totalHours = totalMinutes / 60;
            
            logger.info("시간 계산: 기존={}분, 신규={}분, 총합={}분({}시간)", 
                    existingTotalMinutes, newReservationMinutes, totalMinutes, totalHours);
            
            // 2시간 제한 검증
            if (totalMinutes > 120) {
                long remainingMinutes = 120 - existingTotalMinutes;
                long remainingHours = remainingMinutes / 60;
                
                if (remainingMinutes <= 0) {
                    return new BaseResponseDTO("사용자별 예약은 하루 2시간으로 제한됩니다.(남은 시간 없음)");
                } else if (remainingHours >= 1) {
                    return new BaseResponseDTO("사용자별 예약은 하루 2시간으로 제한됩니다.(남은 시간: " + remainingHours + "시간)");
                } else {
                    return new BaseResponseDTO("사용자별 예약은 하루 2시간으로 제한됩니다.(남은 시간: " + remainingMinutes + "분)");
                }
            }
            // ===== 하루 2시간 예약 제한 검증 끝.. 나중에 없어질 수도 =====
            
            ReservationEntity reservation = new ReservationEntity();
            reservation.setUser(user);
            reservation.setRoom(room);
            reservation.setStartTime(requestDTO.getReservationStartTime());
            reservation.setEndTime(requestDTO.getReservationEndTime());
            reservation.setStatus("RESERVED");
            
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

            if (!reservation.getUser().getId().equals(requestDTO.getUserId())) {
                return new BaseResponseDTO("본인의 예약만 취소할 수 있습니다.");
            }
            
            if ("CANCELLED".equals(reservation.getStatus())) {
                return new BaseResponseDTO("이미 취소된 예약입니다.");
            }
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = reservation.getStartTime();
            LocalDateTime endTime = reservation.getEndTime();
            
            // 예약 시작 후 이용중이거나 종료된 예약 취소 방지
            if (now.isAfter(startTime) && now.isBefore(endTime)) {
                return new BaseResponseDTO("이용 중인 예약은 취소할 수 없습니다.");
            }
            
            if (now.isAfter(endTime)) {
                return new BaseResponseDTO("이용이 완료된 예약은 취소할 수 없습니다.");
            }
            
            reservation.setStatus("CANCELLED");
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
    public ReservationResponseDTO getUserReservations(Long userId) {
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

    @Transactional(readOnly = true)
    public BaseResponseDTO getCurrentCrowdingLevel() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            long activeReservationsCount = reservationRepository.countActiveReservationsAtTime(currentTime);
            
            logger.info("실시간 혼잡도 조회: 현재시간={}, 예약된룸수={}", currentTime, activeReservationsCount);
            
            return new BaseResponseDTO(String.valueOf(activeReservationsCount));
        } catch (Exception e) {
            logger.error("실시간 혼잡도 조회 중 오류 발생: {}", e.getMessage(), e);
            return new BaseResponseDTO("혼잡도 조회 중 오류가 발생했습니다.");
        }
    }

}