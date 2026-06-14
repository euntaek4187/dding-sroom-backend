package com.example.ddingsroom.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeUsernameDTO {
    // userId는 클라이언트가 보내지 않는다. 서버가 JWT 토큰에서 파생한다.
    private String newUsername;

    public ChangeUsernameDTO() {}

    public ChangeUsernameDTO(String newUsername) {
        this.newUsername = newUsername;
    }

}