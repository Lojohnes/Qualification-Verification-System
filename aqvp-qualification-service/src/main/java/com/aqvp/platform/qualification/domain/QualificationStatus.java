package com.aqvp.platform.qualification.domain;

/**
 * Lifecycle statuses for a {@link Qualification} record.
 *
 * <p>Valid transitions:
 * <ul>
 *   <li>DRAFT → ISSUED</li>
 *   <li>ISSUED → AMENDED</li>
 *   <li>ISSUED → REVOKED</li>
 *   <li>AMENDED → REVOKED</li>
 *   <li>REVOKED and WITHDRAWN are terminal states.</li>
 * </ul>
 */
public enum QualificationStatus {
    /** Created but not yet officially issued. */
    DRAFT,
    /** Officially issued and verifiable. */
    ISSUED,
    /** Corrected after initial issuance; original history preserved. */
    AMENDED,
    /** Withdrawn by the issuing institution; history preserved. */
    REVOKED,
    /** Formally withdrawn (semantic alias for REVOKED in some contexts). */
    WITHDRAWN
}
