package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO record representing a {@link com.aqvp.platform.qualification.domain.Department} response.
 */
public record DepartmentResponseDto(
    UUID id,
    UUID facultyId,
    String facultyName,
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
