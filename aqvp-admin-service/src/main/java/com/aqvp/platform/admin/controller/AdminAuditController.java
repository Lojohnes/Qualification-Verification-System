package com.aqvp.platform.admin.controller;

import com.aqvp.platform.admin.domain.AuditEvent;
import com.aqvp.platform.admin.dto.AuditEventRequestDto;
import com.aqvp.platform.admin.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "admin-audit", description = "Administrative audit record endpoints")
public class AdminAuditController {

    private final AuditService auditService;

    @Operation(summary = "Record an audit event", description = "Persist a business or security event for traceability.")
    @ApiResponse(responseCode = "201", description = "Audit event recorded successfully")
    @PostMapping("/audit-events")
    public ResponseEntity<AuditEvent> recordAuditEvent(@Valid @RequestBody AuditEventRequestDto dto) {
        final AuditEvent saved = auditService.recordEvent(
            dto.eventType(),
            dto.action(),
            dto.actorId(),
            dto.actorName(),
            dto.actorRole(),
            dto.organizationId(),
            dto.resourceType(),
            dto.resourceId(),
            dto.resourceName(),
            dto.previousValues(),
            dto.newValues(),
            dto.ipAddress(),
            dto.deviceInfo());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Search audit events", description = "Search immutable audit events by actor, resource, and date range.")
    @ApiResponse(responseCode = "200", description = "Audit events found")
    @GetMapping("/audit-events")
    public ResponseEntity<List<AuditEvent>> searchAuditEvents(
        @RequestParam(required = false) String actorName,
        @RequestParam(required = false) String resourceType,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(auditService.searchEvents(actorName, resourceType, fromDate, toDate));
    }
}
