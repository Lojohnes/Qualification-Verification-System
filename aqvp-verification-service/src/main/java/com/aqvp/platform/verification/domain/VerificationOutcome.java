package com.aqvp.platform.verification.domain;

/**
 * Business result of a verification attempt.
 */
public enum VerificationOutcome {
    VERIFIED,
    NOT_FOUND,
    REVOKED,
    WITHDRAWN,
    DRAFT_NOT_VERIFIABLE,
    MISMATCH,
    CONSENT_REQUIRED,
    CONSENT_INVALID,
    INVALID_QR,
    ERROR
}
