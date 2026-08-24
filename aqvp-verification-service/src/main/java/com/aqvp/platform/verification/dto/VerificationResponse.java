package com.aqvp.platform.verification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Result of a qualification verification.
 */
public record VerificationResponse(
        String status,
        String message,
        String qualificationNumber,
        UUID qualificationId,
        String qualificationName,
        String classification,
        Integer yearOfAward,
        String issuedAt,
        String verifiedAt
) {
}
