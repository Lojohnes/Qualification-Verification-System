package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Student} entities.
 */
public interface StudentRepository extends JpaRepository<Student, UUID> {

    boolean existsByStudentNumber(String studentNumber);

    boolean existsByEmail(String email);

    Optional<Student> findByStudentNumber(String studentNumber);

    List<Student> findByInstitutionId(UUID institutionId);

    List<Student> findByInstitutionIdAndActiveTrue(UUID institutionId);
}
