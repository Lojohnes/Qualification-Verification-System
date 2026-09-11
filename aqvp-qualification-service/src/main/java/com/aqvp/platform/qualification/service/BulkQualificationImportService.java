package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.domain.QualificationType;
import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.BulkImportErrorDto;
import com.aqvp.platform.qualification.dto.BulkImportResponseDto;
import com.aqvp.platform.qualification.dto.QualificationIssueRequestDto;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import com.aqvp.platform.qualification.repository.QualificationRepository;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Validates and imports qualification records from the approved CSV format. */
@Service
@RequiredArgsConstructor
public class BulkQualificationImportService {

    private static final List<String> HEADERS = List.of(
        "Student ID", "Recipient Name", "Programme", "Qualification", "Class", "Graduation Year");

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final QualificationRepository qualificationRepository;
    private final QualificationService qualificationService;
    private final DocumentService documentService;

    @Transactional(readOnly = true)
    public BulkImportResponseDto preview(MultipartFile file, UUID institutionId) {
        final ParsedImport parsed = parse(file, institutionId);
        return response(parsed.totalRows(), parsed.validRows(), 0, parsed.errors());
    }

    @Transactional
    public BulkImportResponseDto confirm(MultipartFile file, UUID institutionId, String importedBy) {
        final ParsedImport parsed = parse(file, institutionId);
        if (!parsed.errors().isEmpty()) {
            return response(parsed.totalRows(), parsed.validRows(), 0, parsed.errors());
        }

        int imported = 0;
        for (ImportRow row : parsed.rows()) {
            final QualificationRequestDto request = new QualificationRequestDto(
                "BULK-" + row.student().getStudentNumber() + "-" + row.year(),
                row.student().getId(), institutionId, row.program().getId(),
                qualificationType(row.qualification()), row.qualification(), row.classification(), row.year(), null);
            final var qualification = qualificationService.createQualification(request);
            qualificationService.issueQualification(qualification.id(), new QualificationIssueRequestDto(null), importedBy);
            documentService.generateCertificateDocument(qualification.id());
            documentService.generateQrCodeDocument(qualification.id());
            imported++;
        }
        return response(parsed.totalRows(), parsed.validRows(), imported, List.of());
    }

    private ParsedImport parse(MultipartFile file, UUID institutionId) {
        final List<ImportRow> rows = new ArrayList<>();
        final List<BulkImportErrorDto> errors = new ArrayList<>();
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            errors.add(new BulkImportErrorDto(0, "file", "A non-empty .csv file is required"));
            return new ParsedImport(rows, errors, 0);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            final String header = reader.readLine();
            if (header == null || !parseLine(header).equals(HEADERS)) {
                errors.add(new BulkImportErrorDto(1, "header", "Expected: " + String.join(",", HEADERS)));
                return new ParsedImport(rows, errors, 0);
            }
            final Set<String> studentIds = new HashSet<>();
            String line;
            int rowNumber = 1;
            int totalRows = 0;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) continue;
                totalRows++;
                final List<String> values = parseLine(line);
                if (values.size() != HEADERS.size()) {
                    errors.add(new BulkImportErrorDto(rowNumber, "row", "Expected six CSV columns"));
                    continue;
                }
                validateRow(values, rowNumber, institutionId, studentIds, rows, errors);
            }
            return new ParsedImport(rows, errors, totalRows);
        } catch (IOException exception) {
            errors.add(new BulkImportErrorDto(0, "file", "Unable to read CSV file"));
        }
        return new ParsedImport(rows, errors, rows.size());
    }

    private void validateRow(List<String> values, int rowNumber, UUID institutionId, Set<String> studentIds,
                             List<ImportRow> rows, List<BulkImportErrorDto> errors) {
        final String studentNumber = values.get(0).trim();
        final String recipient = values.get(1).trim();
        final String programme = values.get(2).trim();
        final String qualification = values.get(3).trim();
        final String classification = values.get(4).trim();
        final String yearText = values.get(5).trim();
        final int before = errors.size();
        if (studentNumber.isBlank()) addError(errors, rowNumber, "Student ID", "Required");
        if (recipient.isBlank()) addError(errors, rowNumber, "Recipient Name", "Required");
        if (programme.isBlank()) addError(errors, rowNumber, "Programme", "Required");
        if (qualification.isBlank()) addError(errors, rowNumber, "Qualification", "Required");
        if (classification.isBlank()) addError(errors, rowNumber, "Class", "Required");
        int year = 0;
        try {
            year = Integer.parseInt(yearText);
            if (year < 1900 || year > 2200) addError(errors, rowNumber, "Graduation Year", "Must be between 1900 and 2200");
        } catch (NumberFormatException exception) {
            addError(errors, rowNumber, "Graduation Year", "Must be a number");
        }
        if (!studentIds.add(studentNumber)) addError(errors, rowNumber, "Student ID", "Duplicate record in file");
        final Student student = studentRepository.findByStudentNumber(studentNumber).orElse(null);
        if (student == null) addError(errors, rowNumber, "Student ID", "Student was not found");
        else if (!student.getInstitutionId().equals(institutionId)) addError(errors, rowNumber, "Student ID", "Student belongs to another institution");
        else if (!fullName(student).equalsIgnoreCase(recipient)) addError(errors, rowNumber, "Recipient Name", "Does not match the student record");
        final Program program = programRepository.findByInstitutionId(institutionId).stream()
            .filter(candidate -> candidate.getName().equalsIgnoreCase(programme)).findFirst().orElse(null);
        if (program == null) addError(errors, rowNumber, "Programme", "Programme was not found");
        if (before == errors.size() && qualificationRepository.existsByQualificationNumber("BULK-" + studentNumber + "-" + year)) {
            addError(errors, rowNumber, "Student ID", "Qualification already exists for this year");
        }
        if (before == errors.size()) rows.add(new ImportRow(student, program, qualification, classification, year));
    }

    private String qualificationType(String qualification) {
        final String value = qualification.toLowerCase(Locale.ROOT);
        if (value.contains("diploma")) return QualificationType.DIPLOMA.name();
        if (value.contains("certificate")) return QualificationType.CERTIFICATE.name();
        return QualificationType.DEGREE.name();
    }

    private String fullName(Student student) { return student.getFirstName() + " " + student.getLastName(); }

    private List<String> parseLine(String line) {
        final List<String> values = new ArrayList<>();
        final StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            final char character = line.charAt(index);
            if (character == '"') quoted = !quoted;
            else if (character == ',' && !quoted) { values.add(value.toString()); value.setLength(0); }
            else value.append(character);
        }
        values.add(value.toString());
        return values;
    }

    private void addError(List<BulkImportErrorDto> errors, int row, String field, String message) {
        errors.add(new BulkImportErrorDto(row, field, message));
    }

    private BulkImportResponseDto response(int total, int valid, int imported, List<BulkImportErrorDto> errors) {
        return new BulkImportResponseDto(total, valid, imported, List.copyOf(errors));
    }

    private record ImportRow(Student student, Program program, String qualification, String classification, int year) {}
    private record ParsedImport(List<ImportRow> rows, List<BulkImportErrorDto> errors, int totalRows) {
        int validRows() { return rows.size(); }
    }
}