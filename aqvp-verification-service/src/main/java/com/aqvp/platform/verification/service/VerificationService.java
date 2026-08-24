package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.dto.VerificationRequest;
import com.aqvp.platform.verification.dto.VerificationResponse;

/**
 * Service for verifying qualification records.
 */
public interface VerificationService {

    VerificationResponse verify(VerificationRequest request, String username, String ipAddress);
}
