package com.aqvp.platform.admin.dto;

import com.aqvp.platform.admin.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(
    @NotBlank(message = "Template code is required") String templateCode,
    @NotNull(message = "Channel is required") NotificationChannel channel,
    @NotBlank(message = "Recipient is required") String recipient,
    String subject,
    @NotBlank(message = "Message is required") String message
) {
}
