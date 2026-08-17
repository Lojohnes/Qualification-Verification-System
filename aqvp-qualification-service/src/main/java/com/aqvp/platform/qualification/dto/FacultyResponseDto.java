package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO record representing a {@link com.aqvp.platform.qualification.domain.Faculty} response.
 */
public record FacultyResponseDto(
    UUID id,
    UUID institutionId,
    String institutionName,
    String name,
    String code,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {}
