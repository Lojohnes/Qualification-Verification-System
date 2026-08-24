package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.VerificationConfidence;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for QR verification outcomes.
 */
public record VerificationResultResponseDto(
    UUID verificationRequestId,
    UUID resultId,
    VerificationOutcome outcome,
    VerificationConfidence confidence,
    Integer matchScore,
    VerifiedQualificationDto qualification,
    VerifiedHolderDto holder,
    List<MatchDetailDto> matchDetails,
    LocalDateTime verifiedAt
) {}
