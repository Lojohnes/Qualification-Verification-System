package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.ConsentRecord;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.EvidenceType;
import com.aqvp.platform.verification.domain.VerificationEvidence;
import com.aqvp.platform.verification.domain.VerificationRequest;
import com.aqvp.platform.verification.domain.VerificationRequestStatus;
import com.aqvp.platform.verification.dto.ConsentRequestDto;
import com.aqvp.platform.verification.dto.ConsentValidationResponseDto;
import com.aqvp.platform.verification.dto.CreateVerificationRequestDto;
import com.aqvp.platform.verification.dto.EvidenceRequestDto;
import com.aqvp.platform.verification.dto.SubjectDto;
import com.aqvp.platform.verification.dto.VerificationRequestResponseDto;
import com.aqvp.platform.verification.dto.VerificationResultSummaryDto;
import com.aqvp.platform.verification.exception.EntityNotFoundException;
import com.aqvp.platform.verification.repository.ConsentRecordRepository;
import com.aqvp.platform.verification.repository.VerificationEvidenceRepository;
import com.aqvp.platform.verification.repository.VerificationRequestRepository;
import com.aqvp.platform.verification.repository.VerificationResultRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for tracked verification requests and consent.
 */
@Service
@RequiredArgsConstructor
public class VerificationRequestService {

    private static final DateTimeFormatter REFERENCE_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VerificationRequestRepository requestRepository;
    private final ConsentRecordRepository consentRecordRepository;
    private final VerificationEvidenceRepository evidenceRepository;
    private final VerificationResultRepository resultRepository;
    private final ConsentValidationService consentValidationService;

