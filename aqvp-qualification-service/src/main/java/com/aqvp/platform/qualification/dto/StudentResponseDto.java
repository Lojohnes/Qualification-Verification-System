package com.aqvp.platform.qualification.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a {@link com.aqvp.platform.qualification.domain.Student}.
 */
public record StudentResponseDto(
    UUID id,
    String studentNumber,
    String firstName,
    String lastName,
    String email,
    LocalDate dateOfBirth,
    String nationalId,
    UUID institutionId,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {}
