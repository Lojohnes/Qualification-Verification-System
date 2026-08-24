package com.aqvp.platform.verification.dto;

import java.util.UUID;

/**
 * Structured evidence supplied by the verifier for comparison.
 */
public record EvidenceRequestDto(
    String qualificationNumber,
    String studentNumber,
    String holderFirstName,
    String holderLastName,
    Integer yearOfAward,
    String qualificationName,
    UUID institutionId,
    String institutionName
) {}
