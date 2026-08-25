package com.aqvp.platform.admin.controller;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.dto.NotificationRequestDto;
import com.aqvp.platform.admin.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "admin-notifications", description = "Notification dispatch endpoints")
public class AdminNotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send a notification", description = "Dispatch an email or SMS notification through the provider abstraction.")
    @ApiResponse(responseCode = "201", description = "Notification created and sent")
    @PostMapping("/notifications")
    public ResponseEntity<Notification> sendNotification(@Valid @RequestBody NotificationRequestDto dto) {
        final Notification created = notificationService.sendNotification(
            dto.templateCode(),
            dto.channel(),
            dto.recipient(),
            dto.subject(),
            dto.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
