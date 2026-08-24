package com.aqvp.platform.verification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Normalized evidence submitted by a verifier.
 */
@Entity
@Table(name = "verification_evidence")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class VerificationEvidence extends BaseEntity {

    @Column(nullable = false)
    private UUID verificationRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private EvidenceType evidenceType;

    @Column(length = 100)
    private String qualificationNumber;

    @Column(length = 100)
    private String studentNumber;

    @Column(length = 150)
    private String holderFirstName;

    @Column(length = 150)
    private String holderLastName;

    @Column
    private Integer yearOfAward;

    @Column(length = 255)
    private String qualificationName;

    @Column
    private UUID institutionId;

    @Column(length = 255)
    private String institutionName;

    @Column(length = 64)
    private String rawPayloadHash;
}
