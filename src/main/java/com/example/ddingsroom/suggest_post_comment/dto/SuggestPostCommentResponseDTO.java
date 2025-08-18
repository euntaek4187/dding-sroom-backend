package com.example.ddingsroom.suggest_post_comment.dto;

import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostCommentResponseDTO {
    private Long id;

    @JsonProperty("suggest_post_id")
    private Long suggestPostId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("answer_content")
    private String answerContent;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public SuggestPostCommentResponseDTO(SuggestPostCommentEntity entity) {
        this.id = entity.getId();
        this.suggestPostId = entity.getSuggestPost().getId();
        this.userId = entity.getUserId();
        this.answerContent = entity.getAnswerContent();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

}
