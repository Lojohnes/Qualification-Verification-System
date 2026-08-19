package com.aqvp.platform.qualification.repository;

import com.aqvp.platform.qualification.domain.QualificationStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link QualificationStatusHistory} entries.
 */
public interface QualificationStatusHistoryRepository extends JpaRepository<QualificationStatusHistory, UUID> {

    List<QualificationStatusHistory> findByQualificationIdOrderByChangedAtAsc(UUID qualificationId);
}
