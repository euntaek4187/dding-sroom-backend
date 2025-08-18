package com.example.ddingsroom.suggest_post_image.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class SuggestPostImageUploadRequestDTO {
    @NotNull(message = "건의 게시물 ID는 필수입니다.")
    @JsonProperty("suggest_post_id")
    private Long suggestPostId;
}
