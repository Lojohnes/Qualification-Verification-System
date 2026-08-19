package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Qualification} entities.
 */
public interface QualificationRepository extends JpaRepository<Qualification, UUID> {

    boolean existsByQualificationNumber(String qualificationNumber);

    Optional<Qualification> findByQualificationNumber(String qualificationNumber);

    Optional<Qualification> findBySecurityIdentifier(String securityIdentifier);

    List<Qualification> findByStudentId(UUID studentId);

    List<Qualification> findByInstitutionId(UUID institutionId);

    List<Qualification> findByStatus(QualificationStatus status);

    List<Qualification> findByInstitutionIdAndStatus(UUID institutionId, QualificationStatus status);
}
