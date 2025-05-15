package com.example.ddingsroom.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    private int userId;
    private int roomId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
}