package com.aqvp.platform.admin.controller;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import com.aqvp.platform.admin.service.NotificationService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notifications/send")
    public ResponseEntity<Notification> sendNotification(
        @RequestParam @NotBlank String templateCode,
        @RequestParam NotificationChannel channel,
        @RequestParam @NotBlank String recipient,
        @RequestParam(required = false) String subject,
        @RequestParam @NotBlank String message) {
        return ResponseEntity.ok(notificationService.sendNotification(templateCode, channel, recipient, subject, message));
    }
}
