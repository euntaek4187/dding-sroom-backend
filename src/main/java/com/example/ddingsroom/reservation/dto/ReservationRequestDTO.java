package com.example.ddingsroom.reservation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    // userId는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.
    private Long roomId;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
}