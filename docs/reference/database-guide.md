# Database Guide

Last updated: 2026-08-24

## Database Architecture

AQVP uses PostgreSQL for dev/prod persistence and H2 for tests/local identity profile. Each service is intended to own its schema/database. Cross-service direct database access is not allowed.

Configured dev databases by service convention:

- `identity_db`
- `qualification_db`
- `verification_db`
- `admin_db`

## Implemented Identity Schema

Migration files:

- `aqvp-identity-service/src/main/resources/db/migration/V1__init_identity_schema.sql`
- `aqvp-identity-service/src/main/resources/db/migration/V2__seed_roles_permissions.sql`

Tables:

| Table | Purpose |
|---|---|
| `permissions` | Permission catalog with unique `name`. |
| `roles` | Role catalog with unique `name`. |
| `roles_permissions` | Many-to-many role/permission join table. |
| `users` | User accounts with username/email uniqueness and account state flags. |
| `users_roles` | Many-to-many user/role join table. |
| `refresh_tokens` | Refresh tokens tied to users. |
| `api_clients` | Machine clients with secret hashes. |
| `api_client_authorities` | API client authority values. |

Key constraints and indexes:

- UUID primary keys with PostgreSQL `gen_random_uuid()`.
- Unique `permissions.name`, `roles.name`, `users.username`, `users.email`, `refresh_tokens.token`, `api_clients.client_id`.
- Indexes on `refresh_tokens.token`, `users.username`, and `users.email`.
- Join tables cascade deletes from parent role/user/client records.

Seed data:

- Permissions: `user:read`, `user:write`, `user:delete`, `role:read`, `role:write`.
- Roles: `ADMIN`, `USER`.
- Admin user: `admin` / `admin@aqvp.local` seeded with BCrypt password for local/dev use.

## Migration Rules

- Place migrations in `src/main/resources/db/migration/`.
- Use `V<version>__<description>.sql`.
- Do not modify migrations that have already been merged.
- Add incremental migrations for changes.
- Keep seed data idempotent where possible.
- Define database-level constraints for uniqueness and relationships.

## Entity Standards

- Use UUID primary keys.
- Include audit fields: `created_at`, `updated_at`, `created_by`, `updated_by`, `version`.
- Use optimistic locking with `@Version`.
- Default JPA relationships to lazy loading unless a deliberate exception is documented.
- Avoid cascading deletes for business records unless explicitly required.

## Implemented Qualification Schema

Migration files:

- `aqvp-qualification-service/src/main/resources/db/migration/V1__init_institution_schema.sql`
- `aqvp-qualification-service/src/main/resources/db/migration/V2__init_qualification_schema.sql`
- `aqvp-qualification-service/src/main/resources/db/migration/V3__init_document_schema.sql`

Document table:

| Table | Purpose |
|---|---|
| `qualification_documents` | Metadata for generated certificates, transcripts, and QR code artifacts stored outside the database. |

`qualification_documents` stores `qualification_id`, `document_type`, `file_name`, `content_type`, `storage_key`, `sha256_hash`, `size_bytes`, `qr_payload`, `digital_signature`, `signature_algorithm`, `signer_key_id`, `generated_at`, and standard audit/version fields.

## Planned Schemas

Business-module migrations are not present yet. Expected future tables include:

- Qualification: `csv_import_batches`.
- Document: `certificate_templates` and external object-storage lifecycle metadata for production storage providers.
- Verification: `verification_requests`, `verification_results`, `verification_evidence`, `consent_records`.
- Audit/Notification: `audit_events`, `notifications`, `notification_templates`, `notification_attempts`.

## Backup and Integrity Considerations

- Production backups must include all service databases and migration history.
- Restore procedures must preserve audit history and qualification status history.
- Sensitive data should be encrypted at rest or protected by managed database controls.
- Verification and audit records require retention rules before production launch.
