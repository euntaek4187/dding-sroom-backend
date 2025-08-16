package com.example.ddingsroom.community_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostRequestDTO {

    @JsonProperty("post_id")
    private Long postId;        // update, delete용

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("category")
    private Integer category;   // API 문서에서 "..."로 되어있지만 테이블에서 int이므로 Integer 사용
}
