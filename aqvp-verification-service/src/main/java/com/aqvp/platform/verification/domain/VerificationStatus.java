package com.aqvp.platform.verification.domain;

/**
 * Result of a verification attempt.
 */
public enum VerificationStatus {
    VERIFIED,
    NOT_FOUND,
    REVOKED,
    WITHDRAWN,
    NOT_ISSUED
}
