package com.aqvp.platform.verification.domain;

/**
 * Validation state for consent evidence.
 */
public enum ConsentStatus {
    NOT_REQUIRED,
    PENDING,
    VALID,
    INVALID,
    EXPIRED
}
