package com.example.ddingsroom.reservation.dto;

import com.example.ddingsroom.reservation.entity.ReservationEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationResponseDTO {
    private String message;
    private List<ReservationDTO> reservations;

    @Data
    public static class ReservationDTO {
        private int id;
        private int userId;
        private int roomId;
        private String roomName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReservationDTO fromEntity(ReservationEntity entity) {
            ReservationDTO dto = new ReservationDTO();
            dto.setId(entity.getId());
            dto.setUserId(entity.getUser().getId());
            dto.setRoomId(entity.getRoom().getId());
            dto.setRoomName(entity.getRoom().getRoomName());
            dto.setStartTime(entity.getStartTime());
            dto.setEndTime(entity.getEndTime());
            dto.setStatus(entity.getStatus());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());
            return dto;
        }
    }
}