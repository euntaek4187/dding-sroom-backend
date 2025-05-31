package com.example.ddingsroom.user.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class MyPageDTO {
    private Integer id;
    private String email;
    private String username;
    private String age;
    private String studentNumber;
    private String role;
    private String state;
    private LocalDateTime registrationDate;

    public MyPageDTO() {}

    public MyPageDTO(com.example.ddingsroom.user.entity.UserEntity userEntity) {
        if (userEntity != null) {
            this.id = userEntity.getId();
            this.email = userEntity.getEmail();
            this.username = userEntity.getUsername();
            this.age = userEntity.getAge();
            this.studentNumber = userEntity.getStudentNumber();
            this.role = userEntity.getRole();
            this.state = userEntity.getState();
            this.registrationDate = userEntity.getRegistrationDate();
        }
    }
}