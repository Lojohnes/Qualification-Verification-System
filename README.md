# Academic Qualification Verification Platform (AQVP)

Enterprise foundation for the Academic Qualification Verification Platform.

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven 3.9+
- PostgreSQL 16
- Keycloak 24
- Apache Kafka 3.6

## Module Structure

| Module | Purpose |
|--------|---------|
| `aqvp-shared-kernel` | Cross-cutting DDD primitives, events, exceptions, utilities |
| `aqvp-identity-service` | User authentication and authorization bounded context |
| `aqvp-qualification-service` | Qualification records and documents bounded context |
| `aqvp-verification-service` | Verification workflows bounded context |
| `aqvp-admin-service` | Administrative and audit bounded context |
| `aqvp-api-gateway` | Spring Cloud Gateway for request routing |

## Quick Start

1. Start infrastructure:
   ```bash
   docker compose up -d
   ```
2. Create application databases in PostgreSQL (one per service).
3. Build the project:
   ```bash
   mvn -B clean verify
   ```
4. Run a service with a specific profile:
   ```bash
   cd aqvp-identity-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Profiles

- `dev` (default) — local development, PostgreSQL on `localhost`
- `test` — in-memory H2 for integration tests
- `prod` — external credentials via environment variables

## Quality Gates

- **Checkstyle**: `mvn -P static-analysis checkstyle:check`
- **SpotBugs**: `mvn -P static-analysis spotbugs:check`
- **JaCoCo**: coverage reports generated under `target/site/jacoco`

## GitHub Actions

The `.github/workflows/ci.yml` pipeline builds, runs static analysis and collects coverage on every push or pull request to `main` and `develop`.

## Documentation

Project documentation is now split into two persistent groups:

- `docs/memory/` preserves project context, current state, decisions, test results, open work, and session handoff notes.
- `docs/reference/` contains the authoritative technical reference library for architecture, domain, business rules, APIs, database, development, testing, security, deployment, quick reference, glossary, and training.

Historical source documents from the earlier docs layout are preserved under `docs/reference/legacy/`.
