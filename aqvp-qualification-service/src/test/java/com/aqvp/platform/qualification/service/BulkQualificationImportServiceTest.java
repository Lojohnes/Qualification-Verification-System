package com.aqvp.platform.qualification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class BulkQualificationImportServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private ProgramRepository programRepository;
    @Mock private QualificationRepository qualificationRepository;
    @Mock private QualificationService qualificationService;
    @Mock private DocumentService documentService;

    private BulkQualificationImportService service;
    private UUID institutionId;

    @BeforeEach
    void setUp() {
        service = new BulkQualificationImportService(
            studentRepository, programRepository, qualificationRepository, qualificationService, documentService);
        institutionId = UUID.randomUUID();
    }

    @Test
    void previewReportsInvalidHeader() {
        final var result = service.preview(csv("wrong\nMSU001,A,B,C,2.1,2026"), institutionId);

        assertThat(result.totalRows()).isZero();
        assertThat(result.errors()).anyMatch(error -> error.field().equals("header"));
    }

    @Test
    void previewReportsDuplicateRowsAndCountsInvalidRows() {
        final Student student = Student.builder().id(UUID.randomUUID()).studentNumber("MSU001")
            .firstName("Student").lastName("A").institutionId(institutionId).build();
        when(studentRepository.findByStudentNumber("MSU001")).thenReturn(Optional.of(student));
        when(programRepository.findByInstitutionId(institutionId)).thenReturn(List.of());

        final var result = service.preview(csv("Student ID,Recipient Name,Programme,Qualification,Class,Graduation Year\n"
            + "MSU001,Student A,Information Systems,BCom Honours,2.1,2026\n"
            + "MSU001,Student A,Information Systems,BCom Honours,2.1,2026"), institutionId);

        assertThat(result.totalRows()).isEqualTo(2);
        assertThat(result.validRows()).isZero();
        assertThat(result.errors()).anyMatch(error -> error.field().equals("Student ID"));
    }

    @Test
    void confirmCreatesIssuesAndDocumentsForValidRows() {
        final UUID studentId = UUID.randomUUID();
        final UUID qualificationId = UUID.randomUUID();
        final Student student = Student.builder().id(studentId).studentNumber("MSU001")
            .firstName("Student").lastName("A").institutionId(institutionId).build();
        final Program program = Program.builder().id(UUID.randomUUID()).name("Information Systems").build();
        when(studentRepository.findByStudentNumber("MSU001")).thenReturn(Optional.of(student));
        when(programRepository.findByInstitutionId(institutionId)).thenReturn(List.of(program));
        when(qualificationRepository.existsByQualificationNumber("BULK-MSU001-2026")).thenReturn(false);
        when(qualificationService.createQualification(any())).thenReturn(new QualificationResponseDto(
            qualificationId, "BULK-MSU001-2026", studentId, institutionId, program.getId(), "DEGREE",
            "BCom Honours", "2.1", 2026, "DRAFT", null, null, null, null, null, List.of(), null, null, null, null, null));

        final var result = service.confirm(csv("Student ID,Recipient Name,Programme,Qualification,Class,Graduation Year\n"
            + "MSU001,Student A,Information Systems,BCom Honours,2.1,2026"), institutionId, "user");

        assertThat(result.importedRows()).isEqualTo(1);
        verify(qualificationService).issueQualification(any(), any(), org.mockito.ArgumentMatchers.eq("user"));
        verify(documentService).generateCertificateDocument(qualificationId);
        verify(documentService).generateQrCodeDocument(qualificationId);
    }

    private MockMultipartFile csv(String content) {
        return new MockMultipartFile("file", "qualifications.csv", "text/csv",
            content.getBytes(StandardCharsets.UTF_8));
    }
}