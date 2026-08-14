package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Faculty;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Faculty} entity.
 */
@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    List<Faculty> findByInstitutionId(UUID institutionId);
    Optional<Faculty> findByInstitutionIdAndCode(UUID institutionId, String code);
    boolean existsByInstitutionIdAndCode(UUID institutionId, String code);
}
