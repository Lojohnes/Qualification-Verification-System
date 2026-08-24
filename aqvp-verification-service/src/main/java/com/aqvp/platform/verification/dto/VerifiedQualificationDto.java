package com.aqvp.platform.verification.dto;

import java.time.LocalDateTime;

/**
 * Redacted qualification details returned after verification.
 */
public record VerifiedQualificationDto(
    String qualificationNumber,
    String qualificationName,
    String qualificationType,
    String classification,
    Integer yearOfAward,
    String status,
    LocalDateTime issuedAt,
    String institutionName
) {}
