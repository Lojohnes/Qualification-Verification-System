package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.VerificationEvidence;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for normalized verification evidence.
 */
public interface VerificationEvidenceRepository extends JpaRepository<VerificationEvidence, UUID> {

    Optional<VerificationEvidence> findTopByVerificationRequestIdOrderByCreatedAtDesc(UUID verificationRequestId);
}
