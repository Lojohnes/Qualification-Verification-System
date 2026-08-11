# Testing Guide

Last updated: 2026-08-10

## Testing Strategy

AQVP uses layered automated testing:

- Unit tests for services, mappers, validators, utilities, and security helpers.
- Controller tests with Spring MVC/MockMvc.
- Repository tests with JPA/H2.
- Integration tests with Spring Boot and, where needed, Testcontainers/PostgreSQL.
- Static analysis with Checkstyle and SpotBugs.
- Coverage collection with JaCoCo.
- Future UI/E2E tests for frontend and full qualification lifecycle.

## Commands

Run all tests/build checks:

```powershell
mvn -B clean verify
```

Run tests for one module:

```powershell
mvn test -pl aqvp-identity-service -am
```

Run static analysis:

```powershell
mvn verify -pl aqvp-identity-service -am -Pstatic-analysis
```

Run a specific test class:

```powershell
mvn test -pl aqvp-identity-service -Dtest=AuthControllerTest
```

Frontend checks:

```powershell
cd frontend/aqvp-web
npm run build
npm run lint
```

## Current Recorded Results

The Identity test report from 2026-07-29 records:

- 101 total tests.
- 100 passed.
- 1 skipped because Docker was unavailable.
- 0 failures.
- 0 errors.
- Build success.

## Acceptance Criteria for New Modules

Each new backend module should include:

- Service tests.
- Mapper tests.
- Validator tests where custom validation exists.
- Repository tests.
- Controller tests.
- Security/authorization tests.
- Integration tests for important workflows.
- Migration tests where schema risk is material.

Each API change should include:

- OpenAPI annotations.
- Request/response examples.
- Tests for success, validation failure, not found, conflict, unauthorized, and forbidden outcomes where applicable.

## Regression Requirements

Before merge:

- No Identity functionality is broken.
- No duplicated Identity auth logic is introduced.
- All relevant module tests pass.
- Static analysis passes.
- Documentation is updated for API, database, security, and operational changes.

