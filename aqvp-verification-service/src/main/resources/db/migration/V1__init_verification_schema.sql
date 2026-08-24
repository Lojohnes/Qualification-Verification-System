-- Verification module schema
CREATE TABLE IF NOT EXISTS verifications (
    id                    UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    qualification_number  VARCHAR(100) NOT NULL,
    security_identifier   VARCHAR(255),
    qualification_id      UUID,
    status                VARCHAR(20)  NOT NULL,
    method                VARCHAR(20)  NOT NULL,
    verified_by           VARCHAR(100),
    verified_at           TIMESTAMP    NOT NULL DEFAULT now(),
    ip_address            VARCHAR(100),
    notes                 TEXT,
    created_at            TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    version               BIGINT
);

CREATE INDEX IF NOT EXISTS idx_verifications_qual_number ON verifications(qualification_number);
CREATE INDEX IF NOT EXISTS idx_verifications_security_id ON verifications(security_identifier);
CREATE INDEX IF NOT EXISTS idx_verifications_verified_at ON verifications(verified_at);
