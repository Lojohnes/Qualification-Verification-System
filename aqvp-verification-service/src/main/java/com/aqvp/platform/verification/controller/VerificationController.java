package com.aqvp.platform.verification.controller;

import com.aqvp.platform.verification.dto.VerificationRequest;
import com.aqvp.platform.verification.dto.VerificationResponse;
import com.aqvp.platform.verification.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for qualification verification workflows.
 */
@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<VerificationResponse> verify(@Valid @RequestBody VerificationRequest request,
                                                       Principal principal,
                                                       HttpServletRequest httpRequest) {
        final String username = principal != null ? principal.getName() : "anonymous";
        final String ipAddress = extractIpAddress(httpRequest);
        final VerificationResponse response = verificationService.verify(request, username, ipAddress);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VerificationResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK).body(List.of());
    }

    private String extractIpAddress(HttpServletRequest request) {
        final String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isBlank()) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
