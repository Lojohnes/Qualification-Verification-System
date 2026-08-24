package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.VerificationConfidence;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Compact result summary embedded in request responses.
 */
public record VerificationResultSummaryDto(
    UUID id,
    VerificationOutcome outcome,
    VerificationConfidence confidence,
    Integer matchScore,
    LocalDateTime verifiedAt
) {}
