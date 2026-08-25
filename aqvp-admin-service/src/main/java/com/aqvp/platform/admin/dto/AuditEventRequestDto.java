package com.aqvp.platform.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record AuditEventRequestDto(
    @NotBlank(message = "Event type is required") String eventType,
    @NotBlank(message = "Action is required") String action,
    UUID actorId,
    String actorName,
    String actorRole,
    String organizationId,
    @NotBlank(message = "Resource type is required") String resourceType,
    UUID resourceId,
    String resourceName,
    String previousValues,
    String newValues,
    String ipAddress,
    String deviceInfo
) {
}
