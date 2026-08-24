package com.aqvp.platform.verification.repository;

import com.aqvp.platform.verification.domain.VerifiedQualification;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Read-only repository for qualifications stored in the qualification schema.
 */
@Repository
public interface VerifiedQualificationRepository extends JpaRepository<VerifiedQualification, UUID> {

    Optional<VerifiedQualification> findByQualificationNumber(String qualificationNumber);

    Optional<VerifiedQualification> findBySecurityIdentifier(String securityIdentifier);
}
