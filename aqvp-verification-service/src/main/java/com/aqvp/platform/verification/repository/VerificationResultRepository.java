package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.VerificationResult;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for persisted verification results.
 */
public interface VerificationResultRepository extends JpaRepository<VerificationResult, UUID> {

    Optional<VerificationResult> findTopByVerificationRequestIdOrderByVerifiedAtDesc(UUID verificationRequestId);
}
