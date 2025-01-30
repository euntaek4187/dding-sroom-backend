package com.example.ddingsroom.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {
    private String email;
    private String username;
    private String password;
    private String age;
    private String studentNumber;
}