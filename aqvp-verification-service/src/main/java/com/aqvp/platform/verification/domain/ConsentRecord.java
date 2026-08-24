package com.aqvp.platform.verification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Consent evidence linked to a verification request.
 */
@Entity
@Table(name = "consent_records")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ConsentRecord extends BaseEntity {

    @Column(nullable = false)
    private UUID verificationRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ConsentType consentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ConsentScope scope;

    @Column(length = 150)
    private String holderFirstName;

    @Column(length = 150)
    private String holderLastName;

    @Column
    private LocalDate holderDateOfBirth;

    @Column(length = 255)
    private String holderEmail;

    @Column(length = 255)
    private String consentReference;

    @Column
    private LocalDateTime grantedAt;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime validatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConsentStatus status;

    @Column(columnDefinition = "TEXT")
    private String failureReason;
}
