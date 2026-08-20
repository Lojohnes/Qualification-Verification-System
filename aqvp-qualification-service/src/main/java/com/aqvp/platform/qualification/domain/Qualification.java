package com.aqvp.platform.qualification.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Authoritative academic qualification record.
 *
 * <p>Records must never be permanently deleted. Revocation or superseding must preserve history.
 */
@Entity
@Table(name = "qualifications")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = "Lombok generated collections for JPA relationship map."
)
public class Qualification extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String qualificationNumber;

    /** UUID reference to the student holding this qualification. */
    @Column(nullable = false)
    private UUID studentId;

    /** UUID reference to the issuing institution. */
    @Column(nullable = false)
    private UUID institutionId;

    /** UUID reference to the academic programme (optional). */
    @Column
    private UUID programId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private QualificationType qualificationType;

    @Column(nullable = false, length = 255)
    private String qualificationName;

    /** Degree classification e.g. First Class, 2.1, Distinction. */
    @Column(length = 100)
    private String classification;

    @Column(nullable = false)
    private Integer yearOfAward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private QualificationStatus status = QualificationStatus.DRAFT;

    /**
     * Cryptographically secure identifier generated at issuance.
     * Used by QR codes to reference this record without exposing biodata.
     */
    @Column(unique = true, length = 255)
    private String securityIdentifier;

    @Column
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime revokedAt;

    @Column(columnDefinition = "TEXT")
    private String revocationReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @OneToMany(mappedBy = "qualificationId", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("changedAt ASC")
    private List<QualificationStatusHistory> statusHistory = new ArrayList<>();
}
