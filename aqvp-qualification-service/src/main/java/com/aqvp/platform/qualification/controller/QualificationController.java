package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.QualificationAmendRequestDto;
import com.aqvp.platform.qualification.dto.QualificationIssueRequestDto;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.dto.QualificationRevokeRequestDto;
import com.aqvp.platform.qualification.service.QualificationService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing qualification records.
 *
 * <p>Lifecycle mutations (issue, amend, revoke) use sub-resource action endpoints so that
 * the principal performing each action is captured for auditing.
 */
@RestController
@RequestMapping("/api/v1/qualifications")
@RequiredArgsConstructor
public class QualificationController {

    private final QualificationService qualificationService;

    @PostMapping
    public ResponseEntity<QualificationResponseDto> createQualification(
            @Valid @RequestBody QualificationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(qualificationService.createQualification(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QualificationResponseDto> updateQualification(
            @PathVariable UUID id,
            @Valid @RequestBody QualificationRequestDto dto) {
        return ResponseEntity.ok(qualificationService.updateQualification(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QualificationResponseDto> getQualificationById(@PathVariable UUID id) {
        return ResponseEntity.ok(qualificationService.getQualificationById(id));
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<QualificationResponseDto>> getQualificationsByStudent(
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(qualificationService.getQualificationsByStudent(studentId));
    }

    @GetMapping
    public ResponseEntity<List<QualificationResponseDto>> getQualificationsByInstitution(
            @RequestParam UUID institutionId) {
        return ResponseEntity.ok(qualificationService.getQualificationsByInstitution(institutionId));
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<QualificationResponseDto> issueQualification(
            @PathVariable UUID id,
            @RequestBody(required = false) QualificationIssueRequestDto dto,
            Principal principal) {
        final String issuedBy = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(qualificationService.issueQualification(id, dto, issuedBy));
    }

    @PostMapping("/{id}/amend")
    public ResponseEntity<QualificationResponseDto> amendQualification(
            @PathVariable UUID id,
            @Valid @RequestBody QualificationAmendRequestDto dto,
            Principal principal) {
        final String amendedBy = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(qualificationService.amendQualification(id, dto, amendedBy));
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<QualificationResponseDto> revokeQualification(
            @PathVariable UUID id,
            @Valid @RequestBody QualificationRevokeRequestDto dto,
            Principal principal) {
        final String revokedBy = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(qualificationService.revokeQualification(id, dto, revokedBy));
    }
}
