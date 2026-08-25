package com.aqvp.platform.admin.repository;

import com.aqvp.platform.admin.domain.AuditEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    default List<AuditEvent> searchEvents(String actorName, String resourceType, String fromDate, String toDate) {
        final LocalDateTime from = fromDate == null || fromDate.isBlank()
            ? LocalDateTime.of(1970, 1, 1, 0, 0)
            : LocalDateTime.of(LocalDate.parse(fromDate), LocalDateTime.MIN.toLocalTime());
        final LocalDateTime to = toDate == null || toDate.isBlank()
            ? LocalDateTime.now()
            : LocalDateTime.of(LocalDate.parse(toDate), LocalDateTime.MAX.toLocalTime());

        if (actorName != null && !actorName.isBlank() && resourceType != null && !resourceType.isBlank()) {
            return findByActorNameContainingIgnoreCaseAndResourceTypeContainingIgnoreCaseAndOccurredAtBetween(
                actorName, resourceType, from, to
            );
        }
        if (actorName != null && !actorName.isBlank()) {
            return findByActorNameContainingIgnoreCaseAndOccurredAtBetween(actorName, from, to);
        }
        if (resourceType != null && !resourceType.isBlank()) {
            return findByResourceTypeContainingIgnoreCaseAndOccurredAtBetween(resourceType, from, to);
        }
        return findByOccurredAtBetween(from, to);
    }

    List<AuditEvent> findByActorNameContainingIgnoreCaseAndResourceTypeContainingIgnoreCaseAndOccurredAtBetween(
        String actorName,
        String resourceType,
        LocalDateTime fromDate,
        LocalDateTime toDate
    );

    List<AuditEvent> findByActorNameContainingIgnoreCaseAndOccurredAtBetween(
        String actorName,
        LocalDateTime fromDate,
        LocalDateTime toDate
    );

    List<AuditEvent> findByResourceTypeContainingIgnoreCaseAndOccurredAtBetween(
        String resourceType,
        LocalDateTime fromDate,
        LocalDateTime toDate
    );

    List<AuditEvent> findByOccurredAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
