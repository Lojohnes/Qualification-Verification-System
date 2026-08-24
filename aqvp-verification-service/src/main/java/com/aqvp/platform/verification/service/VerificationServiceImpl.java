package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.Verification;
import com.aqvp.platform.verification.domain.VerificationMethod;
import com.aqvp.platform.verification.domain.VerificationStatus;
import com.aqvp.platform.verification.domain.VerifiedQualification;
import com.aqvp.platform.verification.dto.VerificationRequest;
import com.aqvp.platform.verification.dto.VerificationResponse;
import com.aqvp.platform.verification.repository.VerificationRepository;
import com.aqvp.platform.verification.repository.VerifiedQualificationRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of qualification verification workflows.
 */
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VerifiedQualificationRepository qualificationRepository;
    private final VerificationRepository verificationRepository;

    @Override
    @Transactional
    public VerificationResponse verify(VerificationRequest request, String username, String ipAddress) {
        final Optional<VerifiedQualification> found = findQualification(request);

        if (found.isEmpty()) {
            final Verification record = saveRecord(
                    request.qualificationNumber() != null ? request.qualificationNumber() : request.securityIdentifier(),
                    request.securityIdentifier(),
                    null,
                    VerificationStatus.NOT_FOUND,
                    parseMethod(request.method()),
                    username,
                    ipAddress,
                    "No matching qualification was found."
            );
            return buildResponse(record, null);
        }

        final VerifiedQualification qualification = found.get();
        final VerificationStatus status = resolveStatus(qualification.getStatus());
        final String message = buildMessage(status, qualification);

        final Verification record = saveRecord(
                qualification.getQualificationNumber(),
                qualification.getSecurityIdentifier(),
                qualification.getId(),
                status,
                parseMethod(request.method()),
                username,
                ipAddress,
                message
        );

        return buildResponse(record, qualification);
    }

    private Optional<VerifiedQualification> findQualification(VerificationRequest request) {
        if (request.qualificationNumber() != null && !request.qualificationNumber().isBlank()) {
            return qualificationRepository.findByQualificationNumber(request.qualificationNumber());
        }
        if (request.securityIdentifier() != null && !request.securityIdentifier().isBlank()) {
            return qualificationRepository.findBySecurityIdentifier(request.securityIdentifier());
        }
        return Optional.empty();
    }

    private VerificationStatus resolveStatus(String qualificationStatus) {
        return switch (qualificationStatus) {
            case "ISSUED", "AMENDED" -> VerificationStatus.VERIFIED;
            case "REVOKED" -> VerificationStatus.REVOKED;
            case "WITHDRAWN" -> VerificationStatus.WITHDRAWN;
            default -> VerificationStatus.NOT_ISSUED;
        };
    }

    private String buildMessage(VerificationStatus status, VerifiedQualification qualification) {
        return switch (status) {
            case VERIFIED -> "The qualification is verified and valid.";
            case REVOKED -> "The qualification has been revoked. Reason: "
                    + defaultIfNull(qualification.getRevocationReason(), "No reason provided.");
            case WITHDRAWN -> "The qualification has been withdrawn.";
            case NOT_ISSUED -> "The qualification has not been issued yet.";
            case NOT_FOUND -> "No matching qualification was found.";
        };
    }

    private VerificationMethod parseMethod(String method) {
        try {
            return VerificationMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            return VerificationMethod.MANUAL;
        }
    }

    private Verification saveRecord(String qualificationNumber,
                                    String securityIdentifier,
                                    java.util.UUID qualificationId,
                                    VerificationStatus status,
                                    VerificationMethod method,
                                    String username,
                                    String ipAddress,
                                    String notes) {
        final Verification record = new Verification();
        record.setQualificationNumber(qualificationNumber);
        record.setSecurityIdentifier(securityIdentifier);
        record.setQualificationId(qualificationId);
        record.setStatus(status);
        record.setMethod(method);
        record.setVerifiedBy(username);
        record.setVerifiedAt(LocalDateTime.now());
        record.setIpAddress(ipAddress);
        record.setNotes(notes);
        return verificationRepository.save(record);
    }

    private VerificationResponse buildResponse(Verification record, VerifiedQualification qualification) {
        return new VerificationResponse(
                record.getStatus().name(),
                record.getNotes(),
                record.getQualificationNumber(),
                record.getQualificationId(),
                qualification != null ? qualification.getQualificationName() : null,
                qualification != null ? qualification.getClassification() : null,
                qualification != null ? qualification.getYearOfAward() : null,
                qualification != null && qualification.getIssuedAt() != null
                        ? qualification.getIssuedAt().format(DATE_TIME_FORMATTER) : null,
                record.getVerifiedAt().format(DATE_TIME_FORMATTER)
        );
    }

    private String defaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
