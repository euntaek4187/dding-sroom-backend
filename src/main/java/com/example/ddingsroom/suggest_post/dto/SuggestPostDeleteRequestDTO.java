package com.example.ddingsroom.suggest_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuggestPostDeleteRequestDTO {
//    suggest_id 필드는 json이 아닌 url 경로에서 받으므로 이 부분 제거
//    @NotNull(message = "건의 ID는 필수입니다.")
//    @JsonProperty("suggest_id")
//    private Long suggestId;

}
