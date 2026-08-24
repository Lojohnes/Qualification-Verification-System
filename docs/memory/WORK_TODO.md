# Work TODO

Last updated: 2026-08-24

## Highest Priority

| Priority | Task | Dependencies | Acceptance Criteria | Status |
|---|---|---|---|---|
| P0 | Resolve Java version mismatch. | Root POM, identity POM, onboarding docs, CI. | All setup/build docs and Maven compiler settings agree on the intended Java version. | Complete |
| P0 | Add or remove frontend `/api/v1/auth/me` contract. | Identity auth service/controller, frontend auth service. | Current-user flow is implemented and tested, or frontend no longer calls a missing endpoint. | Complete |
| P0 | Implement real password reset or remove placeholder behavior from exposed UX. | Identity service, notification provider. | Reset flow creates/verifies tokens and dispatches notification, or endpoints are clearly marked unavailable. | Pending |
| P0 | Implement first business module domain and API. | Identity permission model, module development standards. | At least one business module has migrations, entities, DTOs, services, controllers, OpenAPI docs, and tests. | Complete |

## Backend Business Modules

| Priority | Task | Notes | Status |
|---|---|---|---|
| P1 | Institution module: institutions, faculties, departments, programs. | Unique institution code and tenant boundaries are core rules. | Complete |
| P1 | Qualification module: students, qualification records, issuance, amendment, revocation. | Must preserve history; avoid permanent deletes for qualifications. | Complete |
| P1 | Verification module: request intake, consent validation, QR verification, result tracking. | First backend slice implemented; still needs local/CI Maven verification, broader controller/security/integration tests, certificate upload/OCR, holder-token consent, rate limiting, and frontend screens. | In Progress |
| P1 | Document module: certificate templates, PDF generation, QR embedding, digital signatures, storage metadata. | Initial implementation complete in Qualification service with local storage, generated PDFs/QR PNGs, HMAC signatures, and metadata. Remaining production work: templates, external object storage, async generation, PKI signing. | In Progress |
| P1 | Audit module: immutable event model and searchable audit log. | Capture actor, organization, role, action, resource, previous/new values, timestamp, IP/device where appropriate. | Pending |
| P2 | Notification module: email/SMS abstraction, templates, retry logging. | Provider configuration may need mock profile for local/dev. | Pending |

## Frontend

| Priority | Task | Notes | Status |
|---|---|---|---|
| P1 | Align API base URL strategy with gateway or direct service calls. | Current default is `http://localhost:8081`. | Pending |
| P1 | Replace business module placeholder pages with working screens as APIs land. | Institution module (Institutions, Programs) and Qualification module (Students, Qualifications) implemented against `aqvp-qualification-service` on port `8082`. | Complete |
| P1 | Add Faculty/Department selector to the Program form once Faculty/Department REST endpoints exist. | Backend has schema/repositories only; no controller yet. Program form currently accepts a manually entered Department UUID. | Pending |
| P1 | Seed or grant `institution:read`, `institution:write`, `program:read`, `program:write` authorities to the default admin role in Identity. | Required for the new Institution/Program frontend screens to return data instead of 403. | Pending |
| P2 | Add role/permission-aware navigation and action visibility. | Must use Identity authorities. | Pending |
| P2 | Add frontend tests. | Current package scripts do not include a test runner. | Pending |

## Testing

| Priority | Task | Notes | Status |
|---|---|---|---|
| P1 | Run full Maven verification in current workspace. | Especially after Java version decision. | Complete |
| P1 | Run Maven verification for new Verification slice. | `mvn` is unavailable in the current shell and the repo has no Maven wrapper. Run once Maven is installed or wrapper is added. | Pending |
| P1 | Run Identity Testcontainers migration test in Docker-capable environment. | Currently reported skipped when Docker unavailable. | Pending |
| P1 | Add tests for any new business module before merge. | Unit, repository, controller, security, and integration tests. | Complete — Qualification service now has 61 passing tests including the Document module slice. |
| P2 | Add E2E tests for issue-to-verify workflow once modules exist. | Candidate tools: Playwright or Cypress. | Pending |

## Documentation

| Priority | Task | Notes | Status |
|---|---|---|---|
| P1 | Keep docs/memory updated after every substantial session. | Required by project operating rules. | Ongoing |
| P1 | Update Group 2 reference docs whenever implementation changes. | Especially API, database, security, deployment, and testing guides. | Ongoing |
| P2 | Keep legacy source docs archived under `docs/reference/legacy`. | Avoid duplicate authoritative sources; use current `docs/reference` files as authoritative. | Ongoing |
