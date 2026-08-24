package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.ConsentType;
import com.aqvp.platform.verification.dto.ConsentRequestDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Validates consent evidence before verification details are disclosed.
 */
@Service
public class ConsentValidationService {

    public ConsentValidationResult validate(ConsentRequestDto consent) {
        if (consent == null) {
            return new ConsentValidationResult(
                ConsentStatus.PENDING,
                ConsentScope.STATUS_ONLY,
                "Consent is required before qualification details can be disclosed"
            );
        }

        if (consent.scope() == ConsentScope.STATUS_ONLY) {
            return new ConsentValidationResult(ConsentStatus.VALID, ConsentScope.STATUS_ONLY, null);
        }

        if (consent.grantedAt() != null && consent.grantedAt().isAfter(LocalDateTime.now())) {
            return new ConsentValidationResult(
                ConsentStatus.INVALID,
                consent.scope(),
                "Consent grant date cannot be in the future"
            );
        }
        if (consent.expiresAt() == null || !consent.expiresAt().isAfter(LocalDateTime.now())) {
            return new ConsentValidationResult(ConsentStatus.EXPIRED, consent.scope(), "Consent has expired");
        }
        if (consent.grantedAt() != null && !consent.expiresAt().isAfter(consent.grantedAt())) {
            return new ConsentValidationResult(
                ConsentStatus.INVALID,
                consent.scope(),
                "Consent expiry must be after the grant date"
            );
        }
        if (requiresReference(consent.consentType()) && !StringUtils.hasText(consent.consentReference())) {
            return new ConsentValidationResult(
                ConsentStatus.INVALID,
                consent.scope(),
                "Consent reference is required for " + consent.consentType()
            );
        }
        return new ConsentValidationResult(ConsentStatus.VALID, consent.scope(), null);
    }

    private boolean requiresReference(ConsentType consentType) {
        return consentType == ConsentType.ATTESTED_BY_VERIFIER || consentType == ConsentType.DOCUMENTED_CONSENT;
    }
}
