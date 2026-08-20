package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import com.aqvp.platform.qualification.domain.QualificationStatusHistory;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates qualification certificates, transcripts and QR codes.
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final QualificationRepository qualificationRepository;
    private final StudentRepository studentRepository;
    private final InstitutionRepository institutionRepository;
    private final ProgramRepository programRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    @SneakyThrows
    public byte[] generateCertificate(UUID qualificationId) {
        final Qualification qualification = findQualification(qualificationId);
        final Student student = findStudent(qualification.getStudentId());
        final Institution institution = findInstitution(qualification.getInstitutionId());
        final String facultyName = resolveFacultyName(qualification.getProgramId());

        final byte[] qrBytes = buildQrImage(qualification, student, institution);
        return MsuCertificateGenerator.generate(qualification, student, institution, facultyName, qrBytes);
    }

    @Override
    @Transactional(readOnly = true)
    @SneakyThrows
    public byte[] generateTranscript(UUID qualificationId) {
        final Qualification qualification = findQualification(qualificationId);
        final Student student = findStudent(qualification.getStudentId());
        final Institution institution = findInstitution(qualification.getInstitutionId());

        try (PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            final PDPageContentStream cs = new PDPageContentStream(document, page);

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
            cs.showText(String.format("%-22s %-14s %-14s %-22s %s", "Changed At", "From", "To", "Changed By", "Reason"));
            cs.endText();

            y -= 18;
            cs.setFont(PDType1Font.HELVETICA, 10);
            for (final QualificationStatusHistory entry : qualification.getStatusHistory()) {
                if (y < 80) {
                    cs.close();
                    final PDPage next = new PDPage(PDRectangle.A4);
                    document.addPage(next);
                    y = next.getMediaBox().getHeight() - 80;
                }
                final String previous = defaultIfNull(entry.getPreviousStatus(), "-");
                final String reason = defaultIfNull(entry.getReason(), "-");
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText(String.format("%-22s %-14s %-14s %-22s %s",
                        entry.getChangedAt() != null ? entry.getChangedAt().format(DATE_TIME_FORMATTER) : "-",
                        previous,
                        entry.getNewStatus(),
                        defaultIfNull(entry.getChangedBy(), "-"),
                        reason.length() > 35 ? reason.substring(0, 32) + "..." : reason));
                cs.endText();
                y -= 16;
            }

            cs.close();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SneakyThrows
    public byte[] generateQrCode(UUID qualificationId) {
        final Qualification qualification = findQualification(qualificationId);
        final Student student = findStudent(qualification.getStudentId());
        final Institution institution = findInstitution(qualification.getInstitutionId());
        return buildQrImage(qualification, student, institution);
    }

    @SneakyThrows
    private byte[] buildQrImage(Qualification qualification, Student student, Institution institution) {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("qualificationNumber", qualification.getQualificationNumber());
        payload.put("qualificationName", qualification.getQualificationName());
        payload.put("studentNumber", student.getStudentNumber());
        payload.put("studentName", student.getFirstName() + " " + student.getLastName());
        payload.put("institutionName", institution.getName());
        payload.put("yearOfAward", qualification.getYearOfAward());
        payload.put("classification", defaultIfNull(qualification.getClassification(), "-"));
        payload.put("status", qualification.getStatus().name());
        payload.put("securityIdentifier", defaultIfNull(qualification.getSecurityIdentifier(), "-"));

        final String json = objectMapper.writeValueAsString(payload);
        final QRCodeWriter writer = new QRCodeWriter();
        final Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        final BitMatrix bitMatrix = writer.encode(json, BarcodeFormat.QR_CODE, 400, 400, hints);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out, new com.google.zxing.client.j2se.MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB()));
        return out.toByteArray();
    }

    private Qualification findQualification(UUID id) {
        return qualificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Qualification not found with id: " + id));
    }

    private Student findStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }

    private Institution findInstitution(UUID id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + id));
    }

    private String resolveProgramName(UUID programId) {
        return Optional.ofNullable(programId)
                .flatMap(programRepository::findById)
                .map(Program::getName)
                .orElse(null);
    }

    private String resolveFacultyName(UUID programId) {
        return Optional.ofNullable(programId)
                .flatMap(programRepository::findById)
                .map(program -> program.getDepartment().getFaculty().getName())
                .orElse(null);
    }

    private String defaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
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
        cs.showText(safe.length() > 70 ? safe.substring(0, 67) + "..." : safe);
        cs.endText();
    }
}
