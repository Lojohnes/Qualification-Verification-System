package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.VerificationConfidence;
import com.aqvp.platform.verification.domain.VerificationOutcome;
import com.aqvp.platform.verification.dto.EvidenceRequestDto;
import com.aqvp.platform.verification.dto.MatchDetailDto;
import com.aqvp.platform.verification.dto.QualificationVerificationSnapshotDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Compares submitted evidence against the authoritative qualification snapshot.
 */
@Service
public class VerificationMatchingService {

    public VerificationMatchResult match(
            EvidenceRequestDto evidence,
            QualificationVerificationSnapshotDto snapshot,
            ConsentValidationResult consent) {
        final VerificationMatchResult statusResult = resultForStatus(snapshot.status());
        if (statusResult != null) {
            return statusResult;
        }
        if (!consent.validForVerification()) {
            final VerificationOutcome outcome = consent.status() == ConsentStatus.INVALID
                    || consent.status() == ConsentStatus.EXPIRED
                ? VerificationOutcome.CONSENT_INVALID
                : VerificationOutcome.CONSENT_REQUIRED;
            return new VerificationMatchResult(
                outcome,
                VerificationConfidence.NONE,
                0,
                List.of(),
                outcome.name(),
                consent.failureReason()
            );
        }

        if (evidence == null) {
            return new VerificationMatchResult(
                VerificationOutcome.VERIFIED,
                VerificationConfidence.HIGH,
                100,
                List.of(),
                null,
                null
            );
        }

        final List<MatchDetailDto> details = new ArrayList<>();
        addComparison(details, "qualificationNumber", evidence.qualificationNumber(), snapshot.qualificationNumber());
        addComparison(details, "studentNumber", evidence.studentNumber(), snapshot.student().studentNumber());
        addComparison(details, "holderFirstName", evidence.holderFirstName(), snapshot.student().firstName());
        addComparison(details, "holderLastName", evidence.holderLastName(), snapshot.student().lastName());
        addComparison(details, "qualificationName", evidence.qualificationName(), snapshot.qualificationName());
        addComparison(details, "institutionName", evidence.institutionName(), snapshot.institution().name());
        addComparison(details, "yearOfAward", evidence.yearOfAward(), snapshot.yearOfAward());

        if (details.isEmpty()) {
            return new VerificationMatchResult(
                VerificationOutcome.VERIFIED,
                VerificationConfidence.HIGH,
                100,
                details,
                null,
                null
            );
        }

        final long matched = details.stream().filter(MatchDetailDto::matched).count();
        final int score = (int) Math.round((matched * 100.0) / details.size());
        if (score < 70) {
            return new VerificationMatchResult(
                VerificationOutcome.MISMATCH,
                VerificationConfidence.LOW,
                score,
                details,
                "EVIDENCE_MISMATCH",
                "Submitted evidence does not match the authoritative qualification record"
            );
        }
        return new VerificationMatchResult(
            VerificationOutcome.VERIFIED,
            score == 100 ? VerificationConfidence.HIGH : VerificationConfidence.MEDIUM,
            score,
            details,
            null,
            null
        );
    }

    private VerificationMatchResult resultForStatus(String status) {
        return switch (status) {
            case "ISSUED", "AMENDED" -> null;
            case "REVOKED" -> fixedStatusResult(VerificationOutcome.REVOKED, status);
            case "WITHDRAWN" -> fixedStatusResult(VerificationOutcome.WITHDRAWN, status);
            default -> fixedStatusResult(VerificationOutcome.DRAFT_NOT_VERIFIABLE, status);
        };
    }

    private VerificationMatchResult fixedStatusResult(VerificationOutcome outcome, String status) {
        return new VerificationMatchResult(
            outcome,
            VerificationConfidence.HIGH,
            100,
            List.of(),
            outcome.name(),
            "Qualification status is " + status
        );
    }

    private void addComparison(List<MatchDetailDto> details, String field, String submitted, String authoritative) {
        if (!StringUtils.hasText(submitted)) {
            return;
        }
        details.add(new MatchDetailDto(field, submitted, normalize(submitted).equals(normalize(authoritative))));
    }

    private void addComparison(List<MatchDetailDto> details, String field, Integer submitted, Integer authoritative) {
        if (submitted == null) {
            return;
        }
        details.add(new MatchDetailDto(field, submitted.toString(), submitted.equals(authoritative)));
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
