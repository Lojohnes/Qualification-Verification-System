package com.aqvp.platform.verification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.VerificationChannel;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import com.aqvp.platform.verification.domain.VerificationPurpose;
import com.aqvp.platform.verification.domain.VerificationRequest;
import com.aqvp.platform.verification.dto.ConsentRequestDto;
import com.aqvp.platform.verification.dto.EvidenceRequestDto;
import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.verification.dto.QrVerificationRequestDto;
import com.aqvp.platform.verification.dto.VerificationResultResponseDto;
import com.aqvp.platform.verification.exception.UpstreamNotFoundException;
import com.aqvp.platform.verification.repository.VerificationResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VerificationEngineServiceTest {

    private final VerificationRequestService requestService = mock(VerificationRequestService.class);
    private final QualificationLookupClient lookupClient = mock(QualificationLookupClient.class);
    private final VerificationResultRepository resultRepository = mock(VerificationResultRepository.class);
    private final AuditEventPublisher auditEventPublisher = mock(AuditEventPublisher.class);
    private final VerificationEngineService service = new VerificationEngineService(
        requestService,
        new QrPayloadParser(),
        new HashingService(),
        lookupClient,
        new VerificationMatchingService(),
        resultRepository,
        new ObjectMapper(),
        auditEventPublisher
    );

    @Test
    void verifyQrReturnsVerifiedWhenSnapshotAndEvidenceMatch() {
        final VerificationRequest request = request();
        final QualificationVerificationSnapshotDto snapshot = snapshot("ISSUED");
        when(requestService.createQrRequest(any(), any(), any(), any())).thenReturn(request);
        when(requestService.latestConsentResult(request.getId())).thenReturn(validConsent());
        when(lookupClient.findBySecurityIdentifier("security-123")).thenReturn(snapshot);
        when(resultRepository.save(any())).thenAnswer(invocation -> {
            final com.aqvp.platform.verification.domain.VerificationResult result = invocation.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        final VerificationResultResponseDto response = service.verifyQr(
            qrRequest(validConsentDto(), matchingEvidence()),
            "verifier"
        );

        assertThat(response.outcome()).isEqualTo(VerificationOutcome.VERIFIED);
        assertThat(response.qualification()).isNotNull();
        assertThat(response.holder()).isNotNull();
        verify(auditEventPublisher).verificationCompleted(request.getId(), VerificationOutcome.VERIFIED);
    }

    @Test
    void verifyQrReturnsConsentRequiredWithoutDetailsWhenConsentMissing() {
        final VerificationRequest request = request();
        when(requestService.createQrRequest(any(), any(), any(), any())).thenReturn(request);
        when(requestService.latestConsentResult(request.getId())).thenReturn(
            new ConsentValidationResult(ConsentStatus.PENDING, ConsentScope.STATUS_ONLY, "Consent required")
        );
        when(lookupClient.findBySecurityIdentifier("security-123")).thenReturn(snapshot("ISSUED"));
        when(resultRepository.save(any())).thenAnswer(invocation -> {
            final com.aqvp.platform.verification.domain.VerificationResult result = invocation.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        final VerificationResultResponseDto response = service.verifyQr(qrRequest(null, null), "verifier");

        assertThat(response.outcome()).isEqualTo(VerificationOutcome.CONSENT_REQUIRED);
        assertThat(response.holder()).isNull();
        assertThat(response.qualification().status()).isEqualTo("ISSUED");
    }

    @Test
    void verifyQrPersistsNotFoundWhenQualificationServiceHasNoRecord() {
        final VerificationRequest request = request();
        when(requestService.createQrRequest(any(), any(), any(), any())).thenReturn(request);
        when(requestService.latestConsentResult(request.getId())).thenReturn(validConsent());
        when(lookupClient.findBySecurityIdentifier("security-123"))
            .thenThrow(new UpstreamNotFoundException("No authoritative qualification record found"));
        when(resultRepository.save(any())).thenAnswer(invocation -> {
            final com.aqvp.platform.verification.domain.VerificationResult result = invocation.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        final VerificationResultResponseDto response = service.verifyQr(
            qrRequest(validConsentDto(), matchingEvidence()),
            "verifier"
        );

        assertThat(response.outcome()).isEqualTo(VerificationOutcome.NOT_FOUND);
        assertThat(response.qualification()).isNull();
    }

    private QrVerificationRequestDto qrRequest(ConsentRequestDto consent, EvidenceRequestDto evidence) {
        return new QrVerificationRequestDto(
            "AQVP:v1:MSU:security-123",
            VerificationPurpose.EMPLOYMENT,
            consent,
            evidence
        );
    }

    private EvidenceRequestDto matchingEvidence() {
        return new EvidenceRequestDto("Q-1", "S-1", "Amina", "Dube", 2024, "Computer Science", null, "MSU");
    }

    private ConsentRequestDto validConsentDto() {
        return new ConsentRequestDto(
            com.aqvp.platform.verification.domain.ConsentType.ATTESTED_BY_VERIFIER,
            ConsentScope.BASIC_DETAILS,
            null,
            null,
            null,
            null,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(10),
            "HR-123"
        );
    }

    private ConsentValidationResult validConsent() {
        return new ConsentValidationResult(ConsentStatus.VALID, ConsentScope.BASIC_DETAILS, null);
    }

    private VerificationRequest request() {
        final VerificationRequest request = VerificationRequest.builder()
            .requestReference("VR-1")
            .channel(VerificationChannel.QR)
            .purpose(VerificationPurpose.EMPLOYMENT)
            .build();
        request.setId(UUID.randomUUID());
        return request;
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
