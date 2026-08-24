package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.QualificationVerificationSnapshotDto;
import com.aqvp.platform.qualification.service.QualificationVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal read-only endpoints used by the Verification service.
 */
@RestController
@RequestMapping("/api/v1/internal/qualifications/verification-snapshots")
@RequiredArgsConstructor
public class QualificationVerificationController {

    private final QualificationVerificationService qualificationVerificationService;

    @GetMapping("/by-security-identifier/{securityIdentifier}")
    public ResponseEntity<QualificationVerificationSnapshotDto> getBySecurityIdentifier(
            @PathVariable String securityIdentifier) {
        return ResponseEntity.ok(qualificationVerificationService.getBySecurityIdentifier(securityIdentifier));
    }

    @GetMapping("/by-number/{qualificationNumber}")
    public ResponseEntity<QualificationVerificationSnapshotDto> getByQualificationNumber(
            @PathVariable String qualificationNumber) {
        return ResponseEntity.ok(qualificationVerificationService.getByQualificationNumber(qualificationNumber));
    }
}
