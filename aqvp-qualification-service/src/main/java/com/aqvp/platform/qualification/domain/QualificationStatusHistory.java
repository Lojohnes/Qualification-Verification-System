package com.aqvp.platform.qualification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

/**
 * Immutable audit record of a {@link Qualification} status transition.
 *
 * <p>Does not extend {@link BaseEntity} — history entries are write-once and must not be updated.
 */
@Entity
@Table(name = "qualification_status_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualificationStatusHistory {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    /**
     * UUID of the qualification this history entry belongs to.
     * Stored as a plain column; the parent {@link Qualification} manages the relationship.
     */
    @Column(nullable = false, updatable = false)
    private UUID qualificationId;

    @Column(length = 50, updatable = false)
    private String previousStatus;

    @Column(nullable = false, length = 50, updatable = false)
    private String newStatus;

    @Column(length = 255, updatable = false)
    private String changedBy;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
