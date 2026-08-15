package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO record representing a {@link com.aqvp.platform.qualification.domain.Program} response.
 */
public record ProgramResponseDto(
    UUID id,
    UUID institutionId,
    String institutionName,
    UUID departmentId,
    String departmentName,
    String name,
    String code,
    String degreeLevel,
    Integer durationSemesters,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {}
