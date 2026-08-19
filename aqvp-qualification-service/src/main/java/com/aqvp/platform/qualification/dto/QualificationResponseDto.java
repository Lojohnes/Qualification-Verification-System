package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a {@link com.aqvp.platform.qualification.domain.Qualification}.
 */
public record QualificationResponseDto(
    UUID id,
    String qualificationNumber,
    UUID studentId,
    UUID institutionId,
    UUID programId,
    String qualificationType,
    String qualificationName,
    String classification,
    Integer yearOfAward,
    String status,
    String securityIdentifier,
    LocalDateTime issuedAt,
    LocalDateTime revokedAt,
    String revocationReason,
    String notes,
    List<QualificationStatusHistoryDto> statusHistory,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {

    /** Compact canonical constructor — stores an unmodifiable defensive copy of the history list. */
    public QualificationResponseDto {
        statusHistory = statusHistory == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(statusHistory);
    }

    /** Returns an unmodifiable view of the status history to prevent external mutation. */
    @Override
    public List<QualificationStatusHistoryDto> statusHistory() {
        return Collections.unmodifiableList(statusHistory);
    }
}

