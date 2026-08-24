package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.DocumentType;
import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationDocument;
import com.aqvp.platform.qualification.domain.QualificationStatusHistory;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.DocumentFileDto;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import com.aqvp.platform.qualification.exception.BusinessException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.DocumentMapper;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import com.aqvp.platform.qualification.repository.QualificationDocumentRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates, stores, signs and retrieves qualification documents and QR codes.
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PNG_CONTENT_TYPE = "image/png";

    private final QualificationRepository qualificationRepository;
    private final StudentRepository studentRepository;
    private final InstitutionRepository institutionRepository;
    private final ProgramRepository programRepository;
    private final QualificationDocumentRepository documentRepository;
    private final DocumentStorageService documentStorageService;
    private final DigitalSignatureService digitalSignatureService;
    private final DocumentMapper documentMapper;

    @Override
    @Transactional
    public byte[] generateCertificate(UUID qualificationId) {
        return generateCertificateArtifact(qualificationId).content();
    }

    @Override
    @Transactional
    public byte[] generateTranscript(UUID qualificationId) {
        return generateTranscriptArtifact(qualificationId).content();
    }

    @Override
    @Transactional
    public byte[] generateQrCode(UUID qualificationId) {
        return generateQrCodeArtifact(qualificationId).content();
    }

    @Override
    @Transactional
    public DocumentResponseDto generateCertificateDocument(UUID qualificationId) {
        return documentMapper.toResponseDto(generateCertificateArtifact(qualificationId).document());
    }

    @Override
    @Transactional
    public DocumentResponseDto generateTranscriptDocument(UUID qualificationId) {
        return documentMapper.toResponseDto(generateTranscriptArtifact(qualificationId).document());
    }

    @Override
    @Transactional
    public DocumentResponseDto generateQrCodeDocument(UUID qualificationId) {
        return documentMapper.toResponseDto(generateQrCodeArtifact(qualificationId).document());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getDocumentsForQualification(UUID qualificationId) {
        findQualification(qualificationId);
        return documentRepository.findByQualificationIdOrderByGeneratedAtDesc(qualificationId)
                .stream()
                .map(documentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDto getDocument(UUID documentId) {
        return documentMapper.toResponseDto(findDocument(documentId));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFileDto getDocumentFile(UUID documentId) {
        final QualificationDocument document = findDocument(documentId);
        final byte[] content = documentStorageService.read(document.getStorageKey());
        final String actualHash = sha256(content);
        if (!document.getSha256Hash().equals(actualHash)) {
            throw new BusinessException("Stored document hash does not match metadata");
        }
        return new DocumentFileDto(document.getFileName(), document.getContentType(), content);
    }

    @SneakyThrows
    private GeneratedDocument generateCertificateArtifact(UUID qualificationId) {
        final DocumentContext context = resolveContext(qualificationId);
        final byte[] qrBytes = buildQrImage(context.qrPayload());
        final String facultyName = resolveFacultyName(context.qualification().getProgramId());
        final byte[] pdf = MsuCertificateGenerator.generate(
                context.qualification(), context.student(), context.institution(), facultyName, qrBytes);
        final String fileName = "certificate-" + context.qualification().getQualificationNumber() + ".pdf";
        final QualificationDocument document = persistDocument(
                context.qualification(), DocumentType.CERTIFICATE, fileName, PDF_CONTENT_TYPE,
                context.qrPayload(), pdf);
        return new GeneratedDocument(document, pdf);
    }

    @SneakyThrows
    private GeneratedDocument generateTranscriptArtifact(UUID qualificationId) {
        final DocumentContext context = resolveContext(qualificationId);
        final byte[] pdf = buildTranscriptPdf(context.qualification(), context.student(), context.institution());
        final String fileName = "transcript-" + context.qualification().getQualificationNumber() + ".pdf";
        final QualificationDocument document = persistDocument(
                context.qualification(), DocumentType.TRANSCRIPT, fileName, PDF_CONTENT_TYPE,
                context.qrPayload(), pdf);
        return new GeneratedDocument(document, pdf);
    }

    @SneakyThrows
    private GeneratedDocument generateQrCodeArtifact(UUID qualificationId) {
        final DocumentContext context = resolveContext(qualificationId);
        final byte[] png = buildQrImage(context.qrPayload());
        final String fileName = "qr-" + context.qualification().getQualificationNumber() + ".png";
        final QualificationDocument document = persistDocument(
                context.qualification(), DocumentType.QR_CODE, fileName, PNG_CONTENT_TYPE,
                context.qrPayload(), png);
        return new GeneratedDocument(document, png);
    }

    private QualificationDocument persistDocument(Qualification qualification,
                                                  DocumentType documentType,
                                                  String fileName,
                                                  String contentType,
                                                  String qrPayload,
                                                  byte[] content) {
        final String storageKey = documentStorageService.store(fileName, content);
        final QualificationDocument document = QualificationDocument.builder()
                .qualificationId(qualification.getId())
                .documentType(documentType)
                .fileName(fileName)
                .contentType(contentType)
                .storageKey(storageKey)
                .sha256Hash(sha256(content))
                .sizeBytes((long) content.length)
                .qrPayload(qrPayload)
                .digitalSignature(digitalSignatureService.sign(qrPayload, content))
                .signatureAlgorithm(digitalSignatureService.algorithm())
                .signerKeyId(digitalSignatureService.signerKeyId())
                .generatedAt(LocalDateTime.now())
                .build();
        return documentRepository.save(document);
    }

    private DocumentContext resolveContext(UUID qualificationId) {
        final Qualification qualification = findQualification(qualificationId);
        final Student student = findStudent(qualification.getStudentId());
        final Institution institution = findInstitution(qualification.getInstitutionId());
        final String qrPayload = buildQrPayload(qualification, institution);
        return new DocumentContext(qualification, student, institution, qrPayload);
    }

    private String buildQrPayload(Qualification qualification, Institution institution) {
        if (qualification.getSecurityIdentifier() == null || qualification.getSecurityIdentifier().isBlank()) {
            throw new BusinessException("Qualification must be issued before generating verifiable documents");
        }
        return "AQVP:v1:" + institution.getCode() + ":" + qualification.getSecurityIdentifier();
    }

    @SneakyThrows
    private byte[] buildQrImage(String qrPayload) {
        final QRCodeWriter writer = new QRCodeWriter();
        final Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        final BitMatrix bitMatrix = writer.encode(qrPayload, BarcodeFormat.QR_CODE, 400, 400, hints);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                out,
                new com.google.zxing.client.j2se.MatrixToImageConfig(
                        Color.BLACK.getRGB(), Color.WHITE.getRGB()));
        return out.toByteArray();
    }

    @SneakyThrows
    private byte[] buildTranscriptPdf(Qualification qualification, Student student, Institution institution) {
        try (PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(document, page);

            float y = page.getMediaBox().getHeight() - 80;
            final float margin = 60;

            cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("QUALIFICATION TRANSCRIPT");
            cs.endText();

            y -= 50;
            drawLabelValue(cs, margin, y, "Qualification Number", qualification.getQualificationNumber());
            y -= 28;
            drawLabelValue(cs, margin, y, "Qualification", qualification.getQualificationName());
            y -= 28;
            drawLabelValue(cs, margin, y, "Holder", student.getFirstName() + " " + student.getLastName());
            y -= 28;
            drawLabelValue(cs, margin, y, "Institution", institution.getName());
            y -= 28;
            drawLabelValue(cs, margin, y, "Status", qualification.getStatus().name());
            y -= 28;
            if (qualification.getSecurityIdentifier() != null) {
                drawLabelValue(cs, margin, y, "Security Identifier", qualification.getSecurityIdentifier());
                y -= 28;
            }

            y -= 40;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("Status History");
            cs.endText();

            y -= 30;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("Changed At             From          To            Changed By             Reason");
            cs.endText();

            y -= 18;
            cs.setFont(PDType1Font.HELVETICA, 10);
            for (final QualificationStatusHistory entry : qualification.getStatusHistory()) {
                if (y < 80) {
                    cs.close();
                    final PDPage next = new PDPage(PDRectangle.A4);
                    document.addPage(next);
                    cs = new PDPageContentStream(document, next);
                    y = next.getMediaBox().getHeight() - 80;
                    cs.setFont(PDType1Font.HELVETICA, 10);
                }
                final String previous = defaultIfNull(entry.getPreviousStatus(), "-");
                final String reason = truncate(defaultIfNull(entry.getReason(), "-"), 35);
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(String.format("%-22s %-13s %-13s %-22s %s",
                        entry.getChangedAt() != null ? entry.getChangedAt().format(DATE_TIME_FORMATTER) : "-",
                        previous,
                        entry.getNewStatus(),
                        defaultIfNull(entry.getChangedBy(), "-"),
                        reason));
                cs.endText();
                y -= 16;
            }

            cs.close();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private Qualification findQualification(UUID id) {
        return qualificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Qualification not found with id: " + id));
    }

    private QualificationDocument findDocument(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
    }

    private Student findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }

    private Institution findInstitution(UUID id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + id));
    }

    private String resolveFacultyName(UUID programId) {
        return Optional.ofNullable(programId)
                .flatMap(programRepository::findById)
                .map(Program::getDepartment)
                .map(department -> department.getFaculty().getName())
                .orElse(null);
    }

    private String sha256(byte[] content) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (Exception ex) {
            throw new BusinessException("Unable to hash generated document", ex);
        }
    }

    private String defaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private String truncate(String value, int maxLength) {
        return value.length() > maxLength ? value.substring(0, maxLength - 3) + "..." : value;
    }

    @SneakyThrows
    private void drawLabelValue(PDPageContentStream cs, float x, float y, String label, String value) {
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        cs.setFont(PDType1Font.HELVETICA, 11);
        cs.beginText();
        cs.newLineAtOffset(x + 170, y);
        final String safe = value != null ? value : "-";
        cs.showText(truncate(safe, 70));
        cs.endText();
    }

    private record DocumentContext(
        Qualification qualification,
        Student student,
        Institution institution,
        String qrPayload
    ) {}

    private record GeneratedDocument(
        QualificationDocument document,
        byte[] content
    ) {}
}
