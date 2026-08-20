package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.VerificationChannel;
import com.aqvp.platform.verification.domain.VerificationPurpose;
import com.aqvp.platform.verification.domain.VerificationRequestStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for verification requests.
 */
public record VerificationRequestResponseDto(
    UUID id,
    String requestReference,
    VerificationChannel channel,
    VerificationPurpose purpose,
    VerificationRequestStatus status,
    ConsentStatus consentStatus,
    LocalDateTime expiresAt,
    LocalDateTime completedAt,
    VerificationResultSummaryDto latestResult,
    LocalDateTime createdAt
) {}
