package com.aqvp.platform.verification.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for verifying a QR code under an existing request.
 */
public record QrVerificationForRequestDto(
    @NotBlank String qrPayload,
    @Valid EvidenceRequestDto evidence
) {}
