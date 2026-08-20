# Current State

Last updated: 2026-08-20

## Implementation Status

AQVP is currently a multi-module Spring Boot and React project with completed Identity and Qualification service slices, plus a first implemented Verification Engine backend slice.

## Working or Implemented

- Root Maven parent project with modules for shared kernel, identity, qualification, verification, admin, and API gateway.
- Identity service with:
  - JWT login, refresh, logout, change password, forgot password placeholder, reset password placeholder, and GET /api/v1/auth/me (current user details).
  - User CRUD list/create/update/delete surface.
- Identity & Access Module:
  - Token-based authentication and authorization.
  - User, Role, and Permission CRUD operations.
  - API Client management (infrastructure).
  - Secure `/api/v1/auth/me` endpoint.
- Institution Module (under `aqvp-qualification-service`):
  - Database schema migrations and entities for Institutions, Faculties, Departments, and Programs.
  - Stateless JWT token validation filter and Security Configuration mapping permissions.
  - REST endpoints for CRUD operations under `/api/v1/institutions` and `/api/v1/programs`.
  - Comprehensive unit and integration tests.
- Qualification Module (under `aqvp-qualification-service`):
  - Student records and qualification lifecycle APIs are implemented.
  - Qualifications can be created, issued, amended, and revoked.
  - Issued qualifications receive a `securityIdentifier` used by QR verification.
  - Internal verification snapshot endpoints expose minimal authoritative records to `aqvp-verification-service`.
- Verification Engine backend slice (under `aqvp-verification-service`):
  - Flyway schema, entities, repositories, DTOs, JWT security, exception handling, QR parsing, consent validation, evidence matching, request tracking, result persistence, and QR verification APIs are implemented.
  - QR verification resolves `AQVP:v1:<issuerCode>:<securityIdentifier>` against the Qualification service internal snapshot API rather than reading the Qualification database directly.
  - Focused unit tests were added for QR parsing, consent validation, matching, engine flow, and the Qualification snapshot service.
- React/Vite/MUI application.
  - Public login, register, forgot/reset password pages.
  - Protected layout, dashboard, identity pages, placeholders for future modules.
  - Axios, token storage, route guards, theme/snackbar contexts, and Redux store.
  - Institution module frontend (`frontend/aqvp-web/src/features/institution`): Institutions and Programs list/create/edit/deactivate screens wired to the live Qualification service REST API (`http://localhost:8082` by default, via `VITE_QUALIFICATION_API_BASE_URL`), using a second authenticated axios client (`qualificationApi`) that shares the existing token/refresh interceptor logic.
- Docker Compose infrastructure for PostgreSQL, Keycloak, Zookeeper, and Kafka.
- GitHub Actions CI configured for Maven verify with static analysis.

## Partially Implemented

- API gateway routing exists, but the frontend default API base URL points directly at identity service port `8081`, not gateway port `8080`.
- API client authentication infrastructure exists, but operational CRUD management is missing.
- Password reset endpoints exist as placeholders only.
- Program create/edit UI requires a Department UUID entered manually because no Faculty/Department REST endpoints exist yet to populate a selector; the schema and repositories exist but there is no controller.
- The Institution/Program screens require the Identity-issued JWT to include `institution:read`, `institution:write`, `program:read`, and `program:write` authorities. These permissions are not present in the current Identity seed migration (`V2__seed_roles_permissions.sql`), so the default admin role must be granted them (or the migration updated) before the new screens will return data instead of `403`.
- Verification frontend screens are still placeholders.
- Verification support for certificate upload/OCR, holder-token consent, QR digital signatures, rate limiting, and full audit integration remains pending.
- Admin service remains a structural scaffold without domain models, migrations, controllers, or tests.

## Not Yet Implemented

- CSV import for qualification records.
- Certificate upload extraction and OCR verification.
- Document storage, PDF certificate generation, QR generation, and digital signatures.
- Audit event model, searchable audit trail, notification service, email/SMS providers.
- Production deployment manifests, rollback automation, and production secrets management.

## Current Architecture State

The implementation is a modular service-oriented Spring Boot repository rather than a single deployed monolith. Each service has its own Maven module and runtime port. Identity remains the reference implementation for security and module patterns. Qualification is the authoritative source for issued academic records. Verification now uses REST to call Qualification for verification snapshots, preserving the no shared database access rule.

## Current Development Phase

The project is in business module implementation. The immediate priority is to compile and run the new Verification slice in a Maven-capable environment, then add controller/security/integration coverage and frontend verification screens.

