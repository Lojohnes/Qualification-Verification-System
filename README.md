# Academic Qualification Verification Platform (AQVP)

Enterprise foundation for the Academic Qualification Verification Platform.

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven 3.9+
- PostgreSQL 16
- Redis 7
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

1. Copy the environment template and set a local JWT secret:
   ```powershell
   copy .env.example .env
   ```
   Edit `.env` if you need to change host ports (the default PostgreSQL host port is `5433` to avoid conflicts with a local PostgreSQL installation).
2. Start infrastructure:
   ```powershell
   docker compose -f docker/docker-compose.dev.yml --env-file .env up -d
   ```
3. Verify all containers are healthy:
   ```powershell
   docker compose -f docker/docker-compose.dev.yml --env-file .env ps
   ```
4. Build the project:
   ```powershell
   mvn -B clean verify -DskipTests
   ```
5. Run a service with the dev profile (after loading `.env` variables in your shell):
   ```powershell
   cd aqvp-identity-service
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```

## Profiles

- `dev` (default) — local development, PostgreSQL on `localhost`
- `test` — in-memory H2 for integration tests
- `prod` — external credentials via environment variables

## Quality Gates

- **Checkstyle**: `mvn -P static-analysis checkstyle:check`
- **SpotBugs**: `mvn -P static-analysis spotbugs:check`
- **JaCoCo**: coverage reports generated under `target/site/jacoco`

## GitHub Actions. 

The `.github/workflows/ci.yml` pipeline builds, runs static analysis and collects coverage on every push or pull request to `main` and `develop`.

## Documentation

Project documentation is now split into two persistent groups:

- `docs/memory/` preserves project context, current state, decisions, test results, open work, and session handoff notes.
- `docs/reference/` contains the authoritative technical reference library for architecture, domain, business rules, APIs, database, development, testing, security, deployment, quick reference, glossary, and training.

Historical source documents from the earlier docs layout are preserved under `docs/reference/legacy/`.
