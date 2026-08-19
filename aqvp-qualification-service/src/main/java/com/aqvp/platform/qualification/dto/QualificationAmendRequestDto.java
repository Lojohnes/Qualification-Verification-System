package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for amending an {@code ISSUED} or {@code AMENDED}
 * {@link com.aqvp.platform.qualification.domain.Qualification}.
 *
 * <p>Amending preserves history and transitions the record to {@code AMENDED}.
 */
public record QualificationAmendRequestDto(
    @NotBlank(message = "Reason for amendment is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    String reason,

    @NotNull(message = "Updated qualification name is required")
    @Size(max = 255, message = "Qualification name must not exceed 255 characters")
    String qualificationName,

    @Size(max = 100, message = "Classification must not exceed 100 characters")
    String classification,

    String notes
) {}
