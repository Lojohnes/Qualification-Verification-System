package com.aqvp.platform.verification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import com.aqvp.platform.verification.dto.EvidenceRequestDto;
import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VerificationMatchingServiceTest {

    private final VerificationMatchingService service = new VerificationMatchingService();

    @Test
    void matchVerifiesIssuedQualificationWhenEvidenceMatches() {
        final VerificationMatchResult result = service.match(
            new EvidenceRequestDto("Q-1", "S-1", "Amina", "Dube", 2024, "Computer Science", null, "MSU"),
            snapshot("ISSUED"),
            validConsent()
        );

        assertThat(result.outcome()).isEqualTo(VerificationOutcome.VERIFIED);
        assertThat(result.matchScore()).isEqualTo(100);
    }

    @Test
    void matchReturnsMismatchWhenCriticalEvidenceDiffers() {
        final VerificationMatchResult result = service.match(
            new EvidenceRequestDto("WRONG", "BAD", "Jane", "Roe", 1999, "Law", null, "Other"),
            snapshot("ISSUED"),
            validConsent()
        );

        assertThat(result.outcome()).isEqualTo(VerificationOutcome.MISMATCH);
        assertThat(result.matchScore()).isLessThan(70);
    }

    @Test
    void matchReturnsRevokedForRevokedQualification() {
        final VerificationMatchResult result = service.match(null, snapshot("REVOKED"), validConsent());

        assertThat(result.outcome()).isEqualTo(VerificationOutcome.REVOKED);
    }

    @Test
    void matchRequiresConsentBeforeDisclosureForIssuedRecord() {
        final VerificationMatchResult result = service.match(
            null,
            snapshot("ISSUED"),
            new ConsentValidationResult(ConsentStatus.PENDING, ConsentScope.STATUS_ONLY, "Consent required")
        );

        assertThat(result.outcome()).isEqualTo(VerificationOutcome.CONSENT_REQUIRED);
    }

    private ConsentValidationResult validConsent() {
        return new ConsentValidationResult(ConsentStatus.VALID, ConsentScope.BASIC_DETAILS, null);
    }

    private QualificationVerificationSnapshotDto snapshot(String status) {
        return new QualificationVerificationSnapshotDto(
            UUID.randomUUID(),
            "security-123",
            "Q-1",
            "DEGREE",
            "Computer Science",
            "First Class",
            2024,
            status,
            LocalDateTime.now(),
            null,
            null,
            new QualificationVerificationSnapshotDto.StudentSnapshot(
                UUID.randomUUID(),
                "S-1",
                "Amina",
                "Dube",
                LocalDate.of(2001, 4, 12)
            ),
            new QualificationVerificationSnapshotDto.InstitutionSnapshot(UUID.randomUUID(), "MSU", "MSU")
        );
    }
}
