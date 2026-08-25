package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import com.aqvp.platform.qualification.domain.QualificationStatusHistory;
import com.aqvp.platform.qualification.domain.QualificationType;
import com.aqvp.platform.qualification.dto.QualificationAmendRequestDto;
import com.aqvp.platform.qualification.dto.QualificationIssueRequestDto;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.dto.QualificationRevokeRequestDto;
import com.aqvp.platform.qualification.exception.BusinessException;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.QualificationMapper;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.QualificationStatusHistoryRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing qualification records.
 *
 * <p>Records are never permanently deleted. Lifecycle transitions (issue, amend, revoke)
 * each append an immutable {@link QualificationStatusHistory} entry.
 */
@Service
@RequiredArgsConstructor
public class QualificationServiceImpl implements QualificationService {

    private final QualificationRepository qualificationRepository;
    private final QualificationStatusHistoryRepository statusHistoryRepository;
    private final StudentRepository studentRepository;
    private final QualificationMapper qualificationMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    @Transactional
    public QualificationResponseDto createQualification(QualificationRequestDto dto) {
        if (qualificationRepository.existsByQualificationNumber(dto.qualificationNumber())) {
            throw new DuplicateResourceException(
                "Qualification number already exists: " + dto.qualificationNumber());
        }
        validateQualificationType(dto.qualificationType());
        studentRepository.findById(dto.studentId())
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + dto.studentId()));

        final Qualification qualification = qualificationMapper.toEntity(dto);
        qualification.setStatus(QualificationStatus.DRAFT);
        final Qualification saved = qualificationRepository.save(qualification);
        auditEventPublisher.publish("qualification.created", "CREATE", "qualification", saved.getId(),
            saved.getQualificationNumber(), null, "{\"status\":\"DRAFT\"}");
        return qualificationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public QualificationResponseDto updateQualification(UUID id, QualificationRequestDto dto) {
        final Qualification qualification = findOrThrow(id);

        if (!qualification.getQualificationNumber().equals(dto.qualificationNumber())
                && qualificationRepository.existsByQualificationNumber(dto.qualificationNumber())) {
            throw new DuplicateResourceException(
                "Qualification number already exists: " + dto.qualificationNumber());
        }
        validateQualificationType(dto.qualificationType());
        qualificationMapper.updateEntity(dto, qualification);
        final Qualification updated = qualificationRepository.save(qualification);
        auditEventPublisher.publish("qualification.updated", "UPDATE", "qualification", updated.getId(),
            updated.getQualificationNumber(), null, "{\"status\":\"" + updated.getStatus() + "\"}");
        return qualificationMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public QualificationResponseDto getQualificationById(UUID id) {
        return qualificationMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QualificationResponseDto> getQualificationsByStudent(UUID studentId) {
        return qualificationRepository.findByStudentId(studentId).stream()
            .map(qualificationMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QualificationResponseDto> getQualificationsByInstitution(UUID institutionId) {
        return qualificationRepository.findByInstitutionId(institutionId).stream()
            .map(qualificationMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QualificationResponseDto issueQualification(UUID id, QualificationIssueRequestDto dto, String issuedBy) {
        final Qualification qualification = findOrThrow(id);
        assertStatus(qualification, QualificationStatus.DRAFT,
            "Only DRAFT qualifications can be issued. Current status: " + qualification.getStatus());

        final String previousStatus = qualification.getStatus().name();
        qualification.setStatus(QualificationStatus.ISSUED);
        qualification.setIssuedAt(LocalDateTime.now());
        qualification.setSecurityIdentifier(UUID.randomUUID().toString());
        if (dto != null && dto.notes() != null) {
            qualification.setNotes(dto.notes());
        }

        appendHistory(qualification, previousStatus, QualificationStatus.ISSUED.name(), issuedBy, null);
        final Qualification issued = qualificationRepository.save(qualification);
        auditEventPublisher.publish("qualification.issued", "ISSUE", "qualification", issued.getId(),
            issued.getQualificationNumber(), "{\"status\":\"" + previousStatus + "\"}", "{\"status\":\"ISSUED\"}");
        return qualificationMapper.toResponseDto(issued);
    }

    @Override
    @Transactional
    public QualificationResponseDto amendQualification(UUID id, QualificationAmendRequestDto dto, String amendedBy) {
        final Qualification qualification = findOrThrow(id);
        if (qualification.getStatus() != QualificationStatus.ISSUED
                && qualification.getStatus() != QualificationStatus.AMENDED) {
            throw new BusinessException(
                "Only ISSUED or AMENDED qualifications can be amended. Current status: " + qualification.getStatus());
        }

        final String previousStatus = qualification.getStatus().name();
        qualification.setStatus(QualificationStatus.AMENDED);
        qualification.setQualificationName(dto.qualificationName());
        if (dto.classification() != null) {
            qualification.setClassification(dto.classification());
        }
        if (dto.notes() != null) {
            qualification.setNotes(dto.notes());
        }

        appendHistory(qualification, previousStatus, QualificationStatus.AMENDED.name(), amendedBy, dto.reason());
        final Qualification amended = qualificationRepository.save(qualification);
        auditEventPublisher.publish("qualification.amended", "AMEND", "qualification", amended.getId(),
            amended.getQualificationNumber(), "{\"status\":\"" + previousStatus + "\"}", "{\"status\":\"AMENDED\"}");
        return qualificationMapper.toResponseDto(amended);
    }

    @Override
    @Transactional
    public QualificationResponseDto revokeQualification(UUID id, QualificationRevokeRequestDto dto, String revokedBy) {
        final Qualification qualification = findOrThrow(id);
        if (qualification.getStatus() == QualificationStatus.REVOKED
                || qualification.getStatus() == QualificationStatus.WITHDRAWN) {
            throw new BusinessException(
                "Qualification is already in a terminal state: " + qualification.getStatus());
        }
        if (qualification.getStatus() == QualificationStatus.DRAFT) {
            throw new BusinessException("DRAFT qualifications cannot be revoked; delete or issue them instead.");
        }

        final String previousStatus = qualification.getStatus().name();
        qualification.setStatus(QualificationStatus.REVOKED);
        qualification.setRevokedAt(LocalDateTime.now());
        qualification.setRevocationReason(dto.reason());

        appendHistory(qualification, previousStatus, QualificationStatus.REVOKED.name(), revokedBy, dto.reason());
        final Qualification revoked = qualificationRepository.save(qualification);
        auditEventPublisher.publish("qualification.revoked", "REVOKE", "qualification", revoked.getId(),
            revoked.getQualificationNumber(), "{\"status\":\"" + previousStatus + "\"}", "{\"status\":\"REVOKED\"}");
        return qualificationMapper.toResponseDto(revoked);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Qualification findOrThrow(UUID id) {
        return qualificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Qualification not found with id: " + id));
    }

    private void assertStatus(Qualification qualification, QualificationStatus expected, String message) {
        if (qualification.getStatus() != expected) {
            throw new BusinessException(message);
        }
    }

    private void appendHistory(Qualification qualification, String previousStatus,
                                String newStatus, String changedBy, String reason) {
        final QualificationStatusHistory entry = QualificationStatusHistory.builder()
            .qualificationId(qualification.getId())
            .previousStatus(previousStatus)
            .newStatus(newStatus)
            .changedBy(changedBy)
            .reason(reason)
            .build();
        statusHistoryRepository.save(entry);
    }

    private void validateQualificationType(String type) {
        try {
            QualificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid qualification type: " + type);
        }
    }
}
