package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.Verification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for verification attempts.
 */
@Repository
public interface VerificationRepository extends JpaRepository<Verification, UUID> {
}
