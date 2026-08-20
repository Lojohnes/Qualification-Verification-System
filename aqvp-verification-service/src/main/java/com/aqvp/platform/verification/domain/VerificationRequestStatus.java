package com.aqvp.platform.verification.domain;

/**
 * Processing state for a verification request.
 */
public enum VerificationRequestStatus {
    PENDING_CONSENT,
    READY,
    PROCESSING,
    COMPLETED,
    FAILED,
    EXPIRED,
    CANCELLED
}
