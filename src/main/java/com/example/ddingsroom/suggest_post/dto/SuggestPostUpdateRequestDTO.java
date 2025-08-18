package com.example.ddingsroom.suggest_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SuggestPostUpdateRequestDTO {

    @NotNull(message = "건의 ID는 필수입니다.")
    @JsonProperty("suggest_id")
    private Long suggestId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Long userId;

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

    public Long getSuggestId() {
        return suggestId;
    }

    public void setSuggestId(Long suggestId) {
        this.suggestId = suggestId;
    }

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

    public Boolean getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(Boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    @Override
    public String toString() {
        return "SuggestPostUpdateRequestDTO{" +
                "suggestId=" + suggestId +
                ", userId=" + userId +
                ", suggestTitle='" + suggestTitle + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", isAnswered=" + isAnswered +
                '}';
    }
}
