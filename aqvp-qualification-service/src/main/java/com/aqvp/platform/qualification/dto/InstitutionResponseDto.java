package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO record representing an {@link com.aqvp.platform.qualification.domain.Institution} response.
 */
public record InstitutionResponseDto(
    UUID id,
    String name,
    String code,
    String description,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {}
