# Business Rules

Last updated: 2026-08-10

## Platform Rules

- Every client organization must operate within its own tenant boundary.
- Every client is classified as Read-Only or Read & Write.
- A client organization may have fewer than three active users but never more than three active client-user accounts.
- Client user roles are IT Admin, HR Manager, and Director.
- Super Admin and System Admin operate at platform level.
- Users must not access another client's private administrative information.
- Deactivated users lose access immediately.

## Access Category Rules

- Read-Only organizations can verify qualifications and view permitted verification results.
- Read-Only organizations cannot create, alter, revoke, or generate qualifications/certificates/QR codes.
- Read & Write organizations can verify qualifications and create/maintain qualification records only within their authorized issuing scope.
- Read & Write status must only be granted to appropriately verified qualification-awarding organizations.

## Qualification Rules

- Every qualification must have a unique system record/reference.
- Every AQVP-generated qualification must have a unique security identifier.
- Duplicate qualification/security identifiers are forbidden.
- Qualification records should not normally be permanently deleted; revocation or superseding must preserve history.
- CSV imports must be validated before records are committed.
- Issuing institutions can only modify qualifications they are authorized to issue.

## QR and Verification Rules

- The server-side qualification record is authoritative.
- QR codes identify and authenticate records; they should not expose unnecessary sensitive biodata.
- Verification must compare supplied certificate details against the authoritative record.
- Supported verification result categories are:
  - `VERIFIED`
  - `NOT VERIFIED`
  - `DETAILS MISMATCH`
  - `REVOKED` or `WITHDRAWN`
  - `REQUIRES REVIEW`
- Certificate upload should support PDF, JPG, JPEG, and PNG when implemented.
- If a QR code is detected in an uploaded certificate, QR verification should be used.

## Audit Rules

- Qualification creation, correction, revocation, certificate generation, QR generation, and verification events must be auditable.
- Audit events should capture actor, organization, role, timestamp, action, resource, previous values, new values, verification request, and IP/device information where appropriate.
- Audit records should be immutable or tamper-resistant.

## Security Rules

- All non-public endpoints require authentication.
- Authorization uses Identity permissions, typically `<resource>:<action>`.
- Passwords must be hashed with BCrypt.
- Secrets, tokens, and credentials must not be logged or committed.
- Production requires HTTPS/TLS and strong environment-provided JWT secrets.
- Production QR/security identifiers must use cryptographically secure randomness and/or signatures.

