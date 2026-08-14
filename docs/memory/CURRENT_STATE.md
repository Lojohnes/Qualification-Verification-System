# Current State

Last updated: 2026-08-10

## Implementation Status

AQVP is currently a multi-module Spring Boot and React project with a completed Identity service and scaffolded business modules.

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
- React/Vite/MUI application.
  - Public login, register, forgot/reset password pages.
  - Protected layout, dashboard, identity pages, placeholders for future modules.
  - Axios, token storage, route guards, theme/snackbar contexts, and Redux store.
- Docker Compose infrastructure for PostgreSQL, Keycloak, Zookeeper, and Kafka.
- GitHub Actions CI configured for Maven verify with static analysis.

## Partially Implemented

- API gateway routing exists, but the frontend default API base URL points directly at identity service port `8081`, not gateway port `8080`.
- API client authentication infrastructure exists, but operational CRUD management is missing.
- Password reset endpoints exist as placeholders only.
- Qualification Service: core Institution module is implemented, but the Qualification/Student records and issuance workflow are missing.
- Verification and Admin services are structural scaffolds without domain models, migrations, controllers, or tests.

## Not Yet Implemented

- Qualification records, student records, issuance, amendment, revocation, and CSV import.
- Verification requests, consent validation, QR verification engine, certificate upload extraction, verification result persistence.
- Document storage, PDF certificate generation, QR generation, and digital signatures.
- Audit event model, searchable audit trail, notification service, email/SMS providers.
- Production deployment manifests, rollback automation, and production secrets management.

## Current Architecture State

The implementation is a modular service-oriented Spring Boot repository rather than a single deployed monolith. Each service has its own Maven module and runtime port. The Identity service is the only fully implemented backend bounded context. Planned communication patterns are REST for synchronous calls and Kafka for asynchronous events. Shared database access across module boundaries is forbidden by project standards.

## Current Development Phase

The project is between Identity completion and business module implementation. The immediate priority is to build the next business modules using the Identity service as the reference pattern and to align documentation, setup instructions, and API contracts with current source code.

