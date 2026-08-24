package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.VerificationChannel;
import com.aqvp.platform.verification.domain.VerificationConfidence;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import com.aqvp.platform.verification.domain.VerificationRequest;
import com.aqvp.platform.verification.domain.VerificationRequestStatus;
import com.aqvp.platform.verification.domain.VerificationResult;
import com.aqvp.platform.verification.dto.CreateVerificationRequestDto;
import com.aqvp.platform.verification.dto.EvidenceRequestDto;
import com.aqvp.platform.verification.dto.MatchDetailDto;
import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.verification.dto.QrVerificationForRequestDto;
import com.aqvp.platform.verification.dto.QrVerificationRequestDto;
import com.aqvp.platform.verification.dto.VerificationResultResponseDto;
import com.aqvp.platform.verification.dto.VerifiedHolderDto;
import com.aqvp.platform.verification.dto.VerifiedQualificationDto;
import com.aqvp.platform.verification.exception.EntityNotFoundException;
import com.aqvp.platform.verification.exception.InvalidQrException;
import com.aqvp.platform.verification.exception.UpstreamNotFoundException;
import com.aqvp.platform.verification.repository.VerificationResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Runs verification checks and persists outcomes.
 */
@Service
@RequiredArgsConstructor
public class VerificationEngineService {

    private final VerificationRequestService requestService;
    private final QrPayloadParser qrPayloadParser;
    private final HashingService hashingService;
    private final QualificationLookupClient qualificationLookupClient;
    private final VerificationMatchingService matchingService;
    private final VerificationResultRepository resultRepository;
    private final ObjectMapper objectMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Transactional
    public VerificationResultResponseDto verifyQr(QrVerificationRequestDto dto, String requesterUsername) {
        final ResolvedIdentifier resolved = resolveIdentifier(dto.qrPayload());
        final String securityHash = hashingService.sha256(resolved.securityIdentifier());
        final String payloadHash = hashingService.sha256(dto.qrPayload());
        final CreateVerificationRequestDto requestDto = new CreateVerificationRequestDto(
            VerificationChannel.QR,
            dto.purpose(),
            null,
            dto.consent(),
            dto.evidence()
        );
        final VerificationRequest request = requestService.createQrRequest(
            requestDto,
            requesterUsername,
            securityHash,
            payloadHash
        );
        final ConsentValidationResult consent = requestService.latestConsentResult(request.getId());
        return verifyParsedQr(request, resolved.payload(), dto.evidence(), consent, requesterUsername);
    }

    @Transactional
    public VerificationResultResponseDto verifyRequestQr(
            UUID requestId,
            QrVerificationForRequestDto dto,
            String requesterUsername) {
        final VerificationRequest request = requestService.findRequest(requestId);
        if (request.getStatus() == VerificationRequestStatus.COMPLETED) {
            throw new InvalidQrException("Verification request is already completed");
        }
        final QrPayload payload = qrPayloadParser.parse(dto.qrPayload());
        request.setSecurityIdentifierHash(hashingService.sha256(payload.securityIdentifier()));
        final EvidenceRequestDto evidence = dto.evidence() != null
            ? dto.evidence()
            : requestService.latestEvidence(requestId);
        final ConsentValidationResult consent = requestService.latestConsentResult(requestId);
        return verifyParsedQr(request, payload, evidence, consent, requesterUsername);
    }

    @Transactional(readOnly = true)
    public VerificationResultResponseDto getResult(UUID resultId) {
        final VerificationResult result = resultRepository.findById(resultId)
            .orElseThrow(() -> new EntityNotFoundException("Verification result not found with id: " + resultId));
        return toResponse(result, null, List.of(), false);
    }

    private VerificationResultResponseDto verifyParsedQr(
            VerificationRequest request,
            QrPayload payload,
            EvidenceRequestDto evidence,
            ConsentValidationResult consent,
            String requesterUsername) {
        try {
            final QualificationVerificationSnapshotDto snapshot = qualificationLookupClient
                .findBySecurityIdentifier(payload.securityIdentifier());
            final VerificationMatchResult match = matchingService.match(evidence, snapshot, consent);
            final VerificationResult result = saveResult(request, snapshot, match, consent.scope(), requesterUsername);
            final VerificationRequestStatus requestStatus = match.outcome() == VerificationOutcome.ERROR
                ? VerificationRequestStatus.FAILED
                : VerificationRequestStatus.COMPLETED;
            requestService.completeRequest(
                request,
                requestStatus,
                consent.status(),
                snapshot.qualificationId(),
                requesterUsername
            );
            auditEventPublisher.verificationCompleted(request.getId(), match.outcome());
            return toResponse(result, snapshot, match.matchDetails(), consent.validForDisclosure());
        } catch (UpstreamNotFoundException ex) {
            final VerificationMatchResult match = new VerificationMatchResult(
                VerificationOutcome.NOT_FOUND,
                VerificationConfidence.NONE,
                0,
                List.of(),
                "NOT_FOUND",
                ex.getMessage()
            );
            final VerificationResult result = saveResult(
                request,
                null,
                match,
                ConsentScope.STATUS_ONLY,
                requesterUsername
            );
            requestService.completeRequest(
                request,
                VerificationRequestStatus.COMPLETED,
                consent.status(),
                null,
                requesterUsername
            );
            auditEventPublisher.verificationCompleted(request.getId(), VerificationOutcome.NOT_FOUND);
            return toResponse(result, null, match.matchDetails(), false);
        }
    }

