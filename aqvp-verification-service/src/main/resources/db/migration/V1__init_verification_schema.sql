CREATE TABLE IF NOT EXISTS verification_requests (
    id                         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    request_reference          VARCHAR(40)  NOT NULL UNIQUE,
    requester_username         VARCHAR(150),
    requester_organization_id  UUID,
    requester_organization_name VARCHAR(255),
    channel                    VARCHAR(50)  NOT NULL,
    purpose                    VARCHAR(80)  NOT NULL,
    status                     VARCHAR(50)  NOT NULL,
    consent_status             VARCHAR(50)  NOT NULL,
    qualification_id           UUID,
    security_identifier_hash   VARCHAR(64),
    expires_at                 TIMESTAMP    NOT NULL,
    completed_at               TIMESTAMP,
    created_at                 TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMP,
    created_by                 VARCHAR(100),
    updated_by                 VARCHAR(100),
    version                    BIGINT
);

CREATE TABLE IF NOT EXISTS consent_records (
    id                      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    verification_request_id UUID         NOT NULL,
    consent_type            VARCHAR(80)  NOT NULL,
    scope                   VARCHAR(80)  NOT NULL,
    holder_first_name       VARCHAR(150),
    holder_last_name        VARCHAR(150),
    holder_date_of_birth    DATE,
    holder_email            VARCHAR(255),
    consent_reference       VARCHAR(255),
    granted_at              TIMESTAMP,
    expires_at              TIMESTAMP,
    validated_at            TIMESTAMP,
    status                  VARCHAR(50)  NOT NULL,
    failure_reason          TEXT,
    created_at              TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    version                 BIGINT
);

CREATE TABLE IF NOT EXISTS verification_evidence (
    id                      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    verification_request_id UUID         NOT NULL,
    evidence_type           VARCHAR(80)  NOT NULL,
    qualification_number    VARCHAR(100),
    student_number          VARCHAR(100),
    holder_first_name       VARCHAR(150),
    holder_last_name        VARCHAR(150),
    year_of_award           INTEGER,
    qualification_name      VARCHAR(255),
    institution_id          UUID,
    institution_name        VARCHAR(255),
    raw_payload_hash        VARCHAR(64),
    created_at              TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    version                 BIGINT
);

CREATE TABLE IF NOT EXISTS verification_results (
    id                               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    verification_request_id          UUID         NOT NULL,
    outcome                          VARCHAR(80)  NOT NULL,
    confidence                       VARCHAR(50)  NOT NULL,
    matched_qualification_id         UUID,
    matched_security_identifier_hash VARCHAR(64),
    qualification_status             VARCHAR(50),
    match_score                      INTEGER      NOT NULL,
    match_details_json               TEXT,
    response_disclosure_scope        VARCHAR(80)  NOT NULL,
    failure_code                     VARCHAR(100),
    failure_message                  TEXT,
    verified_at                      TIMESTAMP    NOT NULL,
    created_at                       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at                       TIMESTAMP,
    created_by                       VARCHAR(100),
    updated_by                       VARCHAR(100),
    version                          BIGINT
);

CREATE INDEX IF NOT EXISTS idx_verification_requests_reference ON verification_requests(request_reference);
CREATE INDEX IF NOT EXISTS idx_verification_requests_requester ON verification_requests(requester_username);
CREATE INDEX IF NOT EXISTS idx_verification_requests_status ON verification_requests(status);
CREATE INDEX IF NOT EXISTS idx_verification_requests_security_hash
    ON verification_requests(security_identifier_hash);
CREATE INDEX IF NOT EXISTS idx_consent_records_request ON consent_records(verification_request_id);
CREATE INDEX IF NOT EXISTS idx_verification_evidence_request ON verification_evidence(verification_request_id);
CREATE INDEX IF NOT EXISTS idx_verification_results_request ON verification_results(verification_request_id);
CREATE INDEX IF NOT EXISTS idx_verification_results_outcome ON verification_results(outcome);
