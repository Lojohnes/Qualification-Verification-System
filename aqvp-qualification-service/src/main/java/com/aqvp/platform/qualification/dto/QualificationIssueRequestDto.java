package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for the issue action on a {@link com.aqvp.platform.qualification.domain.Qualification}.
 *
 * <p>Transitions the qualification from {@code DRAFT} to {@code ISSUED} and generates
 * a cryptographically secure security identifier.
 */
public record QualificationIssueRequestDto(
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    String notes
) {}
