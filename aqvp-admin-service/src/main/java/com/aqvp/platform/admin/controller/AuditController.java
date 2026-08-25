package com.aqvp.platform.admin.controller;

import com.aqvp.platform.admin.domain.AuditEvent;
import com.aqvp.platform.admin.service.AuditService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/audit-events")
    public ResponseEntity<List<AuditEvent>> searchAuditEvents(
        @RequestParam(required = false) String actorName,
        @RequestParam(required = false) String resourceType,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(auditService.searchEvents(actorName, resourceType, fromDate, toDate));
    }
}
