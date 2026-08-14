package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Institution;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Institution} entity.
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    Optional<Institution> findByCode(String code);
    boolean existsByCode(String code);
}
