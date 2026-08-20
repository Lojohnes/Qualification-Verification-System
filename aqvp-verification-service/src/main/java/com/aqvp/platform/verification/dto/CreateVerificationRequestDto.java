package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.VerificationChannel;
import com.aqvp.platform.verification.domain.VerificationPurpose;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for creating a tracked verification request.
 */
public record CreateVerificationRequestDto(
    @NotNull VerificationChannel channel,
    @NotNull VerificationPurpose purpose,
    @Valid SubjectDto subject,
    @Valid ConsentRequestDto consent,
    @Valid EvidenceRequestDto evidence
) {}
