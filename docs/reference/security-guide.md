# Security Guide

Last updated: 2026-08-10

## Authentication

The implemented Identity service uses Spring Security with stateless JWT authentication.

Current auth mechanisms:

- Username/email plus password login.
- JWT access token.
- Refresh token.
- API client authentication filter using configured client ID/secret headers.
- BCrypt password hashing.

Configured API client headers:

- `X-Api-Client-Id`
- `X-Api-Client-Secret`

## Authorization

Authorization is permission-based. Permissions follow:

```text
<resource>:<action>
```

Current seeded permissions:

- `user:read`
- `user:write`
- `user:delete`
- `role:read`
- `role:write`

Current seeded roles:

- `ADMIN`
- `USER`

Future modules should add permissions such as:

- `institution:read`, `institution:write`
- `qualification:read`, `qualification:write`, `qualification:revoke`
- `verification:create`, `verification:read`, `verification:execute`
- `document:generate`, `document:read`
- `audit:read`

## Public Endpoints

Current security config permits:

- `/api/v1/auth/**`
- `/swagger-ui.html`
- `/swagger-ui/**`
- `/v3/api-docs`
- `/v3/api-docs/**`
- `/actuator/health`

Review whether all `/api/v1/auth/**` endpoints should be public as implementation hardens.

## Data Protection

- Never store or commit real secrets.
- Production `JWT_SECRET` must be strong and environment-provided.
- Use HTTPS/TLS in production.
- Avoid logging credentials, JWTs, refresh tokens, API secrets, or unnecessary PII.
- Sensitive QR payloads should be signed and minimal.
- Passwords must remain BCrypt-hashed.

## Known Security Gaps

- Password reset endpoints are placeholders.
- Refresh tokens are stored as raw token values.
- No login rate limiting or account lockout is implemented.
- CORS allows all origins.
- MFA flag exists on user records but MFA behavior is not implemented.
- Current QR/security identifier implementation does not exist yet.

## Threat Considerations

- Credential stuffing and brute force against login.
- Refresh token database exposure.
- Cross-tenant data leakage.
- Unauthorized qualification mutation by Read-Only organizations.
- QR tampering or replay.
- Certificate document forgery.
- Audit log tampering.
- Overexposure of personal data in QR payloads, logs, API responses, or uploaded files.

