package com.aqvp.platform.admin.service;

import com.aqvp.platform.admin.domain.AuditEvent;
import com.aqvp.platform.admin.repository.AuditEventRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository auditEventRepository;

    @Override
    @Transactional
    public AuditEvent recordEvent(String eventType,
                                 String action,
                                 UUID actorId,
                                 String actorName,
                                 String actorRole,
                                 String organizationId,
                                 String resourceType,
                                 UUID resourceId,
                                 String resourceName,
                                 String previousValues,
                                 String newValues,
                                 String ipAddress,
                                 String deviceInfo) {
        final AuditEvent event = AuditEvent.builder()
            .eventType(eventType)
            .action(action)
            .actorId(actorId)
            .actorName(actorName)
            .actorRole(actorRole)
            .organizationId(organizationId)
            .resourceType(resourceType)
            .resourceId(resourceId)
            .resourceName(resourceName)
            .previousValues(previousValues)
            .newValues(newValues)
            .ipAddress(ipAddress)
            .deviceInfo(deviceInfo)
            .occurredAt(LocalDateTime.now())
            .build();
        return auditEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> searchEvents(String actorName, String resourceType, String fromDate, String toDate) {
        final LocalDateTime from = parseDate(fromDate, true);
        final LocalDateTime to = parseDate(toDate, false);

        if (actorName != null && !actorName.isBlank() && resourceType != null && !resourceType.isBlank()) {
            return auditEventRepository.searchEvents(actorName, resourceType, fromDate, toDate);
        }
        if (actorName != null && !actorName.isBlank()) {
            return auditEventRepository.searchEvents(actorName, null, fromDate, toDate);
        }
        if (resourceType != null && !resourceType.isBlank()) {
            return auditEventRepository.searchEvents(null, resourceType, fromDate, toDate);
        }
        return auditEventRepository.searchEvents(null, null, fromDate, toDate);
    }

    private LocalDateTime parseDate(String value, boolean startOfDay) {
        if (value == null || value.isBlank()) {
            return startOfDay ? LocalDateTime.of(1970, 1, 1, 0, 0) : LocalDateTime.now();
        }
        final LocalDate parsed = LocalDate.parse(value);
        return startOfDay ? LocalDateTime.of(parsed, LocalTime.MIN) : LocalDateTime.of(parsed, LocalTime.MAX);
    }
}
