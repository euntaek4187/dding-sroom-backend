package com.example.ddingsroom.suggest_post.dto;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.util.Category;
import com.example.ddingsroom.suggest_post.util.Location;

import java.time.LocalDateTime;

public class SuggestPostResponseDTO {
    private Long id;
    private Long userId;
    private String suggestTitle;
    private String suggestContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;
    private String location;
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

    public boolean isAnswered() {
        return isAnswered;
    }
}
