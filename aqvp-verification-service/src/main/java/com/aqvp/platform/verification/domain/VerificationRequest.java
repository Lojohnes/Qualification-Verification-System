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
 * Verifier request to check an academic qualification.
 */
@Entity
@Table(name = "verification_requests")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class VerificationRequest extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String requestReference;

    @Column(length = 150)
    private String requesterUsername;

    @Column
    private UUID requesterOrganizationId;

    @Column(length = 255)
    private String requesterOrganizationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VerificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private VerificationPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VerificationRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConsentStatus consentStatus;

    @Column
    private UUID qualificationId;

    @Column(length = 64)
    private String securityIdentifierHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime completedAt;
}
