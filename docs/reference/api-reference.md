# API Reference

Last updated: 2026-08-20

## Base URLs

Current implemented services:

- Identity service: `http://localhost:8081` (Swagger UI: `/swagger-ui.html`, OpenAPI JSON: `/v3/api-docs`)
- Qualification service (Institution module): `http://localhost:8082`
- Verification service: `http://localhost:8083`

Gateway routes exist on `http://localhost:8080`, but the frontend currently defaults to direct service access: `VITE_API_BASE_URL` (default `http://localhost:8081`) for Identity and `VITE_QUALIFICATION_API_BASE_URL` (default `http://localhost:8082`) for Qualification.

## Authentication

Use:

```text
Authorization: Bearer <accessToken>
```

Implemented auth endpoints:

| Method | Path | Purpose | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Authenticate and return access/refresh tokens. | Public |
| POST | `/api/v1/auth/logout` | Revoke refresh token. | Public by current path rule; request requires refresh token. |
| POST | `/api/v1/auth/refresh` | Refresh access token. | Public by current path rule; request requires refresh token. |
| POST | `/api/v1/auth/change-password` | Change authenticated user's password. | Public by current path rule in `SecurityConfig`; service behavior should be reviewed. |
| POST | `/api/v1/auth/forgot-password` | Password reset request placeholder. | Public |
| POST | `/api/v1/auth/reset-password` | Password reset placeholder. | Public |

Known mismatch:

- Frontend defines `GET /api/v1/auth/me`, but current `AuthController` does not implement it.

## Identity Endpoints

| Method | Path | Purpose | Required Authority |
|---|---|---|---|
| GET | `/api/v1/users` | List users. | `user:read` |
| POST | `/api/v1/users` | Create user. | `user:write` |
| PUT | `/api/v1/users/{id}` | Update user. | `user:write` |
| DELETE | `/api/v1/users/{id}` | Delete user. | `user:delete` |
| GET | `/api/v1/roles` | List roles. | `role:read` |
| GET | `/api/v1/roles/{id}` | Get role by ID. | `role:read` enforced at service method level. |
| POST | `/api/v1/roles` | Create role. | `role:write` |
| PUT | `/api/v1/roles/{id}` | Update role. | `role:write` enforced at service method level. |
| GET | `/api/v1/permissions` | List permissions. | `role:read` |

## Qualification Service Endpoints (Institution Module)

| Method | Path | Purpose | Required Authority |
|---|---|---|---|
| GET | `/api/v1/institutions` | List institutions. | `institution:read` |
| GET | `/api/v1/institutions/{id}` | Get institution by ID. | `institution:read` |
| POST | `/api/v1/institutions` | Create institution. | `institution:write` |
| PUT | `/api/v1/institutions/{id}` | Update institution. | `institution:write` |
| DELETE | `/api/v1/institutions/{id}` | Deactivate institution (soft, no hard delete). | `institution:write` |
| GET | `/api/v1/programs` | List programs; supports optional `?institutionId=` filter. | `program:read` |
| GET | `/api/v1/programs/{id}` | Get program by ID. | `program:read` |
| POST | `/api/v1/programs` | Create program. | `program:write` |
| PUT | `/api/v1/programs/{id}` | Update program. | `program:write` |
| DELETE | `/api/v1/programs/{id}` | Delete program. | `program:write` |

## Qualification Service Internal Verification Endpoints

These endpoints are used by `aqvp-verification-service` to resolve authoritative qualification snapshots without direct database access.

| Method | Path | Purpose | Required Authority |
|---|---|---|---|
| GET | `/api/v1/internal/qualifications/verification-snapshots/by-security-identifier/{securityIdentifier}` | Read minimal authoritative snapshot for a QR/security identifier. | `qualification:verify` |
| GET | `/api/v1/internal/qualifications/verification-snapshots/by-number/{qualificationNumber}` | Read minimal authoritative snapshot by qualification number. | `qualification:verify` |

## Verification Service Endpoints

| Method | Path | Purpose | Required Authority |
|---|---|---|---|
| POST | `/api/v1/verification-requests` | Create a tracked verification request with optional consent/evidence. | `verification:write` |
| GET | `/api/v1/verification-requests` | List verification requests. | `verification:read` |
| GET | `/api/v1/verification-requests/{id}` | Read a verification request and latest result summary. | `verification:read` |
| POST | `/api/v1/verification-requests/{id}/consent-validation` | Validate or update request consent. | `verification:write` |
| POST | `/api/v1/verification-requests/{id}/qr-verification` | Verify a QR payload under an existing request. | `verification:write` |
| POST | `/api/v1/verifications/qr` | Create a request and run QR verification in one call. | `verification:write` |
| GET | `/api/v1/verifications/{resultId}` | Read a persisted verification result. | `verification:read` |

Example QR verification request:

```json
{
  "qrPayload": "AQVP:v1:MSU:83a29aa2-8bf1-4f75-822f-f43c036fd2de",
  "purpose": "EMPLOYMENT",
  "consent": {
    "consentType": "ATTESTED_BY_VERIFIER",
    "scope": "BASIC_DETAILS",
    "grantedAt": "2026-08-20T10:30:00",
    "expiresAt": "2026-09-19T10:30:00",
    "consentReference": "HR-FILE-2026-0091"
  },
  "evidence": {
    "holderFirstName": "Amina",
    "holderLastName": "Dube",
    "qualificationName": "Bachelor of Science in Computer Science",
    "yearOfAward": 2024
  }
}
```

Example QR verification response:

```json
{
  "verificationRequestId": "7f74bb0a-7e9a-4828-9487-b3caaeed7046",
  "resultId": "d439a1c3-3eca-4db9-acb9-90e320c1bf62",
  "outcome": "VERIFIED",
  "confidence": "HIGH",
  "matchScore": 100,
  "qualification": {
    "qualificationNumber": "MSU-BSC-2024-0001",
    "qualificationName": "Bachelor of Science in Computer Science",
    "qualificationType": "DEGREE",
    "classification": "First Class",
    "yearOfAward": 2024,
    "status": "ISSUED",
    "issuedAt": "2026-08-18T14:30:00",
    "institutionName": "Midlands State University"
  },
  "holder": {
    "firstName": "Amina",
    "lastName": "Dube"
  },
  "matchDetails": [],
  "verifiedAt": "2026-08-20T10:31:12"
}
```

Known gaps:

- No Faculty/Department REST endpoints exist yet. `ProgramRequestDto.departmentId` must be a valid UUID from the `departments` table; the frontend Program form currently requires this to be entered manually.
- The Identity seed migration (`V2__seed_roles_permissions.sql`) does not grant `institution:*`/`program:*` authorities to the default admin role, so these endpoints currently return `403` for the seeded admin user until permissions are added.

## Example Login Request

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123!"}'
```

Example response shape:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<refresh-token>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

## Error Responses

Current Identity examples use:

```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "fieldErrors": {}
}
```

Common statuses:

- `400`: validation failure.
- `401`: missing/invalid authentication.
- `403`: missing permission.
- `404`: resource not found.
- `409`: duplicate/conflicting state.
- `500`: unexpected server error.

## Planned APIs

Document, Audit, Notification, certificate upload/OCR verification, and frontend Verification screens are not implemented yet. Future APIs should follow `/api/v1/<plural-resource>` naming, DTO boundaries, OpenAPI annotations, pagination for list endpoints, and permission checks using Identity authorities.
