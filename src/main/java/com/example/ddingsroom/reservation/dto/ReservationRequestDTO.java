package com.example.ddingsroom.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    private Long userId;
    private Long roomId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
}