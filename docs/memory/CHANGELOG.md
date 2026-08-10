# Changelog

Last updated: 2026-08-10

All notable project changes should be recorded here in chronological order.

## 2026-08-10

### Documentation

- Created the persistent documentation structure under `docs/memory` and `docs/reference`.
- Added all required Group 1 memory documents and Group 2 technical reference documents.
- Consolidated source-backed project scope from existing docs, Maven modules, service configuration, frontend routes, Identity migrations, and Identity testing report.
- Moved earlier root-level planning and standards docs into `docs/reference/legacy` so `docs` is physically separated into `memory` and `reference`.
- Documented implementation/docs discrepancies:
  - Root project targets Java 21 while older onboarding text references Java 17 and identity module overrides compiler release to 17.
  - Frontend declares `/api/v1/auth/me`, but the current Identity controller does not expose it.
  - API gateway exists on port 8080, while frontend default API base URL points directly to Identity on port 8081.

## 2026-07-29

### Sprint 1 / Identity

- Identity and Access module reported complete, tested, tagged as `v0.1.0-identity`, and merged to `develop`.
- Added JWT authentication, refresh token support, role/permission model, user management, API client authentication infrastructure, Flyway migrations, Swagger/OpenAPI setup, Postman examples, sample JWT docs, and automated tests.
- Identity testing report records 101 tests: 100 passed, 1 Testcontainers/Flyway integration test skipped because Docker was unavailable.

### Planning

- Added Sprint 2 plan for Institution, Qualification, Verification, Document, Audit, Notification, frontend foundation, CI/CD expansion, and integration testing.
- Added coding standards, developer onboarding, module development guide, and team guidelines.
