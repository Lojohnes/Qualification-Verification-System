package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for revoking an {@code ISSUED} or {@code AMENDED}
 * {@link com.aqvp.platform.qualification.domain.Qualification}.
 *
 * <p>Revocation is a terminal transition and preserves history.
 */
public record QualificationRevokeRequestDto(
    @NotBlank(message = "Revocation reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    String reason
) {}
