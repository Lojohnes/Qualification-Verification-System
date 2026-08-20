-- S2-005 / S2-003: Student Records and Qualification Module schema
-- Takunda Mazambani — Sprint 2

CREATE TABLE IF NOT EXISTS students (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_number VARCHAR(100) NOT NULL UNIQUE,
    first_name     VARCHAR(150) NOT NULL,
    last_name      VARCHAR(150) NOT NULL,
    email          VARCHAR(255) UNIQUE,
    date_of_birth  DATE,
    national_id    VARCHAR(100),
    institution_id UUID         NOT NULL REFERENCES institutions(id),
    active         BOOLEAN      NOT NULL DEFAULT true,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    version        BIGINT
);

CREATE TABLE IF NOT EXISTS qualifications (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    qualification_number VARCHAR(100) NOT NULL UNIQUE,
    student_id           UUID         NOT NULL REFERENCES students(id),
    institution_id       UUID         NOT NULL REFERENCES institutions(id),
    program_id           UUID         REFERENCES programs(id),
    qualification_type   VARCHAR(100) NOT NULL,
    qualification_name   VARCHAR(255) NOT NULL,
    classification       VARCHAR(100),
    year_of_award        INTEGER      NOT NULL,
    status               VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
    security_identifier  VARCHAR(255) UNIQUE,
    issued_at            TIMESTAMP,
    revoked_at           TIMESTAMP,
    revocation_reason    TEXT,
    notes                TEXT,
    created_at           TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    version              BIGINT
);

CREATE TABLE IF NOT EXISTS qualification_status_history (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    qualification_id UUID         NOT NULL REFERENCES qualifications(id),
    previous_status  VARCHAR(50),
    new_status       VARCHAR(50)  NOT NULL,
    changed_by       VARCHAR(255),
    reason           TEXT,
    changed_at       TIMESTAMP    NOT NULL DEFAULT now()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_students_student_number   ON students(student_number);
CREATE INDEX IF NOT EXISTS idx_students_institution_id   ON students(institution_id);
CREATE INDEX IF NOT EXISTS idx_students_email            ON students(email);

CREATE INDEX IF NOT EXISTS idx_qualifications_student_id      ON qualifications(student_id);
CREATE INDEX IF NOT EXISTS idx_qualifications_institution_id  ON qualifications(institution_id);
CREATE INDEX IF NOT EXISTS idx_qualifications_status          ON qualifications(status);
CREATE INDEX IF NOT EXISTS idx_qualifications_security_id     ON qualifications(security_identifier);
CREATE INDEX IF NOT EXISTS idx_qualifications_number          ON qualifications(qualification_number);

CREATE INDEX IF NOT EXISTS idx_qual_status_history_qual_id   ON qualification_status_history(qualification_id);
CREATE INDEX IF NOT EXISTS idx_qual_status_history_changed_at ON qualification_status_history(changed_at);
