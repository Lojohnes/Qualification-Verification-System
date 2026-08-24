package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.VerificationPurpose;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for QR verification.
 */
public record QrVerificationRequestDto(
    @NotBlank String qrPayload,
    @NotNull VerificationPurpose purpose,
    @Valid ConsentRequestDto consent,
    @Valid EvidenceRequestDto evidence
) {}
