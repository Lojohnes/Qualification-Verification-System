package com.aqvp.platform.verification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Read-only projection of a qualification record stored in the qualification schema.
 */
@Entity
@Table(name = "qualifications", schema = "qualification")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class VerifiedQualification {

    @Id
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String qualificationNumber;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private UUID institutionId;

    private UUID programId;

    @Column(nullable = false, length = 100)
    private String qualificationType;

    @Column(nullable = false, length = 255)
    private String qualificationName;

    @Column(length = 100)
    private String classification;

    @Column(nullable = false)
    private Integer yearOfAward;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(unique = true, length = 255)
    private String securityIdentifier;

    private LocalDateTime issuedAt;

    private LocalDateTime revokedAt;

    @Column(columnDefinition = "TEXT")
    private String revocationReason;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
