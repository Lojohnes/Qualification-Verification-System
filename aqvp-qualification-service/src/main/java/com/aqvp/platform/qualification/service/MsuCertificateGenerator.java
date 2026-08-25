package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.Student;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Generates an MSU-style certificate PDF for a qualification.
 */
public final class MsuCertificateGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MsuCertificateGenerator() {
    }

    @SneakyThrows
    public static byte[] generate(Qualification qualification,
                                  Student student,
                                  Institution institution,
                                  String facultyName,
                                  byte[] qrBytes) {
        final float pageWidth = 842f;
        final float pageHeight = 595f;

        try (PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage(new PDRectangle(pageWidth, pageHeight));
            document.addPage(page);
            final PDPageContentStream cs = new PDPageContentStream(document, page);

            // outer border
            cs.setLineWidth(1.5f);
            cs.addRect(20, 20, pageWidth - 40, pageHeight - 40);
            cs.stroke();

            // inner border
            cs.setLineWidth(0.5f);
            cs.addRect(32, 32, pageWidth - 64, pageHeight - 64);
            cs.stroke();

            final float margin = 60;
            final byte[] logoBytes = loadInstitutionLogo(institution.getCode());
            final PDImageXObject institutionLogo = logoBytes == null
                    ? null
                    : PDImageXObject.createFromByteArray(document, logoBytes, "institution-logo");

            // top corner crests
            drawCrest(cs, 70, pageHeight - 95, institutionLogo);
            drawCrest(cs, pageWidth - 110, pageHeight - 95, institutionLogo);

            // institution name
            final String institutionName = institution.getName().toUpperCase();
            drawCenteredText(cs, pageWidth / 2, pageHeight - 85, institutionName, PDType1Font.HELVETICA_BOLD, 22);

            // coat of arms placeholder
            final float coatOfArmsY = pageHeight - 170;
            final float facultyY = coatOfArmsY - 35;
            final float logoCenterY = (pageHeight - 85 + facultyY) / 2;
            if (institutionLogo != null) {
                cs.drawImage(institutionLogo, pageWidth / 2 - 55, logoCenterY - 40, 110, 80);
            } else {
                drawCoatOfArmsPlaceholder(cs, pageWidth / 2, logoCenterY, 60);
            }

            // faculty
            final String facultyText = (facultyName != null && !facultyName.isBlank())
                    ? "FACULTY OF " + facultyName.toUpperCase()
                    : "FACULTY";
            drawCenteredText(cs, pageWidth / 2, facultyY, facultyText, PDType1Font.HELVETICA_BOLD, 13);

            // qualification name (degree)
            final float degreeY = facultyY - 35;
            drawCenteredText(cs, pageWidth / 2, degreeY, qualification.getQualificationName(),
                    PDType1Font.HELVETICA_BOLD, 16);

            // declaration
            final float declarationY = degreeY - 40;
            drawCenteredText(cs, pageWidth / 2, declarationY, "We hereby Certify that",
                    PDType1Font.HELVETICA_OBLIQUE, 12);

            // student name
            final float nameY = declarationY - 35;
            drawCenteredText(cs, pageWidth / 2, nameY,
                    student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase(),
                    PDType1Font.HELVETICA_BOLD, 18);

            // statement body
            final String classification = defaultIfNull(qualification.getClassification(), "Pass");
            final String statement = "having completed the approved programme of study and having "
                    + "satisfied the Examiners, has this day been admitted by Senate to the "
                    + qualification.getQualificationName() + " in the " + classification + " Category";
            final float bodyY = nameY - 45;
            drawWrappedCenteredText(cs, pageWidth / 2, bodyY, statement, PDType1Font.HELVETICA, 11,
                    pageWidth - 2 * margin - 60, 18);

            // signature lines
            final float sigY = bodyY - 80;
            drawSignature(cs, margin + 40, sigY, "Vice Chancellor");
            drawSignature(cs, pageWidth - margin - 220, sigY, "Registrar");

            // date (under vice chancellor)
            final String dateText = defaultIfNull(formatDate(qualification), LocalDate.now().format(DATE_FORMATTER));
            drawCenteredText(cs, margin + 40 + 90, sigY - 35, "Date: " + dateText,
                    PDType1Font.HELVETICA, 10);

            // red seal
            drawStarSeal(cs, pageWidth / 2, sigY - 30, 35, 18);

            // certificate number bottom right
            final String certNumber = "CERTIFICATE " + qualification.getQualificationNumber();
            drawRightAlignedText(cs, pageWidth - margin, 70, certNumber, PDType1Font.HELVETICA_BOLD, 11);

            // QR code bottom left
            final PDImageXObject qrImage = PDImageXObject.createFromByteArray(document, qrBytes, "qr");
            cs.drawImage(qrImage, pageWidth - margin - 100, degreeY - 20, 100, 100);

            cs.close();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    @SneakyThrows
    private static byte[] loadInstitutionLogo(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) {
            return null;
        }
        final String logoName = institutionCode.trim().toUpperCase();
        for (final String extension : new String[] {".png", ".jpg", ".jpeg", ".gif"}) {
            final String resourcePath = "/logos/" + logoName + extension;
            try (java.io.InputStream stream = MsuCertificateGenerator.class.getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    return stream.readAllBytes();
                }
            }
        }
        return null;
    }

    private static String defaultIfNull(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }

    private static String formatDate(Qualification qualification) {
        return qualification.getIssuedAt() != null
                ? qualification.getIssuedAt().toLocalDate().format(DATE_FORMATTER)
                : null;
    }

    @SneakyThrows
    private static void drawCenteredText(PDPageContentStream cs, float cx, float y, String text,
                                         PDType1Font font, float fontSize) {
        final String safe = text != null ? text : "";
        final float textWidth = font.getStringWidth(safe) * fontSize / 1000f;
        final float x = cx - textWidth / 2f;
        cs.setFont(font, fontSize);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(safe);
        cs.endText();
    }

    @SneakyThrows
    private static void drawRightAlignedText(PDPageContentStream cs, float x, float y, String text,
                                             PDType1Font font, float fontSize) {
        final String safe = text != null ? text : "";
        final float textWidth = font.getStringWidth(safe) * fontSize / 1000f;
        cs.setFont(font, fontSize);
        cs.beginText();
        cs.newLineAtOffset(x - textWidth, y);
        cs.showText(safe);
        cs.endText();
    }

    @SneakyThrows
    private static void drawWrappedCenteredText(PDPageContentStream cs, float cx, float startY, String text,
                                                PDType1Font font, float fontSize, float maxWidth,
                                                float lineHeight) {
        final String safe = text != null ? text : "";
        final List<String> lines = splitToLines(safe, font, fontSize, maxWidth);
        float y = startY;
        cs.setFont(font, fontSize);
        for (final String line : lines) {
            final float textWidth = font.getStringWidth(line) * fontSize / 1000f;
            final float x = cx - textWidth / 2f;
            cs.beginText();
            cs.newLineAtOffset(x, y);
            cs.showText(line);
            cs.endText();
            y -= lineHeight;
        }
    }

    @SneakyThrows
    private static List<String> splitToLines(String text, PDType1Font font, float fontSize, float maxWidth) {
        final List<String> lines = new ArrayList<>();
        final String[] words = text.split(" ");
        final StringBuilder current = new StringBuilder();
        for (final String word : words) {
            final String candidate = current.isEmpty() ? word : current + " " + word;
            final float width = font.getStringWidth(candidate) * fontSize / 1000f;
            if (width > maxWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            } else {
                current.setLength(0);
                current.append(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    @SneakyThrows
    private static void drawCrest(PDPageContentStream cs, float x, float y, PDImageXObject logo) {
        final float size = 45;
        if (logo != null) {
            final float scale = Math.min(size / logo.getWidth(), size / logo.getHeight());
            final float width = logo.getWidth() * scale;
            final float height = logo.getHeight() * scale;
            cs.drawImage(logo, x + (size - width) / 2, y + (size - height) / 2, width, height);
            return;
        }

        cs.setLineWidth(0.8f);
        cs.setStrokingColor(0, 0, 0);
        cs.addRect(x, y, size, size);
        cs.stroke();
        cs.moveTo(x, y);
        cs.lineTo(x + size, y + size);
        cs.stroke();
        cs.moveTo(x + size, y);
        cs.lineTo(x, y + size);
        cs.stroke();

        cs.setFont(PDType1Font.HELVETICA_BOLD, 8);
        cs.beginText();
        cs.newLineAtOffset(x + 8, y + 16);
        cs.showText("MSU");
        cs.endText();
    }

    @SneakyThrows
    private static void drawCoatOfArmsPlaceholder(PDPageContentStream cs, float cx, float cy, float size) {
        cs.setLineWidth(0.8f);
        cs.setStrokingColor(0, 0, 0);

        // shield outline
        final float half = size / 2f;
        cs.moveTo(cx - half, cy - half);
        cs.lineTo(cx + half, cy - half);
        cs.curveTo(cx + half + 10, cy + 10, cx + 10, cy + half + 10, cx, cy + half + 15);
        cs.curveTo(cx - 10, cy + half + 10, cx - half - 10, cy + 10, cx - half, cy - half);
        cs.closePath();
        cs.stroke();

        cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
        cs.beginText();
        final String text = "COAT OF ARMS";
        final float tw = PDType1Font.HELVETICA_BOLD.getStringWidth(text) * 9f / 1000f;
        cs.newLineAtOffset(cx - tw / 2f, cy + half + 20);
        cs.showText(text);
        cs.endText();
    }

    @SneakyThrows
    private static void drawSignature(PDPageContentStream cs, float x, float y, String title) {
        cs.setLineWidth(0.6f);
        cs.setStrokingColor(0, 0, 0);
        cs.moveTo(x, y);
        cs.lineTo(x + 180, y);
        cs.stroke();

        cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
        cs.beginText();
        cs.newLineAtOffset(x, y - 16);
        cs.showText(title);
        cs.endText();
    }

    @SneakyThrows
    private static void drawStarSeal(PDPageContentStream cs, float cx, float cy, float outerR, float innerR) {
        final int points = 12;
        cs.setStrokingColor(0.6f, 0, 0);
        cs.setNonStrokingColor(0.75f, 0, 0);

        for (int i = 0; i < points * 2; i++) {
            final float angle = (float) (i * Math.PI / points - Math.PI / 2);
            final float r = (i % 2 == 0) ? outerR : innerR;
            final float x = cx + (float) Math.cos(angle) * r;
            final float y = cy + (float) Math.sin(angle) * r;
            if (i == 0) {
                cs.moveTo(x, y);
            } else {
                cs.lineTo(x, y);
            }
        }
        cs.closePath();
        cs.fillAndStroke();
    }
}
