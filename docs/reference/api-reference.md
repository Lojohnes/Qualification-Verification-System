# API Reference

Last updated: 2026-08-10

## Base URLs

Current implemented Identity service:

- Direct: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

Gateway routes exist on `http://localhost:8080`, but the frontend currently defaults to direct Identity service access at `http://localhost:8081`.

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

Business module APIs are not implemented yet. Future APIs should follow `/api/v1/<plural-resource>` naming, DTO boundaries, OpenAPI annotations, pagination for list endpoints, and permission checks using Identity authorities.
