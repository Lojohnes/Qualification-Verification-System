package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.domain.VerificationOutcome;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Placeholder audit publisher until the Admin/Audit module is implemented.
 */
@Component
@Slf4j
public class AuditEventPublisher {

    public void verificationCompleted(UUID requestId, VerificationOutcome outcome) {
        log.info("Verification completed: requestId={}, outcome={}", requestId, outcome);
    }
}
