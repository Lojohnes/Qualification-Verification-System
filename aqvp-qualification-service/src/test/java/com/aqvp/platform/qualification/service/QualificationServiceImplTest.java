package com.aqvp.platform.qualification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import com.aqvp.platform.qualification.domain.QualificationType;
import com.aqvp.platform.qualification.domain.Student;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QualificationServiceImplTest {

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private QualificationStatusHistoryRepository statusHistoryRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private QualificationMapper qualificationMapper;

    @InjectMocks
    private QualificationServiceImpl qualificationService;

    private UUID id;
    private UUID studentId;
    private UUID institutionId;
    private QualificationRequestDto requestDto;
    private Qualification qualification;
    private QualificationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        studentId = UUID.randomUUID();
        institutionId = UUID.randomUUID();

        requestDto = new QualificationRequestDto(
            "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class", 2024, null);

        qualification = Qualification.builder()
            .id(id)
            .qualificationNumber("QUAL-001")
            .studentId(studentId)
            .institutionId(institutionId)
            .qualificationType(QualificationType.DEGREE)
            .qualificationName("Bachelor of Science")
            .classification("First Class")
            .yearOfAward(2024)
            .status(QualificationStatus.DRAFT)
            .build();

        responseDto = new QualificationResponseDto(
            id, "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class",
            2024, "DRAFT", null, null, null, null, null,
            List.of(), null, null, null, null, 1L);
    }

    @Test
    void createQualification_success() {
        when(qualificationRepository.existsByQualificationNumber("QUAL-001")).thenReturn(false);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(Student.builder().id(studentId).build()));
        when(qualificationMapper.toEntity(requestDto)).thenReturn(qualification);
        when(qualificationRepository.save(qualification)).thenReturn(qualification);
        when(qualificationMapper.toResponseDto(qualification)).thenReturn(responseDto);

        final QualificationResponseDto result = qualificationService.createQualification(requestDto);

        assertNotNull(result);
        assertEquals("QUAL-001", result.qualificationNumber());
        verify(qualificationRepository).save(qualification);
    }

    @Test
    void createQualification_duplicateNumber_throwsException() {
        when(qualificationRepository.existsByQualificationNumber("QUAL-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
            () -> qualificationService.createQualification(requestDto));
        verify(qualificationRepository, never()).save(any());
    }

    @Test
    void createQualification_invalidType_throwsException() {
        final QualificationRequestDto badDto = new QualificationRequestDto(
            "QUAL-001", studentId, institutionId, null,
            "INVALID_TYPE", "Degree Name", null, 2024, null);

        when(qualificationRepository.existsByQualificationNumber("QUAL-001")).thenReturn(false);
        // validateQualificationType throws before studentRepository.findById is reached

        assertThrows(BusinessException.class,
            () -> qualificationService.createQualification(badDto));
    }

    @Test
    void createQualification_studentNotFound_throwsException() {
        when(qualificationRepository.existsByQualificationNumber("QUAL-001")).thenReturn(false);
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> qualificationService.createQualification(requestDto));
    }

    @Test
    void getQualificationById_success() {
        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));
        when(qualificationMapper.toResponseDto(qualification)).thenReturn(responseDto);

        final QualificationResponseDto result = qualificationService.getQualificationById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void getQualificationById_notFound_throwsException() {
        when(qualificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> qualificationService.getQualificationById(id));
    }

    @Test
    void issueQualification_success() {
        final QualificationResponseDto issuedResponse = new QualificationResponseDto(
            id, "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class",
            2024, "ISSUED", "some-security-id", null, null, null, null,
            List.of(), null, null, null, null, 2L);

        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));
        when(qualificationRepository.save(qualification)).thenReturn(qualification);
        when(qualificationMapper.toResponseDto(qualification)).thenReturn(issuedResponse);

        final QualificationResponseDto result = qualificationService.issueQualification(
            id, new QualificationIssueRequestDto(null), "admin");

        assertNotNull(result);
        assertEquals(QualificationStatus.ISSUED, qualification.getStatus());
        assertNotNull(qualification.getSecurityIdentifier());
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void issueQualification_notDraft_throwsBusinessException() {
        qualification.setStatus(QualificationStatus.ISSUED);
        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));

        assertThrows(BusinessException.class,
            () -> qualificationService.issueQualification(id, null, "admin"));
    }

    @Test
    void amendQualification_success() {
        qualification.setStatus(QualificationStatus.ISSUED);
        final QualificationAmendRequestDto amendDto = new QualificationAmendRequestDto(
            "Correcting classification", "Bachelor of Science (Hons)", "First Class Honours", null);

        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));
        when(qualificationRepository.save(qualification)).thenReturn(qualification);
        when(qualificationMapper.toResponseDto(qualification)).thenReturn(responseDto);

        qualificationService.amendQualification(id, amendDto, "admin");

        assertEquals(QualificationStatus.AMENDED, qualification.getStatus());
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void amendQualification_onDraft_throwsBusinessException() {
        final QualificationAmendRequestDto amendDto = new QualificationAmendRequestDto(
            "reason", "Name", null, null);
        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));

        assertThrows(BusinessException.class,
            () -> qualificationService.amendQualification(id, amendDto, "admin"));
    }

    @Test
    void revokeQualification_success() {
        qualification.setStatus(QualificationStatus.ISSUED);
        final QualificationRevokeRequestDto revokeDto = new QualificationRevokeRequestDto("Fraudulent record");

        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));
        when(qualificationRepository.save(qualification)).thenReturn(qualification);
        when(qualificationMapper.toResponseDto(qualification)).thenReturn(responseDto);

        qualificationService.revokeQualification(id, revokeDto, "admin");

        assertEquals(QualificationStatus.REVOKED, qualification.getStatus());
        assertEquals("Fraudulent record", qualification.getRevocationReason());
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void revokeQualification_alreadyRevoked_throwsBusinessException() {
        qualification.setStatus(QualificationStatus.REVOKED);
        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));

        assertThrows(BusinessException.class,
            () -> qualificationService.revokeQualification(
                id, new QualificationRevokeRequestDto("reason"), "admin"));
    }

    @Test
    void revokeQualification_onDraft_throwsBusinessException() {
        when(qualificationRepository.findById(id)).thenReturn(Optional.of(qualification));

        assertThrows(BusinessException.class,
            () -> qualificationService.revokeQualification(
                id, new QualificationRevokeRequestDto("reason"), "admin"));
    }
}