    private VerificationResult saveResult(
            VerificationRequest request,
            QualificationVerificationSnapshotDto snapshot,
            VerificationMatchResult match,
            ConsentScope disclosureScope,
            String username) {
        final VerificationResult result = VerificationResult.builder()
            .verificationRequestId(request.getId())
            .outcome(match.outcome())
            .confidence(match.confidence())
            .matchedQualificationId(snapshot == null ? null : snapshot.qualificationId())
            .matchedSecurityIdentifierHash(snapshot == null
                ? null
                : hashingService.sha256(snapshot.securityIdentifier()))
            .qualificationStatus(snapshot == null ? null : snapshot.status())
            .matchScore(match.matchScore())
            .matchDetailsJson(toJson(match.matchDetails()))
            .responseDisclosureScope(disclosureScope)
            .failureCode(match.failureCode())
            .failureMessage(match.failureMessage())
            .verifiedAt(LocalDateTime.now())
            .createdBy(username)
            .build();
        return resultRepository.save(result);
    }

    private VerificationResultResponseDto toResponse(
            VerificationResult result,
            QualificationVerificationSnapshotDto snapshot,
            List<MatchDetailDto> matchDetails,
            boolean discloseDetails) {
        return new VerificationResultResponseDto(
            result.getVerificationRequestId(),
            result.getId(),
            result.getOutcome(),
            result.getConfidence(),
            result.getMatchScore(),
            toQualification(snapshot, discloseDetails),
            toHolder(snapshot, discloseDetails),
            matchDetails.isEmpty() ? fromJson(result.getMatchDetailsJson()) : matchDetails,
            result.getVerifiedAt()
        );
    }

    private VerifiedQualificationDto toQualification(
            QualificationVerificationSnapshotDto snapshot,
            boolean discloseDetails) {
        if (snapshot == null) {
            return null;
        }
        if (!discloseDetails) {
            return new VerifiedQualificationDto(
                null,
                null,
                null,
                null,
                null,
                snapshot.status(),
                null,
                null
            );
        }
        return new VerifiedQualificationDto(
            snapshot.qualificationNumber(),
            snapshot.qualificationName(),
            snapshot.qualificationType(),
            snapshot.classification(),
            snapshot.yearOfAward(),
            snapshot.status(),
            snapshot.issuedAt(),
            snapshot.institution().name()
        );
    }

    private VerifiedHolderDto toHolder(QualificationVerificationSnapshotDto snapshot, boolean discloseDetails) {
        if (snapshot == null || !discloseDetails) {
            return null;
        }
        return new VerifiedHolderDto(snapshot.student().firstName(), snapshot.student().lastName());
    }

    private String toJson(List<MatchDetailDto> details) {
        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }

    private List<MatchDetailDto> fromJson(String json) {
        try {
            return objectMapper.readValue(json == null ? "[]" : json, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }

    private ResolvedIdentifier resolveIdentifier(String rawInput) {
        if (!StringUtils.hasText(rawInput)) {
            throw new InvalidQrException("QR payload, security identifier, or certificate number is required");
        }
        final String trimmed = rawInput.trim();

        try {
            return new ResolvedIdentifier(qrPayloadParser.parse(trimmed), null);
        } catch (InvalidQrException ignored) {
            // Not a structured QR payload; try plain lookups.
        }

        try {
            final QualificationVerificationSnapshotDto snapshot = qualificationLookupClient
                .findBySecurityIdentifier(trimmed);
            return new ResolvedIdentifier(
                new QrPayload("v1", "MANUAL", snapshot.securityIdentifier()),
                snapshot.securityIdentifier());
        } catch (UpstreamNotFoundException ignored) {
            // Try qualification number next.
        }

        try {
            final QualificationVerificationSnapshotDto snapshot = qualificationLookupClient
                .findByQualificationNumber(trimmed);
            return new ResolvedIdentifier(
                new QrPayload("v1", "MANUAL", snapshot.securityIdentifier()),
                snapshot.securityIdentifier());
        } catch (UpstreamNotFoundException e) {
            throw new InvalidQrException(
                "No qualification found for the provided QR payload, security identifier, or certificate number");
        }
    }

    private record ResolvedIdentifier(QrPayload payload, String securityIdentifier) {
        private ResolvedIdentifier {
            if (securityIdentifier == null) {
                securityIdentifier = payload.securityIdentifier();
            }
        }
    }
}
