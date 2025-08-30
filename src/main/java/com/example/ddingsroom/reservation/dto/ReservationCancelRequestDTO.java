package com.example.ddingsroom.reservation.dto;

import lombok.Data;

@Data
public class ReservationCancelRequestDTO {
    private Long userId;
    private Long reservationId;
}