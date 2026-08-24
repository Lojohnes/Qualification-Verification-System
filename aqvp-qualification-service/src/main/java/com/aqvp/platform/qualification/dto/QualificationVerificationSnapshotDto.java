package com.aqvp.platform.qualification.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Minimal authoritative qualification snapshot exposed to the Verification service.
 */
public record QualificationVerificationSnapshotDto(
    UUID qualificationId,
    String securityIdentifier,
    String qualificationNumber,
    String qualificationType,
    String qualificationName,
    String classification,
    Integer yearOfAward,
    String status,
    LocalDateTime issuedAt,
    LocalDateTime revokedAt,
    String revocationReason,
    StudentSnapshot student,
    InstitutionSnapshot institution
) {

    /**
     * Holder fields needed for verification matching and consent checks.
     */
    public record StudentSnapshot(
        UUID studentId,
        String studentNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
    ) {}

    /**
     * Issuer fields needed for verification matching.
     */
    public record InstitutionSnapshot(
        UUID institutionId,
        String name,
        String code
    ) {}
}
