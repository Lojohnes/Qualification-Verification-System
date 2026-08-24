package com.aqvp.platform.qualification.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.DocumentType;
import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationDocument;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import com.aqvp.platform.qualification.domain.QualificationType;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.DocumentFileDto;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import com.aqvp.platform.qualification.exception.BusinessException;
import com.aqvp.platform.qualification.mapper.DocumentMapper;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import com.aqvp.platform.qualification.repository.QualificationDocumentRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private QualificationRepository qualificationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private QualificationDocumentRepository documentRepository;

    @Mock
    private DocumentStorageService documentStorageService;

    @Mock
    private DigitalSignatureService digitalSignatureService;

    private DocumentServiceImpl documentService;
    private UUID qualificationId;
    private UUID studentId;
    private UUID institutionId;
    private Qualification qualification;
    private Student student;
    private Institution institution;

    @BeforeEach
    void setUp() {
        qualificationId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        institutionId = UUID.randomUUID();

        qualification = Qualification.builder()
                .id(qualificationId)
                .qualificationNumber("QUAL-001")
                .studentId(studentId)
                .institutionId(institutionId)
                .qualificationType(QualificationType.DEGREE)
                .qualificationName("Bachelor of Science")
                .classification("First Class")
                .yearOfAward(2024)
                .status(QualificationStatus.ISSUED)
                .securityIdentifier("sec-123")
                .build();

        student = Student.builder()
                .id(studentId)
                .studentNumber("S001")
                .firstName("Ada")
                .lastName("Lovelace")
                .institutionId(institutionId)
                .active(true)
                .build();

        institution = Institution.builder()
                .id(institutionId)
                .code("MSU")
                .name("Midlands State University")
                .active(true)
                .build();

        documentService = new DocumentServiceImpl(
                qualificationRepository,
                studentRepository,
                institutionRepository,
                programRepository,
                documentRepository,
                documentStorageService,
                digitalSignatureService,
                new DocumentMapper());
    }

    @Test
    void generateQrCodeDocument_persistsVerificationCompatiblePayload() {
        stubDocumentContext();
        when(documentStorageService.store(any(), any())).thenReturn("2026/08/qr.png");
        when(digitalSignatureService.signerKeyId()).thenReturn("test-key");
        when(digitalSignatureService.algorithm()).thenReturn("HmacSHA256");
        when(digitalSignatureService.sign(any(), any())).thenReturn("signature");
        when(documentRepository.save(any(QualificationDocument.class))).thenAnswer(invocation -> {
            final QualificationDocument document = invocation.getArgument(0);
            document.setId(UUID.randomUUID());
            return document;
        });

        final DocumentResponseDto result = documentService.generateQrCodeDocument(qualificationId);

        assertEquals("QR_CODE", result.documentType());
        assertEquals("AQVP:v1:MSU:sec-123", result.qrPayload());
        assertEquals("signature", result.digitalSignature());
        assertEquals("HmacSHA256", result.signatureAlgorithm());

        final ArgumentCaptor<QualificationDocument> captor = ArgumentCaptor.forClass(QualificationDocument.class);
        verify(documentRepository).save(captor.capture());
        assertEquals(DocumentType.QR_CODE, captor.getValue().getDocumentType());
        assertEquals("image/png", captor.getValue().getContentType());
        assertNotNull(captor.getValue().getSha256Hash());
    }

    @Test
    void generateQrCodeDocument_unissuedQualification_throwsBusinessException() {
        qualification.setSecurityIdentifier(null);
        stubDocumentContext();

        assertThrows(BusinessException.class, () -> documentService.generateQrCodeDocument(qualificationId));
        verify(documentStorageService, never()).store(any(), any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void getDocumentFile_returnsStoredContentWhenHashMatches() {
        final UUID documentId = UUID.randomUUID();
        final byte[] content = "stored-pdf".getBytes(StandardCharsets.UTF_8);
        final QualificationDocument document = QualificationDocument.builder()
                .id(documentId)
                .qualificationId(qualificationId)
                .documentType(DocumentType.CERTIFICATE)
                .fileName("certificate-QUAL-001.pdf")
                .contentType("application/pdf")
                .storageKey("2026/08/certificate.pdf")
                .sha256Hash("96ba548377fcbffcb0b640d90f5f430851e05e595406b1abb494616e601561e6")
                .sizeBytes((long) content.length)
                .qrPayload("AQVP:v1:MSU:sec-123")
                .digitalSignature("signature")
                .signatureAlgorithm("HmacSHA256")
                .signerKeyId("test-key")
                .build();

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(documentStorageService.read("2026/08/certificate.pdf")).thenReturn(content);

        final DocumentFileDto result = documentService.getDocumentFile(documentId);

        assertEquals("certificate-QUAL-001.pdf", result.fileName());
        assertEquals("application/pdf", result.contentType());
        assertArrayEquals(content, result.content());
    }

    @Test
    void getDocumentsForQualification_mapsRepositoryResults() {
        final QualificationDocument document = QualificationDocument.builder()
                .id(UUID.randomUUID())
                .qualificationId(qualificationId)
                .documentType(DocumentType.TRANSCRIPT)
                .fileName("transcript-QUAL-001.pdf")
                .contentType("application/pdf")
                .storageKey("2026/08/transcript.pdf")
                .sha256Hash("hash")
                .sizeBytes(10L)
                .qrPayload("AQVP:v1:MSU:sec-123")
                .digitalSignature("signature")
                .signatureAlgorithm("HmacSHA256")
                .signerKeyId("test-key")
                .build();

        when(qualificationRepository.findById(qualificationId)).thenReturn(Optional.of(qualification));
        when(documentRepository.findByQualificationIdOrderByGeneratedAtDesc(qualificationId))
                .thenReturn(List.of(document));

        final List<DocumentResponseDto> result = documentService.getDocumentsForQualification(qualificationId);

        assertEquals(1, result.size());
        assertEquals("TRANSCRIPT", result.get(0).documentType());
    }

    private void stubDocumentContext() {
        when(qualificationRepository.findById(qualificationId)).thenReturn(Optional.of(qualification));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));
    }
}
