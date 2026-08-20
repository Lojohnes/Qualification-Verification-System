package com.aqvp.platform.verification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Persisted outcome of a verification attempt.
 */
@Entity
@Table(name = "verification_results")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class VerificationResult extends BaseEntity {

    @Column(nullable = false)
    private UUID verificationRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private VerificationOutcome outcome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VerificationConfidence confidence;

    @Column
    private UUID matchedQualificationId;

    @Column(length = 64)
    private String matchedSecurityIdentifierHash;

    @Column(length = 50)
    private String qualificationStatus;

    @Column(nullable = false)
    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String matchDetailsJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ConsentScope responseDisclosureScope;

    @Column(length = 100)
    private String failureCode;

    @Column(columnDefinition = "TEXT")
    private String failureMessage;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;
}
