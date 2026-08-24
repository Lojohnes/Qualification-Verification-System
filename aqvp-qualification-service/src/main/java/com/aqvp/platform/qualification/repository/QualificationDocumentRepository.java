package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.QualificationDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for generated qualification document metadata.
 */
public interface QualificationDocumentRepository extends JpaRepository<QualificationDocument, UUID> {

    List<QualificationDocument> findByQualificationIdOrderByGeneratedAtDesc(UUID qualificationId);
}
