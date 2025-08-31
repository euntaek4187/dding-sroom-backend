package com.example.ddingsroom.community_post_comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostCommentResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("comment_content")
    private String commentContent;

    @JsonProperty("parent_comment_id")
    private Long parentCommentId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("message")
    private String message;

    // 대댓글 목록
    @JsonProperty("replies")
    private List<CommunityPostCommentResponseDTO> replies;

    // 댓글 타입 구분 (일반댓글/대댓글)
    @JsonProperty("comment_type")
    private String commentType; // "COMMENT" 또는 "REPLY"

    // 대댓글 개수
    @JsonProperty("reply_count")
    private Long replyCount;
}
