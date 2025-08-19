package com.example.ddingsroom.notification.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdateRequestDTO {
    private Integer notificationId;
    private String title;
    private String content;
}