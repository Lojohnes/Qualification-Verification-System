# Memory Summary

Last updated: 2026-08-10

AQVP is a secure, multi-tenant Academic Qualification Verification Platform. Its purpose is to let authorized educational institutions issue verifiable qualifications and let employers or other approved organizations verify them through QR codes, certificate upload, and authoritative record lookup.

The repo is a Spring Boot multi-module backend plus React/Vite frontend. Root modules are `aqvp-shared-kernel`, `aqvp-identity-service`, `aqvp-qualification-service`, `aqvp-verification-service`, `aqvp-admin-service`, and `aqvp-api-gateway`. The frontend lives in `frontend/aqvp-web`.

Identity is the only fully implemented backend bounded context. It includes JWT auth, refresh tokens, users, roles, permissions, API client auth infrastructure, Flyway migrations, Swagger setup, and broad tests. Qualification, verification, admin/audit, document, and notification work remain mostly unimplemented/scaffolded.

Critical constraints:

- Do not rewrite or duplicate Identity.
- New modules must use Identity JWTs and permissions.
- No shared database access across module boundaries.
- QR codes identify/authenticate qualification records; server-side data is authoritative.
- Read-Only organizations verify only; Read & Write institutions may issue within authorized scope.
- Client organizations have at most three active users: IT Admin, HR Manager, Director.
- Record important changes in `CHANGELOG.md`, decisions in `DECISIONS.md`, issues in `ISSUES_AND_BLOCKERS.md`, and handoff in `SESSION_HANDOFF.md`.

Current priorities:

- Resolve Java 21 vs Java 17 mismatch.
- Align frontend `/api/v1/auth/me` with backend.
- Implement first business module using Identity as the reference.
- Harden Identity password reset, refresh-token storage, rate limiting, and production CORS.

