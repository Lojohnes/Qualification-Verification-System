# Issues and Blockers

Last updated: 2026-08-10

## Open Issues

| ID | Severity | Area | Issue | Impact | Workaround / Next Action | Status |
|---|---|---|---|---|---|---|
| DOC-001 | Medium | Docs/build | Root build and CI use Java 21, but older onboarding text references Java 17 and identity module overrides compiler release to 17. | Developer setup confusion and inconsistent bytecode targets. | Decide project-wide target and update identity POM/onboarding accordingly. | Open |
| API-001 | Medium | Identity/frontend | Frontend defines `/api/v1/auth/me`, but current `AuthController` has no `GET /api/v1/auth/me` endpoint. | Current-user fetch may fail if UI calls it. | Add endpoint or remove/adjust frontend usage after confirming intended auth flow. | Open |
| API-002 | Medium | Gateway/frontend | API gateway routes exist on port 8080, but frontend default base URL is identity service on port 8081. | Frontend cannot transparently access future modules unless configured differently. | Decide gateway-first vs direct-service development mode. | Open |
| SEC-001 | High | Identity security | Password reset endpoints are placeholders that only accept requests. | Users cannot complete real self-service reset. | Implement reset token persistence and notification dispatch or hide endpoints until ready. | Open |
| SEC-002 | High | Identity security | Refresh tokens are stored as raw token values. | Database leak could expose long-lived tokens. | Store token hashes and compare by hash. | Open |
| SEC-003 | High | Identity security | No rate limiting or account lockout for login endpoints. | Brute-force attack risk. | Add failed-login tracking and/or gateway/reverse-proxy rate limits. | Open |
| SEC-004 | Medium | Identity security | CORS currently allows all origins. | Too broad for production browser clients. | Restrict production origins via config. | Open |
| API-003 | Low | Identity | API client service exists without REST CRUD controller. | Machine client lifecycle requires manual/seed handling. | Add admin-only `ApiClientController` if operations require it. | Open |
| TEST-001 | Medium | Testing | Testcontainers Flyway integration test skipped when Docker is unavailable. | PostgreSQL migration behavior not validated locally in non-Docker environments. | Run in CI or local Docker-capable environment. | Open |
| MOD-001 | High | Business modules | Qualification, verification, and admin services are scaffolds without domain implementations. | Core AQVP workflows are not available. | Implement Sprint 2/Sprint 3 module backlogs. | Open |

## Blockers

No hard technical blocker prevents documentation work. Feature development is blocked only by ordinary backlog execution and decisions listed above.

## Resolved Issues

| ID | Date | Area | Resolution |
|---|---|---|---|
| None recorded | 2026-08-10 | N/A | Start adding resolved issues here as they are fixed. |

