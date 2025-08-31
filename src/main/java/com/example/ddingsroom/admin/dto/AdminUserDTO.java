package com.example.ddingsroom.admin.dto;

import com.example.ddingsroom.user.entity.UserEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserDTO {
    private Long id;
    private String email;
    private String username;
    private String age;
    private String studentNumber;
    private String role;
    private String state;
    private LocalDateTime registrationDate;
    
    public AdminUserDTO() {}
    
    public AdminUserDTO(UserEntity userEntity) {
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
    
    public static AdminUserDTO fromEntity(UserEntity userEntity) {
        return new AdminUserDTO(userEntity);
    }
} 