package com.example.ddingsroom.suggest_post_comment.dto;

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
public class SuggestCommentDTO {

    @NotNull(message = "건의 게시물 ID는 필수입니다.")
    @JsonProperty("suggest_post_id")
    private Long suggestPostId;

    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다.")
    @JsonProperty("answer_content")
    private String answerContent;


}