    @Transactional
    public VerificationRequestResponseDto createRequest(CreateVerificationRequestDto dto, String requesterUsername) {
        final ConsentValidationResult consentResult = consentValidationService.validate(dto.consent());
        final VerificationRequest request = VerificationRequest.builder()
            .requestReference(generateRequestReference())
            .requesterUsername(requesterUsername)
            .channel(dto.channel())
            .purpose(dto.purpose())
            .status(consentResult.status() == ConsentStatus.VALID
                ? VerificationRequestStatus.READY
                : VerificationRequestStatus.PENDING_CONSENT)
            .consentStatus(consentResult.status())
            .expiresAt(LocalDateTime.now().plusDays(7))
            .createdBy(requesterUsername)
            .build();
        final VerificationRequest saved = requestRepository.save(request);
        saveConsent(saved.getId(), dto.consent(), consentResult, requesterUsername);
        saveEvidence(saved.getId(), mergeSubject(dto.subject(), dto.evidence()), EvidenceType.STRUCTURED_DETAILS, null);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VerificationRequestResponseDto> listRequests() {
        return requestRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VerificationRequestResponseDto getRequest(UUID id) {
        return toResponse(findRequest(id));
    }

    @Transactional
    public ConsentValidationResponseDto validateConsent(UUID requestId, ConsentRequestDto consent, String username) {
        final VerificationRequest request = findRequest(requestId);
        final ConsentValidationResult result = consentValidationService.validate(consent);
        final ConsentRecord record = saveConsent(requestId, consent, result, username);
        request.setConsentStatus(result.status());
        request.setStatus(result.status() == ConsentStatus.VALID
            ? VerificationRequestStatus.READY
            : VerificationRequestStatus.PENDING_CONSENT);
        request.setUpdatedBy(username);
        requestRepository.save(request);
        return new ConsentValidationResponseDto(
            requestId,
            record.getStatus(),
            record.getScope(),
            record.getValidatedAt(),
            record.getFailureReason()
        );
    }

    VerificationRequest createQrRequest(
            CreateVerificationRequestDto dto,
            String requesterUsername,
            String securityIdentifierHash,
            String rawPayloadHash) {
        final ConsentValidationResult consentResult = consentValidationService.validate(dto.consent());
        final VerificationRequest request = VerificationRequest.builder()
            .requestReference(generateRequestReference())
            .requesterUsername(requesterUsername)
            .channel(dto.channel())
            .purpose(dto.purpose())
            .status(VerificationRequestStatus.PROCESSING)
            .consentStatus(consentResult.status())
            .securityIdentifierHash(securityIdentifierHash)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .createdBy(requesterUsername)
            .build();
        final VerificationRequest saved = requestRepository.save(request);
        saveConsent(saved.getId(), dto.consent(), consentResult, requesterUsername);
        saveEvidence(
            saved.getId(),
            mergeSubject(dto.subject(), dto.evidence()),
            EvidenceType.QR_PAYLOAD,
            rawPayloadHash
        );
        return saved;
    }

    VerificationRequest findRequest(UUID id) {
        return requestRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Verification request not found with id: " + id));
    }

    ConsentValidationResult latestConsentResult(UUID requestId) {
        return consentRecordRepository.findTopByVerificationRequestIdOrderByCreatedAtDesc(requestId)
            .map(record -> new ConsentValidationResult(
                record.getStatus(),
                record.getScope(),
                record.getFailureReason()
            ))
            .orElse(new ConsentValidationResult(
                ConsentStatus.PENDING,
                com.aqvp.platform.verification.domain.ConsentScope.STATUS_ONLY,
                "Consent is required before qualification details can be disclosed"
            ));
    }

    EvidenceRequestDto latestEvidence(UUID requestId) {
        return evidenceRepository.findTopByVerificationRequestIdOrderByCreatedAtDesc(requestId)
            .map(this::toEvidenceDto)
            .orElse(null);
    }

    void completeRequest(
            VerificationRequest request,
            VerificationRequestStatus status,
            ConsentStatus consentStatus,
            UUID qualificationId,
            String username) {
        request.setStatus(status);
        request.setConsentStatus(consentStatus);
        request.setQualificationId(qualificationId);
        request.setCompletedAt(LocalDateTime.now());
        request.setUpdatedBy(username);
        requestRepository.save(request);
    }

    private ConsentRecord saveConsent(
            UUID requestId,
            ConsentRequestDto consent,
            ConsentValidationResult result,
            String username) {
        if (consent == null) {
            return null;
        }
        final ConsentRecord record = ConsentRecord.builder()
            .verificationRequestId(requestId)
            .consentType(consent.consentType())
            .scope(consent.scope())
            .holderFirstName(consent.holderFirstName())
            .holderLastName(consent.holderLastName())
            .holderDateOfBirth(consent.dateOfBirth())
            .holderEmail(consent.holderEmail())
            .consentReference(consent.consentReference())
            .grantedAt(consent.grantedAt())
            .expiresAt(consent.expiresAt())
            .validatedAt(LocalDateTime.now())
            .status(result.status())
            .failureReason(result.failureReason())
            .createdBy(username)
            .build();
        return consentRecordRepository.save(record);
    }

    private void saveEvidence(
            UUID requestId,
            EvidenceRequestDto evidence,
            EvidenceType evidenceType,
            String payloadHash) {
        if (evidence == null && payloadHash == null) {
            return;
        }
        final VerificationEvidence entity = VerificationEvidence.builder()
            .verificationRequestId(requestId)
            .evidenceType(evidenceType)
            .qualificationNumber(evidence == null ? null : evidence.qualificationNumber())
            .studentNumber(evidence == null ? null : evidence.studentNumber())
            .holderFirstName(evidence == null ? null : evidence.holderFirstName())
            .holderLastName(evidence == null ? null : evidence.holderLastName())
            .yearOfAward(evidence == null ? null : evidence.yearOfAward())
            .qualificationName(evidence == null ? null : evidence.qualificationName())
            .institutionId(evidence == null ? null : evidence.institutionId())
            .institutionName(evidence == null ? null : evidence.institutionName())
            .rawPayloadHash(payloadHash)
            .build();
        evidenceRepository.save(entity);
    }

    private EvidenceRequestDto mergeSubject(SubjectDto subject, EvidenceRequestDto evidence) {
        if (subject == null) {
            return evidence;
        }
        return new EvidenceRequestDto(
            evidence == null ? null : evidence.qualificationNumber(),
            subject.studentNumber() != null
                ? subject.studentNumber()
                : evidence == null ? null : evidence.studentNumber(),
            subject.holderFirstName() != null
                ? subject.holderFirstName()
                : evidence == null ? null : evidence.holderFirstName(),
            subject.holderLastName() != null
                ? subject.holderLastName()
                : evidence == null ? null : evidence.holderLastName(),
            evidence == null ? null : evidence.yearOfAward(),
            evidence == null ? null : evidence.qualificationName(),
            evidence == null ? null : evidence.institutionId(),
            evidence == null ? null : evidence.institutionName()
        );
    }

    private EvidenceRequestDto toEvidenceDto(VerificationEvidence evidence) {
        return new EvidenceRequestDto(
            evidence.getQualificationNumber(),
            evidence.getStudentNumber(),
            evidence.getHolderFirstName(),
            evidence.getHolderLastName(),
            evidence.getYearOfAward(),
            evidence.getQualificationName(),
            evidence.getInstitutionId(),
            evidence.getInstitutionName()
        );
    }

    private VerificationRequestResponseDto toResponse(VerificationRequest request) {
        final VerificationResultSummaryDto latestResult = resultRepository
            .findTopByVerificationRequestIdOrderByVerifiedAtDesc(request.getId())
            .map(result -> new VerificationResultSummaryDto(
                result.getId(),
                result.getOutcome(),
                result.getConfidence(),
                result.getMatchScore(),
                result.getVerifiedAt()
            ))
            .orElse(null);
        return new VerificationRequestResponseDto(
            request.getId(),
            request.getRequestReference(),
            request.getChannel(),
            request.getPurpose(),
            request.getStatus(),
            request.getConsentStatus(),
            request.getExpiresAt(),
            request.getCompletedAt(),
            latestResult,
            request.getCreatedAt()
        );
    }

    private String generateRequestReference() {
        String reference;
        do {
            reference = "VR-" + LocalDateTime.now().format(REFERENCE_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (requestRepository.existsByRequestReference(reference));
        return reference;
    }
}
