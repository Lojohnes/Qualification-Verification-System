package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Department;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Department} entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByFacultyId(UUID facultyId);
    Optional<Department> findByFacultyIdAndCode(UUID facultyId, String code);
    boolean existsByFacultyIdAndCode(UUID facultyId, String code);
}
