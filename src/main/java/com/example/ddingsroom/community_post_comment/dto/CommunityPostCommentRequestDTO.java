package com.example.ddingsroom.community_post_comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostCommentRequestDTO {

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("comment_content")
    private String commentContent;

    // 대댓글을 위한 부모 댓글 ID 추가
    @JsonProperty("parent_comment_id")
    private Long parentCommentId;
}
