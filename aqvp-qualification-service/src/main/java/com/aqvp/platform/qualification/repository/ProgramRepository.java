package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Program;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Program} entity.
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    Optional<Program> findByCode(String code);
    boolean existsByCode(String code);
    List<Program> findByInstitutionId(UUID institutionId);
}
