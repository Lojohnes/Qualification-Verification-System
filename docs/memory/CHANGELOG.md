# Changelog

Last updated: 2026-08-10

All notable project changes should be recorded here in chronological order.

## 2026-08-13

### Build / Compiler Target

- Aligned compiler release versions to Java 21 across all modules by removing the `maven.compiler.release` overrides from `aqvp-shared-kernel/pom.xml` and `aqvp-identity-service/pom.xml`.

### Identity Service

- Implemented core Institution Module backend in `aqvp-qualification-service` (stories S2-001 & S2-002) including:
  - Database schema migrations for `institutions`, `faculties`, `departments`, and `programs`.
  - Core domain entities with proper relationships, cascading, and audit trails.
  - DTO validation records and MapStruct mappers.
  - Stateless JWT filter and `SecurityConfig` to authorize request permissions.
  - REST controllers for `/api/v1/institutions` and `/api/v1/programs` CRUD actions.
  - Integration and unit tests for controllers and services.
- Added `GET /api/v1/auth/me` endpoint in `aqvp-identity-service` to retrieve the current authenticated user's details and dynamic permissions.
- Aligned target compiler release setting to JDK 21 across the multi-module workspace.
- Added comprehensive integration tests in `AuthControllerTest.java` verifying the `/me` endpoint behaviour and payload.
- Updated mock test instantiations of `UserResponseDto` in `UserServiceTest` and `UserControllerTest` to include permissions.
- Added `shouldReturnCurrentUserDetails` integration test case in `AuthControllerTest`.

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
