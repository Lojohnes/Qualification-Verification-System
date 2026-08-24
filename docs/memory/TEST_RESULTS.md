# Test Results

Last updated: 2026-08-20

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
| 2026-08-20 | Verification implementation sanity scan | PowerShell/rg | New implementation has no lines over 120 chars and no obvious debug leftovers | Static scan passed | Passed |
| 2026-08-20 | Verification Maven tests | Maven/JUnit/Mockito | Run new Verification and Qualification snapshot tests | Blocked: `mvn` is not installed and no `mvnw` wrapper exists | Blocked |

## Aggregate Reported Result

```text
Tests run: 116 recorded before new Verification slice
Failures: 0
Errors: 0
Skipped: 1
Build: SUCCESS for last Maven-capable run; new Verification tests not executed locally
```

## Outstanding Testing Requirements

- Run Testcontainers migration tests in Docker-capable CI/local environment.
- Run the new Verification and Qualification snapshot tests in a Maven-capable environment.
- Add controller, security, repository, and integration tests for the Verification service.
- Add tests for admin/audit, document, notification, and frontend modules as those features are implemented.
- Add end-to-end acceptance tests for the full issue/generate/verify lifecycle after business modules exist.

