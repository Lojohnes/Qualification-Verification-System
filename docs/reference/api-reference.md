# API Reference

Last updated: 2026-08-15

## Base URLs

Current implemented services:

- Identity service: `http://localhost:8081` (Swagger UI: `/swagger-ui.html`, OpenAPI JSON: `/v3/api-docs`)
- Qualification service (Institution module): `http://localhost:8082`

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

The Institution module (Institutions, Programs) is implemented; remaining business module APIs (Qualification records, Verification, Document, Audit, Notification) are not implemented yet. Future APIs should follow `/api/v1/<plural-resource>` naming, DTO boundaries, OpenAPI annotations, pagination for list endpoints, and permission checks using Identity authorities.
