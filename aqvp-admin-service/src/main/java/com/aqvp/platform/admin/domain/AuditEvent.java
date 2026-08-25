package com.aqvp.platform.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditEvent extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String eventType;

    @Column(nullable = false, length = 50)
    private String action;

    @Column
    private UUID actorId;

    @Column(length = 255)
    private String actorName;

    @Column(length = 100)
    private String actorRole;

    @Column(length = 100)
    private String organizationId;

    @Column(length = 100)
    private String resourceType;

    @Column
    private UUID resourceId;

    @Column(length = 255)
    private String resourceName;

    @Column(columnDefinition = "TEXT")
    private String previousValues;

    @Column(columnDefinition = "TEXT")
    private String newValues;

    @Column(length = 255)
    private String ipAddress;

    @Column(length = 255)
    private String deviceInfo;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Override
    protected void onCreate() {
        super.onCreate();
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
