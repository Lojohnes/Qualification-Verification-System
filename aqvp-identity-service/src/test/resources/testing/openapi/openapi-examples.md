# AQVP Identity & Access API - OpenAPI/Swagger Examples

The Identity module exposes the following endpoint groups. Swagger UI is available at `http://localhost:8081/swagger-ui.html` and the OpenAPI JSON at `http://localhost:8081/v3/api-docs`.

## Authentication

### POST /api/v1/auth/login

**Request**
```json
{
  "usernameOrEmail": "admin",
  "password": "Admin123!"
}
```

**Response 200**
```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "{{refresh_token}}",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### POST /api/v1/auth/refresh

**Request**
```json
{
  "refreshToken": "{{refresh_token}}"
}
```

**Response 200**
Same shape as login.

### POST /api/v1/auth/logout

**Request**
```json
{
  "refreshToken": "{{refresh_token}}"
}
```

**Response 204** No content.

### POST /api/v1/auth/change-password

**Request**
```json
{
  "currentPassword": "Admin123!",
  "newPassword": "NewPass123!",
  "confirmPassword": "NewPass123!"
}
```

**Response 204** No content.

## User Management

Requires `user:read` / `user:write` / `user:delete`.

### GET /api/v1/users

**Response 200**
```json
[
  {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "username": "admin",
    "email": "admin@aqvp.local",
    "firstName": "System",
    "lastName": "Administrator",
    "enabled": true,
    "emailVerified": true,
    "mfaEnabled": false,
    "roles": ["ADMIN"]
  }
]
```

### POST /api/v1/users

**Request**
```json
{
  "username": "johndoe",
  "email": "john@aqvp.local",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe",
  "roleIds": ["role-uuid-here"]
}
```

**Response 201**
Same user shape with generated `id` and `Location` header.

## Role Management

Requires `role:read` / `role:write`.

### GET /api/v1/roles

**Response 200**
```json
[
  {
    "id": "role-uuid",
    "name": "ADMIN",
    "description": "Administrator with full access",
    "permissions": ["user:read", "user:write", "user:delete", "role:read", "role:write"]
  }
]
```

## Permission Management

Requires `role:read`.

### GET /api/v1/permissions

**Response 200**
```json
[
  {
    "id": "perm-uuid",
    "name": "user:read",
    "resource": "user",
    "action": "read",
    "description": "Read users"
  }
]
```

## Error Responses

All error responses use the `ErrorResponse` schema:

```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/v1/auth/login",
  "fieldErrors": {}
}
```
