package com.example.ddingsroom.suggest_post_image.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostImageDeleteRequestDTO {
    @NotNull(message = "이미지 ID는 필수입니다.")
    @JsonProperty("image_id")
    private Long imageId;
}
