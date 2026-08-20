package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Read-only DTO representing one entry in the {@link com.aqvp.platform.qualification.domain.QualificationStatusHistory}.
 */
public record QualificationStatusHistoryDto(
    UUID id,
    String previousStatus,
    String newStatus,
    String changedBy,
    String reason,
    LocalDateTime changedAt
) {}
