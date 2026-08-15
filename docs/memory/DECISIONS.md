# Decisions

Last updated: 2026-08-14

## Decision Log

| Date / Phase | Decision | Alternatives Considered | Reason / Notes | Status |
|---|---|---|---|---|
| 2026-07-29 / Sprint 1 | Identity and Access is the reference implementation for future modules. | Rebuild shared patterns in each module. | Avoid duplicated authentication, authorization, DTO, mapper, controller, security, and testing patterns. | Active |
| 2026-07-29 / Sprint 1 | Use Spring Boot multi-module Maven architecture with separate bounded-context services. | Single application module; unrelated repositories per service. | Keeps bounded contexts separated while sharing parent build and shared kernel. | Active |
| 2026-07-29 / Sprint 1 | Use Identity-managed JWT, roles, and permissions for authorization. | Keycloak-only authorization; per-service auth implementations. | Gives all modules a consistent access-control model and avoids reimplementing user/role logic. | Active |
| 2026-07-29 / Sprint 1 | Use Java records for DTOs and MapStruct for mapping. | Mutable DTO classes; manual mapping. | Reduces boilerplate and makes API payload contracts explicit. | Active |
| 2026-07-29 / Sprint 1 | Use Flyway for database schema migrations. | Hibernate DDL auto for all environments; manual SQL outside version control. | Gives repeatable schema management and stable history. | Active |
| 2026-07-29 / Sprint 1 | Use UUID primary keys and shared audit fields on entities. | Numeric sequences; no common audit fields. | Supports distributed module boundaries and consistent auditability. | Active |
| 2026-07-29 / Sprint 1 | Use REST for synchronous module communication and Kafka for async workflows. | Direct database access; synchronous-only design. | Maintains module boundaries and supports event-driven workflows such as document generation and notifications. | Active |
| 2026-07-29 / Sprint 1 | QR codes must identify and authenticate qualification records, not contain the full authoritative record or unnecessary biodata. | Plaintext QR payload containing all certificate data. | Protects personal information and keeps the server-side qualification database authoritative. | Active |
| 2026-07-29 / Sprint 1 | Read-Only organizations cannot create or modify authoritative qualification records. | Single organization permission model. | Preserves trust boundary between verifiers and issuing institutions. | Active |
| 2026-08-10 / Docs refactor | Treat `docs/memory` and `docs/reference` as the persistent project knowledge system. | Keep ad hoc root-level docs only. | Supports continuity across AI/developer sessions and keeps project state auditable. | Active |
| 2026-08-13 | Align all compiler release targets to Java 21 across all Maven modules (removing Java 17 overrides from submodules). | Lower parent POM and CI pipeline to Java 17; keep mismatch with mixed bytecode targets. | Aligns with developer onboarding guide, CI pipeline targets, and leverages modern Java 21 features consistently across the microservices. | Active |
| 2026-08-14 | Business modules use stateless JWT validation (extracting claims and authorities directly from incoming tokens). | Query the Identity service or its database for user info on each request. | Eliminates cross-service communication overhead and keeps services decoupled and high-performance. | Active |

## Open Decision Items

- Decide whether the frontend should call services directly or use the API gateway as its default base URL.
- Decide whether API client management requires public administrative REST endpoints.
- Choose production strategy for password reset delivery, email/SMS providers, rate limiting, and refresh-token hashing.

