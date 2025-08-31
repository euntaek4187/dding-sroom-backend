package com.example.ddingsroom.community_post_comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponseDTO {

    private String message;
    private String error;
    private Object data;

    public static BaseResponseDTO success(String message) {
        return BaseResponseDTO.builder()
                .message(message)
                .build();
    }

    public static BaseResponseDTO success(String message, Object data) {
        return BaseResponseDTO.builder()
                .message(message)
                .data(data)
                .build();
    }

    public static BaseResponseDTO error(String error) {
        return BaseResponseDTO.builder()
                .error(error)
                .build();
    }
}
