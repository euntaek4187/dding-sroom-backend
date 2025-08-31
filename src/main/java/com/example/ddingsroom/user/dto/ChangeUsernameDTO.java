package com.example.ddingsroom.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeUsernameDTO {
    private Long userId;
    private String newUsername;

    public ChangeUsernameDTO() {}

    public ChangeUsernameDTO(Long userId, String newUsername) {
        this.userId = userId;
        this.newUsername = newUsername;
    }

}