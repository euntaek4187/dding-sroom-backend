package com.example.ddingsroom.reservation.repository;
import com.example.ddingsroom.reservation.entity.ReservationEntity;
import com.example.ddingsroom.reservation.entity.RoomEntity;
import com.example.ddingsroom.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
    // 시간 범위가 겹치는 예약 검색
    @Query("SELECT r FROM ReservationEntity r WHERE r.room = :room AND r.status = 'RESERVED' " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    List<ReservationEntity> findOverlappingReservations(
            @Param("room") RoomEntity room,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 같은 방에서 연속된 시간대 예약 검색
    @Query("SELECT r FROM ReservationEntity r WHERE r.user = :user AND r.status = 'RESERVED' " +
            "AND r.room.id = :roomId AND r.endTime = :startTime")
    List<ReservationEntity> findContinuousReservations(
            @Param("user") UserEntity user,
            @Param("roomId") int roomId,
            @Param("startTime") LocalDateTime startTime);

    // 동일 사용자의 동일 시간대 다른 룸 예약 검색
    @Query("SELECT r FROM ReservationEntity r WHERE r.user = :user AND r.status = 'RESERVED' " +
           "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    List<ReservationEntity> findUserOverlappingReservations(
            @Param("user") UserEntity user,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 특정 시간에 예약된 룸 개수 조회 (실시간 혼잡도용)
    @Query("SELECT COUNT(DISTINCT r.room.id) FROM ReservationEntity r WHERE r.status = 'RESERVED' " +
           "AND r.startTime <= :currentTime AND r.endTime > :currentTime")
    long countActiveReservationsAtTime(@Param("currentTime") LocalDateTime currentTime);

    List<ReservationEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    // 특정 날짜의 사용자 예약 시간 합계 조회 (하루 2시간 제한용)
    @Query("SELECT r FROM ReservationEntity r WHERE r.user = :user AND r.status = 'RESERVED' " +
           "AND r.startTime >= :startOfDay AND r.startTime < :endOfDay")
    List<ReservationEntity> findReservationsByUserAndDate(
            @Param("user") UserEntity user, 
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Transactional
    void deleteByUser(UserEntity user);
}