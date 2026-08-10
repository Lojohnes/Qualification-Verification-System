# Domain Model

Last updated: 2026-08-10

## Core Domains

| Domain | Purpose | Current State |
|---|---|---|
| Identity | Users, roles, permissions, refresh tokens, API clients. | Implemented. |
| Organization / Institution | Platform clients, issuing institutions, departments, faculties, programs. | Planned. |
| Qualification | Students, qualification records, issuance, amendment, revocation. | Planned. |
| Document | Certificate templates, generated certificates, QR codes, digital signatures, storage metadata. | Planned. |
| Verification | Verification requests, consent, verification results, evidence comparison. | Planned. |
| Audit | Immutable audit events across security and business workflows. | Planned. |
| Notification | Email/SMS notification templates, dispatch, retry records. | Planned. |

## Implemented Identity Entities

| Entity | Responsibility | Key Fields / Relationships |
|---|---|---|
| `BaseEntity` | Shared identity entity base. | `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `version`. |
| `User` | Human user account. | Unique `username`, unique `email`, `password`, names, enabled/account flags, email/MFA flags, many-to-many `roles`, one-to-many `refreshTokens`. |
| `Role` | Named role containing permissions. | Unique `name`, `description`, many-to-many `permissions`, many-to-many `users`. |
| `Permission` | Fine-grained authority. | Unique `name`, `resource`, `action`, `description`. |
| `RefreshToken` | Long-lived refresh credential. | Unique `token`, `expiryDate`, `revoked`, many-to-one `user`. |
| `ApiClient` | Machine-to-machine client. | Unique `clientId`, `clientSecretHash`, `name`, `description`, `enabled`, authorities collection. |

## Planned Business Entities

| Entity | Responsibility |
|---|---|
| Organization | Registered AQVP client, classified as Read-Only or Read & Write. |
| Institution | Qualification-issuing organization or educational institution. |
| Faculty | Institution subdivision. |
| Department | Faculty or institution subdivision. |
| Program | Academic program offered by an institution. |
| Student / Qualification Holder | Person awarded a qualification. |
| Qualification | Authoritative academic record. |
| Certificate | Generated certificate artifact tied to a qualification. |
| Security Identifier | Cryptographically secure identifier for a qualification. |
| QR Record | QR verification reference tied to an authoritative record. |
| Verification Request | Attempt to verify a qualification. |
| Verification Result | Outcome of a verification attempt. |
| Audit Event | Immutable record of important system action. |
| Notification | Message request and delivery state. |

## Important Relationships

- A user belongs to one client organization in the planned tenant model.
- A client organization may have no more than three active client users.
- An institution owns its academic structures and issued qualifications.
- A qualification belongs to one issuing institution and one qualification holder.
- A certificate belongs to one qualification and carries a QR/security reference.
- A verification request references supplied evidence and, when matched, a qualification.
- Audit events reference the actor, organization, action, resource, and optional before/after values.

