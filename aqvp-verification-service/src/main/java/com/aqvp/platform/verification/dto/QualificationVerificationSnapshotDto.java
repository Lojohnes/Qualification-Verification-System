package com.aqvp.platform.verification.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Client-side mirror of Qualification service's verification snapshot contract.
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

    public record StudentSnapshot(
        UUID studentId,
        String studentNumber,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
    ) {}

    public record InstitutionSnapshot(
        UUID institutionId,
        String name,
        String code
    ) {}
}
