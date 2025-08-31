package com.example.ddingsroom.community_post.dto;

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
    private Object data;  // 데이터 필드 추가!

    // 성공 응답 (메시지만)
    public static BaseResponseDTO success(String message) {
        return BaseResponseDTO.builder()
                .message(message)
                .build();
    }

    // 성공 응답 (데이터 포함)
    public static BaseResponseDTO success(String message, Object data) {
        return BaseResponseDTO.builder()
                .message(message)
                .data(data)
                .build();
    }

    // 오류 응답
    public static BaseResponseDTO error(String error) {
        return BaseResponseDTO.builder()
                .error(error)
                .build();
    }
}
