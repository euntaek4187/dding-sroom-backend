package com.example.ddingsroom.notification.controller;

import com.example.ddingsroom.notification.dto.BaseResponseDTO;
import com.example.ddingsroom.notification.dto.NotificationCreateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationUpdateRequestDTO;
import com.example.ddingsroom.notification.dto.NotificationViewCountRequestDTO;
import com.example.ddingsroom.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO> createNotification(@RequestBody NotificationCreateRequestDTO dto) {
        BaseResponseDTO response = notificationService.createNotification(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponseDTO> updateNotification(@RequestBody NotificationUpdateRequestDTO dto) {
        BaseResponseDTO response = notificationService.updateNotification(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponseDTO> deleteNotification(@PathVariable Integer id) {
        BaseResponseDTO response = notificationService.deleteNotification(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponseDTO> getAllNotifications() {
        BaseResponseDTO response = notificationService.getAllNotifications();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> getNotification(@PathVariable Integer id) {
        BaseResponseDTO response = notificationService.getNotification(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/view")
    public ResponseEntity<BaseResponseDTO> incrementViewCount(@RequestBody NotificationViewCountRequestDTO dto) {
        BaseResponseDTO response = notificationService.incrementViewCount(dto.getNotificationId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-count")
    public ResponseEntity<BaseResponseDTO> getRecentNotificationsCount() {
        BaseResponseDTO response = notificationService.getRecentNotificationsCount();
        return ResponseEntity.ok(response);
    }
}