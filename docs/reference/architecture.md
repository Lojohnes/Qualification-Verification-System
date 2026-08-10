# Architecture Reference

Last updated: 2026-08-10

## Overview

AQVP is organized as a Maven multi-module, service-oriented Spring Boot backend with a React/Vite frontend. The architecture uses bounded contexts for identity, qualification, verification, administration/audit, gateway routing, and shared kernel concerns.

## Backend Components

| Component | Path | Responsibility | Current State |
|---|---|---|---|
| Shared Kernel | `aqvp-shared-kernel` | Cross-cutting primitives, future shared exceptions/events/utilities. | Minimal marker module. |
| Identity Service | `aqvp-identity-service` | Authentication, JWTs, refresh tokens, users, roles, permissions, API client auth. | Implemented and tested. |
| Qualification Service | `aqvp-qualification-service` | Qualification records and document-related bounded context. | Scaffolded. |
| Verification Service | `aqvp-verification-service` | Verification workflows, QR verification, request/result tracking. | Scaffolded. |
| Admin Service | `aqvp-admin-service` | Administrative and audit bounded context. | Scaffolded. |
| API Gateway | `aqvp-api-gateway` | Spring Cloud Gateway routes to backend services. | Routing scaffold exists. |

## Frontend Components

The frontend is in `frontend/aqvp-web` and uses React 19, TypeScript, Vite, MUI, Redux Toolkit, React Router, Axios, React Hook Form, and Yup.

Implemented frontend areas:

- Public auth pages: login, register, forgot password, reset password.
- Protected shell: main layout, dashboard, identity pages.
- Placeholder routes: institution, qualification, verification, documents, audit, reports, settings.
- Shared utilities: storage, JWT helpers, formatters, route constants, API constants.

## Runtime Ports

| Runtime | Default Port |
|---|---|
| API Gateway | 8080 |
| Identity Service | 8081 |
| Qualification Service | 8082 |
| Verification Service | 8083 |
| Admin Service | 8084 |
| Frontend Vite dev server | 3000 |
| PostgreSQL | 5432 |
| Keycloak | 8080 in Docker Compose, which conflicts with API Gateway if both run locally on the host |
| Kafka | 9092 |
| Zookeeper | 2181 |

## Gateway Routes

The gateway strips the first path segment:

| External Path | Target |
|---|---|
| `/identity/**` | `http://localhost:8081/**` |
| `/qualifications/**` | `http://localhost:8082/**` |
| `/verification/**` | `http://localhost:8083/**` |
| `/admin/**` | `http://localhost:8084/**` |

## Architectural Patterns

- Layered Spring modules: config, controller, domain, dto, exception, mapper, repository, security, service, validator.
- REST APIs for synchronous operations.
- Kafka intended for asynchronous workflows.
- Flyway migrations for database schema.
- DTOs at API boundaries; entities are not exposed from controllers.
- MapStruct for entity/DTO mapping.
- JWT and permission-based RBAC from Identity.

## Data Flow

### Authentication

```text
Frontend -> Identity /api/v1/auth/login -> JWT + refresh token
Frontend -> protected backend APIs with Authorization: Bearer <jwt>
Backend -> Spring Security filters -> permissions -> service methods
```

### Planned Qualification Issuance

```text
Institution user -> Qualification API -> validate scope and data
-> create qualification record -> generate security identifier
-> document generation -> QR code/certificate -> audit event
```

### Planned Verification

```text
Verifier -> scan QR or upload certificate
-> Verification API -> resolve reference -> compare with authoritative record
-> return VERIFIED / NOT VERIFIED / DETAILS MISMATCH / REVOKED / REQUIRES REVIEW
-> audit event
```

## External Integrations

- PostgreSQL for service persistence.
- Kafka/Zookeeper for planned event workflows.
- Keycloak is present in Docker Compose but current implemented Identity service performs application-level JWT authentication.
- Future email/SMS providers are planned for notification workflows.

