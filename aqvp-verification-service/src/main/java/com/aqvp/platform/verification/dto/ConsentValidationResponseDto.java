package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Result of consent validation.
 */
public record ConsentValidationResponseDto(
    UUID verificationRequestId,
    ConsentStatus status,
    ConsentScope scope,
    LocalDateTime validatedAt,
    String failureReason
) {}
