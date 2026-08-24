package com.aqvp.platform.verification.controller;

import com.aqvp.platform.verification.dto.QrVerificationRequestDto;
import com.aqvp.platform.verification.dto.VerificationResultResponseDto;
import com.aqvp.platform.verification.service.VerificationEngineService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for direct verification actions and results.
 */
@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationEngineService verificationEngineService;

    @PostMapping("/qr")
    public ResponseEntity<VerificationResultResponseDto> verifyQr(
            @Valid @RequestBody QrVerificationRequestDto dto,
            Principal principal) {
        return ResponseEntity.ok(verificationEngineService.verifyQr(dto, principalName(principal)));
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<VerificationResultResponseDto> getResult(@PathVariable UUID resultId) {
        return ResponseEntity.ok(verificationEngineService.getResult(resultId));
    }

    private String principalName(Principal principal) {
        return principal == null ? "system" : principal.getName();
    }
}
