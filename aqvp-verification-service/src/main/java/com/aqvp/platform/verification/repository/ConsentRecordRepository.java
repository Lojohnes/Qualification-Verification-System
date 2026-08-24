package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.ConsentRecord;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for consent records.
 */
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, UUID> {

    Optional<ConsentRecord> findTopByVerificationRequestIdOrderByCreatedAtDesc(UUID verificationRequestId);
}
