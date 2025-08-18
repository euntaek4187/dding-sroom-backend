package com.example.ddingsroom.suggest_post.dto;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.util.Category;
import com.example.ddingsroom.suggest_post.util.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "user_id",
        "suggest_title",
        "suggest_content",
        "created_at",
        "updated_at",
        "category",
        "location",
        "is_answered"
})
public class SuggestPostResponseDTO {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("suggest_title")
    private String suggestTitle;

    @JsonProperty("suggest_content")
    private String suggestContent;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private String category;
    private String location;

    @JsonProperty("is_answered")
    private boolean isAnswered;

    public SuggestPostResponseDTO(SuggestPostEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.suggestTitle = entity.getSuggestTitle();
        this.suggestContent = entity.getSuggestContent();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.category = Category.fromValue(entity.getCategory()).getName();
        this.location = Location.fromValue(entity.getLocation()).getName();
        this.isAnswered = entity.isAnswered();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSuggestTitle() {
        return suggestTitle;
    }

    public String getSuggestContent() {
        return suggestContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    @JsonProperty("is_answered")
    public boolean isAnswered() {
        return isAnswered;
    }
}
