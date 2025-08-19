package com.example.ddingsroom.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDTO {
    private String message;
    private String error;
    private Object data;

    public static BaseResponseDTO success(String message) {
        return new BaseResponseDTO(message, null, null);
    }

    public static BaseResponseDTO success(String message, Object data) {
        return new BaseResponseDTO(message, null, data);
    }

    public static BaseResponseDTO error(String error) {
        return new BaseResponseDTO(null, error, null);
    }
}