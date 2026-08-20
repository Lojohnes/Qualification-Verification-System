package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.VerificationRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for verification requests.
 */
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, UUID> {

    boolean existsByRequestReference(String requestReference);

    Optional<VerificationRequest> findByRequestReference(String requestReference);

    List<VerificationRequest> findByRequesterUsernameOrderByCreatedAtDesc(String requesterUsername);
}
