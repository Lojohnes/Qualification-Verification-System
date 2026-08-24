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
 * Records an attempt to verify a qualification.
 */
@Entity
@Table(name = "verifications", schema = "verification")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Verification extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String qualificationNumber;

    @Column(length = 255)
    private String securityIdentifier;

    @Column
    private UUID qualificationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationMethod method;

    @Column(length = 100)
    private String verifiedBy;

    @Column(nullable = false)
    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 255)
    private String notes;
}
