# Training Manual

Last updated: 2026-08-10

## Audience

This manual is for AQVP administrators, organization users, and developers supporting current early-stage functionality.

## Current User-Facing Capabilities

Implemented UI areas:

- Login page.
- Register page placeholder/surface.
- Forgot/reset password pages, backed by placeholder backend reset endpoints.
- Protected dashboard.
- Identity pages for users, roles, and permissions.
- Placeholder pages for institution, qualification, verification, documents, audit, reports, and settings.

## Administrator Workflow: Identity

1. Start the Identity service.
2. Open Swagger UI at `http://localhost:8081/swagger-ui.html` or use the frontend.
3. Log in with a seeded or created administrative user.
4. Use user endpoints/pages to create and update users.
5. Use role endpoints/pages to create and update roles.
6. Use permission endpoints/pages to inspect available permissions.

## Login Workflow

1. Navigate to the frontend login page.
2. Enter username/email and password.
3. On success, the frontend stores tokens and opens protected routes.
4. If access token expires, Axios interceptors attempt refresh.
5. If refresh fails, the session expired dialog appears and the user returns to login.

## Planned Verification Workflow

1. User scans a QR code or uploads a certificate.
2. AQVP extracts a verification reference.
3. AQVP retrieves the authoritative qualification record.
4. AQVP compares supplied details against authoritative details.
5. AQVP returns a clear verification result.
6. AQVP records an audit event.

## Planned Issuance Workflow

1. Authorized institution user imports CSV data or enters graduate data manually.
2. AQVP validates required fields, duplicates, and issuing scope.
3. AQVP creates qualification records.
4. AQVP generates security identifiers and QR codes.
5. AQVP merges data into certificate templates.
6. AQVP generates certificates.
7. AQVP registers certificates as issued.
8. Records become available for verification.

## Troubleshooting

| Problem | Likely Cause | Action |
|---|---|---|
| Cannot log in | Identity service is not running or credentials are wrong. | Start Identity on port 8081 and use seeded admin credentials for local/dev. |
| Swagger unavailable | Service not running or wrong profile. | Start Identity and browse to `/swagger-ui.html`. |
| Frontend cannot fetch current user | Backend currently lacks `/api/v1/auth/me`. | Implement endpoint or adjust frontend auth flow. |
| Future module page shows placeholder | Business module UI/API not implemented yet. | Track module implementation in `docs/memory/WORK_TODO.md`. |
| Docker-related tests skip | Docker unavailable. | Run in a Docker-capable environment. |

