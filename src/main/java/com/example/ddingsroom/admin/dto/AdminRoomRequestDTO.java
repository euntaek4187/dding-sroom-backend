package com.example.ddingsroom.admin.dto;

import lombok.Data;

@Data
public class AdminRoomRequestDTO {
    private String roomName;
    private String roomStatus;
    
    public AdminRoomRequestDTO() {
        this.roomStatus = "IDLE"; // 기본값
    }
    
    public AdminRoomRequestDTO(String roomName, String roomStatus) {
        this.roomName = roomName;
        this.roomStatus = roomStatus != null ? roomStatus : "IDLE";
    }
} 