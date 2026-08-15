# Test Results

Last updated: 2026-08-14

## Latest Recorded Test Evidence

Source: `aqvp-identity-service/src/test/resources/testing/report/identity-testing-report.md` and Maven test logs.

| Date | Scope | Environment | Expected | Actual | Status |
|---|---|---|---|---|---|
| 2026-07-29 | Identity controller tests | Maven/JUnit/Spring MVC | Controllers return expected status and payloads | 15 passed | Passed |
| 2026-07-29 | Identity service tests | Maven/JUnit/Mockito | Services enforce auth, token, user, role, permission, API client rules | 33 passed | Passed |
| 2026-07-29 | Identity repository tests | Maven/JPA/H2 | Repositories persist and query identity data | 12 passed | Passed |
| 2026-07-29 | Identity security tests | Maven/Spring Security | JWT filters, API client filters, handlers, principal, and details service work | 12 passed | Passed |
| 2026-07-29 | RBAC/authorization integration | Maven/Spring Security | Permissions protect endpoint access | 4 passed | Passed |
| 2026-07-29 | Validation, DTO mapping, exception handling | Maven/JUnit | Validation and error mapping behave consistently | 23 passed | Passed |
| 2026-07-29 | Flyway migration integration | Testcontainers/PostgreSQL | Real PostgreSQL migrations succeed | 1 skipped because Docker unavailable | Skipped |
| 2026-08-13 | Full workspace clean test | Maven/JDK 21 | Clean compilation and full verification of all modules | 102 tests passed, 1 skipped | Passed |
| 2026-08-14 | Institution module tests | Maven/JUnit/MockMvc | Verify CRUD endpoints, constraints validation, and JWT security roles | 13 passed | Passed |

## Aggregate Reported Result

```text
Tests run: 116
Failures: 0
Errors: 0
Skipped: 1
Build: SUCCESS
```

## Outstanding Testing Requirements

- Run Testcontainers migration tests in Docker-capable CI/local environment.
- Add tests for qualification, verification, admin/audit, document, notification, and frontend modules as those features are implemented.
- Add end-to-end acceptance tests for the full issue/generate/verify lifecycle after business modules exist.

