package com.aqvp.platform.qualification.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class AuditEventPublisher {

    private final RestClient restClient;

    public AuditEventPublisher(RestClient.Builder builder,
                               @Value("${aqvp.services.admin.base-url:http://localhost:8084}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public void publish(String eventType, String action, String resourceType, UUID resourceId,
                        String resourceName, String previousValues, String newValues) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", eventType);
        event.put("action", action);
        event.put("actorName", authentication == null ? "system" : authentication.getName());
        event.put("resourceType", resourceType);
        event.put("resourceId", resourceId);
        event.put("resourceName", resourceName);
        event.put("previousValues", previousValues);
        event.put("newValues", newValues);
        final Object credentials = authentication == null ? null : authentication.getCredentials();
        try {
            restClient.post()
                .uri("/api/v1/admin/audit-events")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> {
                    if (credentials instanceof String token && !token.isBlank()) {
                        headers.setBearerAuth(token);
                    }
                })
                .body(event)
                .retrieve()
                .toBodilessEntity();
        } catch (RuntimeException ex) {
            log.warn("Could not publish audit event type={} action={}: {}", eventType, action, ex.getMessage());
        }
    }
}
