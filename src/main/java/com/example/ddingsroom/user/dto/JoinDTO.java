package com.example.ddingsroom.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JoinDTO {
    private String email;
    private String username;
    private String password;
    private String age;
    private String studentNumber;
}
