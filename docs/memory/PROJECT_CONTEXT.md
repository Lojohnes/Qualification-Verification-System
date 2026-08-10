# Project Context

Last updated: 2026-08-10

## Project Background

The Academic Qualification Verification Platform (AQVP) is a secure, multi-tenant platform for issuing, storing, and verifying academic qualifications. It exists to reduce qualification fraud by giving employers and other verifying organizations an authoritative way to confirm that a certificate, QR code, or uploaded document matches a trusted qualification record.

The system supports two major business capabilities:

- Verification Platform: lets authorized read-only and read/write organizations verify qualifications through QR scanning, certificate upload, and authoritative record lookup.
- Certificate Generation Module: lets authorized educational institutions create qualification records, generate secure identifiers and QR codes, and produce verifiable certificates.

## Business and Domain Context

AQVP serves both qualification issuers and qualification verifiers.

- Read-Only organizations include employers, banks, NGOs, government departments, and recruiters. They can verify qualifications but cannot create or modify authoritative qualification records.
- Read & Write organizations include universities, colleges, examination boards, and professional awarding bodies. They can issue and maintain qualification records within their authorized scope.
- Platform-level users include Super Admin and System Admin.
- Client organization users are limited to IT Admin, HR Manager, and Director, with a maximum of three active client users per organization.

The core lifecycle is:

```text
Issue -> Secure -> Register -> Present -> Scan/Upload -> Verify
```

The platform database is the source of truth. QR codes identify and authenticate records; they must not become the certificate database or expose unnecessary personal data.

## Current Technology Context

- Backend: Java, Spring Boot 3.3.5, Maven multi-module project.
- Root build standard: Java 21, Maven compiler release 21.
- Current mismatch: `aqvp-identity-service/pom.xml` overrides `maven.compiler.release` to 17 while root docs/build use Java 21.
- Persistence: PostgreSQL 16 in dev/prod, H2 for tests and identity local profile.
- Migrations: Flyway.
- Security: Spring Security, JWT, BCrypt, permission-based RBAC.
- API docs: Springdoc OpenAPI / Swagger.
- Frontend: React 19, TypeScript 5.7, Vite 6, MUI 6, Redux Toolkit, Axios.
- Infrastructure: Docker Compose provides PostgreSQL, Keycloak, Zookeeper, and Kafka.
- CI: GitHub Actions runs Maven verify with static analysis on `main` and `develop`.

## Repository Shape

Root modules:

- `aqvp-shared-kernel`: placeholder/shared marker module for cross-cutting primitives.
- `aqvp-identity-service`: implemented Identity and Access bounded context.
- `aqvp-qualification-service`: scaffolded qualification bounded context.
- `aqvp-verification-service`: scaffolded verification bounded context.
- `aqvp-admin-service`: scaffolded administration/audit bounded context.
- `aqvp-api-gateway`: Spring Cloud Gateway routing scaffold.
- `frontend/aqvp-web`: React frontend foundation.

Documentation is maintained as a persistent knowledge system:

- `docs/memory`: project memory, continuity, work state, decisions, tests, and handoff.
- `docs/reference`: authoritative project reference library.

## Important Constraints for AI Agents

- Do not begin work as if the project has no history. Read the required memory docs first.
- The Identity module is the reference implementation and should not be rewritten or duplicated.
- New modules must integrate with Identity for users, roles, permissions, and JWT validation.
- Keep documentation synchronized with implementation after meaningful changes.
- Do not claim planned modules are implemented unless source code, migrations, APIs, and tests exist.
- Do not silently resolve conflicts between docs and code. Investigate, correct the right artifact, and log the change.
- Never commit secrets, real credentials, tokens, private keys, or `.env` files.
- Preserve module boundaries. No shared database access across bounded contexts.

