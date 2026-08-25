package com.aqvp.platform.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.admin.domain.AuditEvent;
import com.aqvp.platform.admin.repository.AuditEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditEventRepository auditEventRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    void shouldRecordAuditEvent() {
        final UUID actorId = UUID.randomUUID();
        final UUID resourceId = UUID.randomUUID();
        final AuditEvent saved = AuditEvent.builder()
            .id(UUID.randomUUID())
            .eventType("qualification.issued")
            .action("ISSUE")
            .actorId(actorId)
            .actorName("admin")
            .resourceType("qualification")
            .resourceId(resourceId)
            .resourceName("Q-1001")
            .createdAt(LocalDateTime.now())
            .build();

        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(saved);

        final AuditEvent result = auditService.recordEvent(
            "qualification.issued",
            "ISSUE",
            actorId,
            "admin",
            "ADMIN",
            "org-1",
            "qualification",
            resourceId,
            "Q-1001",
            "{\"status\":\"DRAFT\"}",
            "{\"status\":\"ISSUED\"}",
            "127.0.0.1",
            "Mozilla/5.0");

        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo("qualification.issued");
        assertThat(result.getAction()).isEqualTo("ISSUE");
        verify(auditEventRepository, times(1)).save(any(AuditEvent.class));
    }

    @Test
    void shouldSearchEventsByActorAndResource() {
        when(auditEventRepository.searchEvents("admin", "qualification", "2026-08-01", "2026-08-31"))
            .thenReturn(List.of(AuditEvent.builder().eventType("qualification.issued").build()));

        final List<AuditEvent> result = auditService.searchEvents(
            "admin",
            "qualification",
            "2026-08-01",
            "2026-08-31");

        assertThat(result).hasSize(1);
    }
}
