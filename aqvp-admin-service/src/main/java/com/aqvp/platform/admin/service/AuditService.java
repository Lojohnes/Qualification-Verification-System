package com.aqvp.platform.admin.service;

import com.aqvp.platform.admin.domain.AuditEvent;
import java.util.List;
import java.util.UUID;

public interface AuditService {

    AuditEvent recordEvent(String eventType,
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
                          String deviceInfo);

    List<AuditEvent> searchEvents(String actorName, String resourceType, String fromDate, String toDate);
}
