package com.aqvp.platform.verification.dto;

import java.time.LocalDate;

/**
 * Holder details submitted by a verifier.
 */
public record SubjectDto(
    String holderFirstName,
    String holderLastName,
    LocalDate dateOfBirth,
    String studentNumber
) {}
