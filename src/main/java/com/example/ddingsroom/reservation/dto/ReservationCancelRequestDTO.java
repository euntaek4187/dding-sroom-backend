package com.example.ddingsroom.reservation.dto;

import lombok.Data;

@Data
public class ReservationCancelRequestDTO {
    private int userId;
    private int reservationId;
}