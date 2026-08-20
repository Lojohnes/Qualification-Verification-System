package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds minimal authoritative snapshots for the Verification service.
 */
@Service
@RequiredArgsConstructor
public class QualificationVerificationServiceImpl implements QualificationVerificationService {

    private final QualificationRepository qualificationRepository;
    private final StudentRepository studentRepository;
    private final InstitutionRepository institutionRepository;

    @Override
    @Transactional(readOnly = true)
    public QualificationVerificationSnapshotDto getBySecurityIdentifier(String securityIdentifier) {
        final Qualification qualification = qualificationRepository.findBySecurityIdentifier(securityIdentifier)
            .orElseThrow(() -> new EntityNotFoundException(
                "Qualification not found for security identifier: " + securityIdentifier));
        return toSnapshot(qualification);
    }

    @Override
    @Transactional(readOnly = true)
    public QualificationVerificationSnapshotDto getByQualificationNumber(String qualificationNumber) {
        final Qualification qualification = qualificationRepository.findByQualificationNumber(qualificationNumber)
            .orElseThrow(() -> new EntityNotFoundException(
                "Qualification not found for number: " + qualificationNumber));
        return toSnapshot(qualification);
    }

    private QualificationVerificationSnapshotDto toSnapshot(Qualification qualification) {
        final Student student = studentRepository.findById(qualification.getStudentId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Student not found with id: " + qualification.getStudentId()));
        final Institution institution = institutionRepository.findById(qualification.getInstitutionId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Institution not found with id: " + qualification.getInstitutionId()));

        return new QualificationVerificationSnapshotDto(
            qualification.getId(),
            qualification.getSecurityIdentifier(),
            qualification.getQualificationNumber(),
            qualification.getQualificationType().name(),
            qualification.getQualificationName(),
            qualification.getClassification(),
            qualification.getYearOfAward(),
            qualification.getStatus().name(),
            qualification.getIssuedAt(),
            qualification.getRevokedAt(),
            qualification.getRevocationReason(),
            new QualificationVerificationSnapshotDto.StudentSnapshot(
                student.getId(),
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getDateOfBirth()
            ),
            new QualificationVerificationSnapshotDto.InstitutionSnapshot(
                institution.getId(),
                institution.getName(),
                institution.getCode()
            )
        );
    }
}
