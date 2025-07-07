package com.example.ddingsroom.suggest_post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SuggestPostDTO {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotBlank(message = "건의 제목은 필수입니다.")
    @Size(max = 255, message = "건의 제목은 255자를 초과할 수 없습니다.")
    private String suggestTitle;

    @NotNull(message = "건의 내용은 필수입니다.")
    private String suggestContent;

    @NotBlank(message = "카테고리 선택은 필수입니다.")
    private String category;

    private String location;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSuggestTitle() {
        return suggestTitle;
    }

    public void setSuggestTitle(String suggestTitle) {
        this.suggestTitle = suggestTitle;
    }

    public String getSuggestContent() {
        return suggestContent;
    }

    public void setSuggestContent(String suggestContent) {
        this.suggestContent = suggestContent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "SuggestPostDTO{" +
                "userId=" + userId +
                ", suggestTitle='" + suggestTitle + '\'' +
                ", suggestContent='" + suggestContent + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
