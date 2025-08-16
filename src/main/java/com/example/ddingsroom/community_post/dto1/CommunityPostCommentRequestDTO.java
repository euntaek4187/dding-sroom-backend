package com.example.ddingsroom.community_post.dto1;

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
}
