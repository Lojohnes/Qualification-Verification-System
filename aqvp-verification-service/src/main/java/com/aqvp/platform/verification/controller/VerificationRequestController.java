package com.aqvp.platform.verification.controller;

import com.aqvp.platform.verification.dto.ConsentRequestDto;
import com.aqvp.platform.verification.dto.ConsentValidationResponseDto;
import com.aqvp.platform.verification.dto.CreateVerificationRequestDto;
import com.aqvp.platform.verification.dto.QrVerificationForRequestDto;
import com.aqvp.platform.verification.dto.VerificationRequestResponseDto;
import com.aqvp.platform.verification.dto.VerificationResultResponseDto;
import com.aqvp.platform.verification.service.VerificationEngineService;
import com.aqvp.platform.verification.service.VerificationRequestService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for tracked verification requests.
 */
@RestController
@RequestMapping("/api/v1/verification-requests")
@RequiredArgsConstructor
public class VerificationRequestController {

    private final VerificationRequestService verificationRequestService;
    private final VerificationEngineService verificationEngineService;

    @PostMapping
    public ResponseEntity<VerificationRequestResponseDto> createRequest(
            @Valid @RequestBody CreateVerificationRequestDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(verificationRequestService.createRequest(dto, principalName(principal)));
    }

    @GetMapping
    public ResponseEntity<List<VerificationRequestResponseDto>> listRequests() {
        return ResponseEntity.ok(verificationRequestService.listRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VerificationRequestResponseDto> getRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(verificationRequestService.getRequest(id));
    }

    @PostMapping("/{id}/consent-validation")
    public ResponseEntity<ConsentValidationResponseDto> validateConsent(
            @PathVariable UUID id,
            @Valid @RequestBody ConsentRequestDto dto,
            Principal principal) {
        return ResponseEntity.ok(verificationRequestService.validateConsent(id, dto, principalName(principal)));
    }

    @PostMapping("/{id}/qr-verification")
    public ResponseEntity<VerificationResultResponseDto> verifyQrForRequest(
            @PathVariable UUID id,
            @Valid @RequestBody QrVerificationForRequestDto dto,
            Principal principal) {
        return ResponseEntity.ok(verificationEngineService.verifyRequestQr(id, dto, principalName(principal)));
    }

    private String principalName(Principal principal) {
        return principal == null ? "system" : principal.getName();
    }
}
