# Project Goals

Last updated: 2026-08-10

## Main Objectives

- Provide a trusted digital platform for verifying academic qualifications.
- Allow authorized educational institutions to create and issue secure, verifiable qualification records and certificates.
- Let employers and other approved organizations verify qualifications through QR scanning, certificate upload, and authoritative record lookup.
- Maintain auditability, tenant isolation, and strong access control across all qualification lifecycle events.

## Functional Goals

- Implement Identity and Access Management with JWT authentication, refresh tokens, roles, permissions, and user management.
- Implement organization onboarding and administration for Read-Only and Read & Write clients.
- Implement Institution, Faculty, Department, and Program management.
- Implement Student Records and Qualification Records.
- Implement qualification issuance, amendment, revocation, and status lifecycle.
- Generate secure security identifiers and QR codes for issued qualifications.
- Generate PDF certificates from approved templates.
- Support CSV bulk import of graduate and qualification data.
- Support verification requests, QR verification, certificate upload verification, result tracking, and reports.
- Capture immutable or tamper-resistant audit events.
- Add email/SMS notification capabilities behind provider abstractions.
- Provide a React frontend with authentication, protected routes, identity administration, dashboards, and future business module screens.

## Non-Functional Goals

- Enforce tenant isolation and least-privilege access.
- Maintain at least 80 percent code coverage for production modules.
- Keep APIs documented with OpenAPI/Swagger and request/response examples.
- Use stable database migrations and avoid modifying merged Flyway scripts.
- Pass Maven build, tests, Checkstyle, SpotBugs, and coverage collection in CI.
- Avoid hard-coded secrets and environment-specific values.
- Keep modules cohesive and communicate across boundaries through REST APIs or events.
- Use HTTPS/TLS, strong JWT secrets, BCrypt password hashing, and cryptographically secure identifiers in production.

## Success Criteria

- Identity service remains stable and integrated as the access-control foundation.
- Each business module has entities, migrations, DTOs, services, controllers, OpenAPI docs, tests, and security rules.
- End-to-end flow works from issuing a qualification to generating a certificate and verifying it later.
- Read-Only organizations cannot mutate qualification records.
- Read & Write organizations can only manage qualifications within their authorized issuing scope.
- Audit records exist for creation, correction, revocation, verification, and security-sensitive actions.
- Documentation reflects the actual implementation at the end of every substantial session.

## Milestones

| Milestone | Target State | Current Status |
|---|---|---|
| Sprint 1 | Identity and Access complete | Reported complete and tested |
| Sprint 2 | Core business modules and frontend foundation | Business modules scaffolded; frontend foundation present |
| v0.1.0-identity | Identity release tag | Referenced in docs; verify tag/branch state before release work |
| v0.2.0 | Integrated core qualification, verification, document, audit, notification increment | Not yet implemented in current source |

