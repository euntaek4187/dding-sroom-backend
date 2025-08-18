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
@AllArgsConstructor
public class SuggestPostDeleteRequestDTO {

    @NotNull(message = "건의 ID는 필수입니다.")
    @JsonProperty("suggest_id")
    private Long suggestId;

}
