-- S2-006: Qualification document management, storage metadata, QR payloads, and signatures

CREATE TABLE IF NOT EXISTS qualification_documents (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    qualification_id    UUID         NOT NULL REFERENCES qualifications(id),
    document_type       VARCHAR(50)  NOT NULL,
    file_name           VARCHAR(255) NOT NULL,
    content_type        VARCHAR(100) NOT NULL,
    storage_key         VARCHAR(500) NOT NULL,
    sha256_hash         VARCHAR(64)  NOT NULL,
    size_bytes          BIGINT       NOT NULL,
    qr_payload          VARCHAR(500) NOT NULL,
    digital_signature   VARCHAR(255) NOT NULL,
    signature_algorithm VARCHAR(100) NOT NULL,
    signer_key_id       VARCHAR(100) NOT NULL,
    generated_at        TIMESTAMP    NOT NULL DEFAULT now(),
    created_at          TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    version             BIGINT
);

CREATE INDEX IF NOT EXISTS idx_qualification_documents_qual_id
    ON qualification_documents(qualification_id);
CREATE INDEX IF NOT EXISTS idx_qualification_documents_type
    ON qualification_documents(document_type);
CREATE INDEX IF NOT EXISTS idx_qualification_documents_generated_at
    ON qualification_documents(generated_at);
