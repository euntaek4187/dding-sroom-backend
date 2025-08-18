package com.example.ddingsroom.suggest_post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class SuggestPostDeleteRequestDTO {

    @NotNull(message = "건의 ID는 필수입니다.")
    @JsonProperty("suggest_id")
    private Long suggestId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @JsonProperty("user_id")
    private Long userId;

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

    @Override
    public String toString() {
        return "SuggestPostDeleteRequestDTO{" +
                "suggestId=" + suggestId +
                ", userId=" + userId +
                '}';
    }
}
