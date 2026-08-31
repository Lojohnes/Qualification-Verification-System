CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    event_type VARCHAR(150) NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_id UUID,
    actor_name VARCHAR(255),
    actor_role VARCHAR(100),
    organization_id VARCHAR(100),
    resource_type VARCHAR(100),
    resource_id UUID,
    resource_name VARCHAR(255),
    previous_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(255),
    device_info VARCHAR(255),
    occurred_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    template_code VARCHAR(100) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider_reference VARCHAR(255),
    provider_response TEXT,
    error_message TEXT,
    attempts INTEGER,
    sent_at TIMESTAMP,
    next_retry_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_actor_name ON audit_events(actor_name);
CREATE INDEX IF NOT EXISTS idx_audit_resource_type ON audit_events(resource_type);
CREATE INDEX IF NOT EXISTS idx_audit_occurred_at ON audit_events(occurred_at);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
