package com.example.ddingsroom.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private String message;
    private boolean success;
    private Object data;
    
    public AdminResponseDTO(String message) {
        this.message = message;
        this.success = true;
    }
    
    public AdminResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public static AdminResponseDTO success(String message) {
        return new AdminResponseDTO(message, true);
    }
    
    public static AdminResponseDTO success(String message, Object data) {
        return new AdminResponseDTO(message, true, data);
    }
    
    public static AdminResponseDTO error(String message) {
        return new AdminResponseDTO(message, false);
    }
} 