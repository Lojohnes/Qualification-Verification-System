# Test Results

Last updated: 2026-08-10

## Latest Recorded Test Evidence

Source: `aqvp-identity-service/src/test/resources/testing/report/identity-testing-report.md`

| Date | Scope | Environment | Expected | Actual | Status |
|---|---|---|---|---|---|
| 2026-07-29 | Identity controller tests | Maven/JUnit/Spring MVC | Controllers return expected status and payloads | 15 passed | Passed |
| 2026-07-29 | Identity service tests | Maven/JUnit/Mockito | Services enforce auth, token, user, role, permission, API client rules | 33 passed | Passed |
| 2026-07-29 | Identity repository tests | Maven/JPA/H2 | Repositories persist and query identity data | 12 passed | Passed |
| 2026-07-29 | Identity security tests | Maven/Spring Security | JWT filters, API client filters, handlers, principal, and details service work | 12 passed | Passed |
| 2026-07-29 | RBAC/authorization integration | Maven/Spring Security | Permissions protect endpoint access | 4 passed | Passed |
| 2026-07-29 | Validation, DTO mapping, exception handling | Maven/JUnit | Validation and error mapping behave consistently | 23 passed | Passed |
| 2026-07-29 | Flyway migration integration | Testcontainers/PostgreSQL | Real PostgreSQL migrations succeed | 1 skipped because Docker unavailable | Skipped |

## Aggregate Reported Result

```text
Tests run: 101
Failures: 0
Errors: 0
Skipped: 1
Build: SUCCESS
```

## Tests Not Run During 2026-08-10 Documentation Refactor

- Full `mvn verify` was not run as part of this documentation-only refactor.
- Frontend `npm run build` or `npm run lint` was not run as part of this documentation-only refactor.
- No new application code was changed, so no functional regression tests were executed in this session.

## Outstanding Testing Requirements

- Run full root Maven verification after resolving Java target alignment.
- Run Testcontainers migration tests in Docker-capable CI/local environment.
- Add tests for qualification, verification, admin/audit, document, notification, and frontend modules as those features are implemented.
- Add end-to-end acceptance tests for the full issue/generate/verify lifecycle after business modules exist.

