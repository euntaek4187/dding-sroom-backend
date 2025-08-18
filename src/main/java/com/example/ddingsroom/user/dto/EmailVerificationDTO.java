package com.example.ddingsroom.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDTO {
    private String email;

    public EmailVerificationDTO() {}

    public EmailVerificationDTO(String email) {
        this.email = email;
    }
}