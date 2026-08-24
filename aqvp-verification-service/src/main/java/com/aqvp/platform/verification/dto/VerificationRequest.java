package com.aqvp.platform.verification.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to verify a qualification.
 */
public record VerificationRequest(
        String qualificationNumber,
        String securityIdentifier,
        @NotBlank String method
) {
}
