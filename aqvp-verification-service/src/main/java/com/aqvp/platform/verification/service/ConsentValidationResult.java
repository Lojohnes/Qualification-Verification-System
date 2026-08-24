package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;

/**
 * Consent validation decision.
 */
public record ConsentValidationResult(
    ConsentStatus status,
    ConsentScope scope,
    String failureReason
) {

    public boolean validForVerification() {
        return status == ConsentStatus.VALID;
    }

    public boolean validForDisclosure() {
        return status == ConsentStatus.VALID && scope != ConsentScope.STATUS_ONLY;
    }
}
