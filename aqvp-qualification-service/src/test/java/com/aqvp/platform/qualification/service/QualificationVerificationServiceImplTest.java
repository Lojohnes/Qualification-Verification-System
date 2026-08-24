package com.aqvp.platform.qualification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import com.aqvp.platform.qualification.domain.QualificationType;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class QualificationVerificationServiceImplTest {

    private final QualificationRepository qualificationRepository = mock(QualificationRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final InstitutionRepository institutionRepository = mock(InstitutionRepository.class);
    private final QualificationVerificationServiceImpl service = new QualificationVerificationServiceImpl(
        qualificationRepository,
        studentRepository,
        institutionRepository
    );

    @Test
    void getBySecurityIdentifierBuildsMinimalAuthoritativeSnapshot() {
        final UUID qualificationId = UUID.randomUUID();
        final UUID studentId = UUID.randomUUID();
        final UUID institutionId = UUID.randomUUID();
        final Qualification qualification = Qualification.builder()
            .id(qualificationId)
            .qualificationNumber("Q-1")
            .securityIdentifier("security-123")
            .studentId(studentId)
            .institutionId(institutionId)
            .qualificationType(QualificationType.DEGREE)
            .qualificationName("Computer Science")
            .yearOfAward(2024)
            .status(QualificationStatus.ISSUED)
            .build();
        final Student student = Student.builder()
            .id(studentId)
            .studentNumber("S-1")
            .firstName("Amina")
            .lastName("Dube")
            .dateOfBirth(LocalDate.of(2001, 4, 12))
            .institutionId(institutionId)
            .active(true)
            .build();
        final Institution institution = Institution.builder()
            .id(institutionId)
            .name("Midlands State University")
            .code("MSU")
            .active(true)
            .build();
        when(qualificationRepository.findBySecurityIdentifier("security-123"))
            .thenReturn(Optional.of(qualification));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));

        final QualificationVerificationSnapshotDto snapshot = service.getBySecurityIdentifier("security-123");

        assertThat(snapshot.qualificationId()).isEqualTo(qualificationId);
        assertThat(snapshot.student().studentNumber()).isEqualTo("S-1");
        assertThat(snapshot.institution().code()).isEqualTo("MSU");
        assertThat(snapshot.status()).isEqualTo("ISSUED");
    }
}
