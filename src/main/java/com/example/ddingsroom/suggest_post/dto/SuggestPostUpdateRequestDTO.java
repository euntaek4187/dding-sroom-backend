package com.example.ddingsroom.suggest_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostUpdateRequestDTO {

    @NotNull(message = "건의 ID는 필수입니다.")
    @JsonProperty("suggest_id")
    private Long suggestId;

    @NotBlank(message = "건의 제목은 필수입니다.")
    @Size(max = 255, message = "건의 제목은 255자를 초과할 수 없습니다.")
    @JsonProperty("suggest_title")
    private String suggestTitle;

    @NotBlank(message = "건의 내용은 필수입니다.")
    @JsonProperty("suggest_content")
    private String suggestContent;

    @NotBlank(message = "카테고리는 필수입니다.")
    @JsonProperty("category")
    private String category;

    @NotBlank(message = "위치는 필수입니다.")
    @JsonProperty("location")
    private String location;

    @NotNull(message = "답변 여부는 필수입니다.")
    @JsonProperty("is_answered")
    private Boolean isAnswered;

}
