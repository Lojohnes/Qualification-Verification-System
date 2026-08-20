package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.QualificationAmendRequestDto;
import com.aqvp.platform.qualification.dto.QualificationIssueRequestDto;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.dto.QualificationRevokeRequestDto;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing qualification records.
 *
 * <p>Qualification records are never permanently deleted; lifecycle changes use dedicated actions.
 */
public interface QualificationService {

    QualificationResponseDto createQualification(QualificationRequestDto dto);

    QualificationResponseDto updateQualification(UUID id, QualificationRequestDto dto);

    QualificationResponseDto getQualificationById(UUID id);

    List<QualificationResponseDto> getQualificationsByStudent(UUID studentId);

    List<QualificationResponseDto> getQualificationsByInstitution(UUID institutionId);

    /**
     * Transitions qualification from DRAFT → ISSUED, generating a security identifier.
     *
     * @param id qualification UUID
     * @param dto optional notes for the transition
     * @param issuedBy principal name of the user performing the action
     * @return updated qualification response
     */
    QualificationResponseDto issueQualification(UUID id, QualificationIssueRequestDto dto, String issuedBy);

    /**
     * Transitions ISSUED or AMENDED → AMENDED, recording a history entry.
     *
     * @param id qualification UUID
     * @param dto amendment details and reason
     * @param amendedBy principal name of the user performing the action
     * @return updated qualification response
     */
    QualificationResponseDto amendQualification(UUID id, QualificationAmendRequestDto dto, String amendedBy);

    /**
     * Transitions ISSUED or AMENDED → REVOKED (terminal state), recording a history entry.
     *
     * @param id qualification UUID
     * @param dto revocation reason
     * @param revokedBy principal name of the user performing the action
     * @return updated qualification response
     */
    QualificationResponseDto revokeQualification(UUID id, QualificationRevokeRequestDto dto, String revokedBy);
}
