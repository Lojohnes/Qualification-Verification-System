# AQVP Identity Service - Sample Requests & Responses

Base URL: `http://localhost:8081`

## Test Accounts

| Username | Password | Authorities |
|---|---|---|
| `admin` | `Admin123!` | `user:read`, `user:write`, `user:delete`, `role:read`, `role:write` |
| `system_admin` | `Password123!` | all |
| `institution_admin` | `Password123!` | `user:read`, `user:write`, `role:read` |
| `registrar` | `Password123!` | `user:read`, `user:write` |
| `verifier` | `Password123!` | `user:read` |
| `auditor` | `Password123!` | `user:read`, `role:read` |

## Login

### Request
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin123!"}'
```

### Response 200
```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

## Create User

### Request
```bash
curl -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <accessToken>" \
  -d '{
    "username": "johndoe",
    "email": "john@aqvp.local",
    "password": "Password123!",
    "firstName": "John",
    "lastName": "Doe",
    "roleIds": ["role-uuid-here"]
  }'
```

### Response 201
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "username": "johndoe",
  "email": "john@aqvp.local",
  "firstName": "John",
  "lastName": "Doe",
  "enabled": true,
  "emailVerified": false,
  "mfaEnabled": false,
  "roles": ["USER"]
}
```

## Validation Error

### Response 400
```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "fieldErrors": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters and contain uppercase, lowercase, digit and special character"
  }
}
```

## Access Denied

### Response 403
```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/v1/users",
  "fieldErrors": {}
}
```

## Unauthorized

### Response 401
```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required",
  "path": "/api/v1/users",
  "fieldErrors": {}
}
```
