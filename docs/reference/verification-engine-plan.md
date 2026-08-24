# AQVP Verification Engine Design and Implementation Plan

Last updated: 2026-08-20

## Current Codebase Context

AQVP is a Spring Boot 3.3.5 multi-module project with Identity, Qualification, Verification, Admin, Gateway, and React frontend modules. Identity is the reference implementation for JWT security, permissions, exception handling, DTO records, MapStruct mappers, Flyway, and testing. Qualification is implemented for institutions, students, and qualification lifecycle. Verification is currently only a scaffold with `VerificationServiceApplication`, application configuration, and Maven dependencies.

Important constraints from memory and source:

- Do not duplicate Identity or read the Identity database from other services.
- Business modules validate Identity-issued JWTs locally and authorize by permissions.
- No shared database access across bounded contexts.
- Qualification records in `aqvp-qualification-service` are authoritative.
- QR codes identify/authenticate qualification records; they must not contain full biodata.
- `Qualification.securityIdentifier` is generated on issue and is already indexed/unique.
- Current Qualification API exposes lookups by id, student id, and institution id, but not by `securityIdentifier`.
- Verification service runs on port `8083`; gateway route `/verification/**` strips the prefix and forwards to it.

## Assumptions

1. A verifier may be an authenticated client organization user or an approved machine client. Public anonymous QR previews are out of scope for the first secure implementation.
2. Consent is required before returning personal details. A QR scan may disclose only a minimal status until consent is valid.
3. Consent can initially be represented by request metadata and evidence fields, then replaced later by a stronger holder-facing consent workflow.
4. The first implementation supports QR verification and structured lookup. Certificate upload/OCR is explicitly deferred, but the data model leaves room for uploaded evidence.
5. Verification service calls Qualification service through REST using an internal service credential or propagated JWT, not direct database access.
6. Verification outcomes are persisted for traceability, rate-limit analysis, and future audit integration.
7. Digital QR signatures are recommended for production. The first slice may parse a signed or unsigned AQVP payload, but must always confirm against the server-side qualification record.

## Target Outcomes

The Verification Engine should support:

- Creating and tracking verification requests.
- Validating consent before disclosing personal or qualification details.
- Resolving QR payloads to authoritative qualification records.
- Comparing submitted evidence with authoritative records.
- Returning consistent verification results and storing each attempt.
- Exposing REST APIs that follow existing `/api/v1/<plural-resource>` conventions.
- Publishing or preparing audit events for verification attempts.

## Main Components

### Verification Service Components

| Component | Responsibility |
|---|---|
| `VerificationRequestController` | Create/list/read verification requests and run verification actions. |
| `QrVerificationController` | QR-specific verification shortcut endpoints. Can be merged into request controller if the surface stays small. |
| `VerificationRequestService` | Own request creation, state transitions, requester metadata, and persistence. |
| `ConsentValidationService` | Validate consent type, holder identifiers, expiry, scope, and disclosure rules. |
| `QrPayloadParser` | Parse QR content, normalize payloads, validate expected AQVP format, extract `securityIdentifier`, and optionally verify QR signature. |
| `QualificationLookupClient` | REST client for Qualification service internal verification snapshot endpoints. |
| `VerificationMatchingService` | Compare submitted evidence against authoritative qualification snapshot and produce match details. |
| `VerificationResultService` | Persist result, normalize outcome, redact response fields by consent level. |
| `AuditEventPublisher` | Publish verification events to Kafka or temporarily log/persist outbox rows until Admin/Audit exists. |
| `VerificationSecurityConfig` | Stateless JWT validation, CORS, route authorization, and public health/OpenAPI exemptions. |
| `GlobalExceptionHandler` | Match existing Identity/Qualification error shape. |

### Qualification Service Additions

| Component | Responsibility |
|---|---|
| `QualificationVerificationController` | Internal/read-only endpoint for verification snapshots by `securityIdentifier` and optionally qualification number. |
| `QualificationVerificationService` | Build minimal authoritative snapshot without exposing unnecessary fields. |
| `QualificationVerificationSnapshotDto` | Stable service-to-service contract consumed by Verification service. |
| Security rule | Require internal authority such as `qualification:verify` or `service:verification`. |

## Domain Model

### VerificationRequest

Stores the business request to verify a qualification.

Fields:

- `id` UUID primary key.
- `requestReference` unique readable reference, e.g. `VR-20260820-000001`.
- `requesterUserId` optional UUID from JWT subject/claim.
- `requesterUsername` from principal.
- `requesterOrganizationId` optional UUID claim when tenant model is available.
- `requesterOrganizationName` optional.
- `channel` enum: `QR`, `LOOKUP`, `UPLOAD`, `API`.
- `purpose` enum: `EMPLOYMENT`, `ADMISSION`, `REGULATORY`, `BACKGROUND_CHECK`, `OTHER`.
- `status` enum: `PENDING_CONSENT`, `READY`, `PROCESSING`, `COMPLETED`, `FAILED`, `EXPIRED`, `CANCELLED`.
- `consentStatus` enum: `NOT_REQUIRED`, `PENDING`, `VALID`, `INVALID`, `EXPIRED`.
- `qualificationId` nullable authoritative UUID after match.
- `securityIdentifierHash` nullable SHA-256 hash of QR security identifier for traceability without leaking raw tokens.
- `createdAt`, `updatedAt`, `expiresAt`, `completedAt`.
- `createdBy`, `updatedBy`, `version`.

### ConsentRecord

Stores consent evidence linked to a request.

Fields:

- `id` UUID primary key.
- `verificationRequestId` FK.
- `consentType` enum: `ATTESTED_BY_VERIFIER`, `HOLDER_TOKEN`, `DOCUMENTED_CONSENT`.
- `holderFirstName`, `holderLastName`, `holderDateOfBirth` optional depending on type.
- `holderEmail` optional.
- `consentReference` optional external or internal token/reference.
- `scope` enum or list: `STATUS_ONLY`, `BASIC_DETAILS`, `FULL_DETAILS`.
- `grantedAt`, `expiresAt`.
- `validatedAt`.
- `status` enum: `PENDING`, `VALID`, `INVALID`, `EXPIRED`.
- `failureReason`.

### VerificationResult

Stores the result for each attempt.

Fields:

- `id` UUID primary key.
- `verificationRequestId` FK.
- `outcome` enum: `VERIFIED`, `NOT_FOUND`, `REVOKED`, `WITHDRAWN`, `DRAFT_NOT_VERIFIABLE`, `MISMATCH`, `CONSENT_REQUIRED`, `CONSENT_INVALID`, `INVALID_QR`, `ERROR`.
- `confidence` enum: `HIGH`, `MEDIUM`, `LOW`, `NONE`.
- `matchedQualificationId` nullable UUID.
- `matchedSecurityIdentifierHash` nullable.
- `qualificationStatus` nullable string from authoritative record.
- `matchScore` integer 0-100.
- `matchDetailsJson` JSON/text describing compared fields.
- `responseDisclosureScope` enum.
- `failureCode` and `failureMessage`.
- `verifiedAt`.

### VerificationEvidence

Optional normalized evidence submitted with a request.

Fields:

- `id` UUID primary key.
- `verificationRequestId` FK.
- `evidenceType` enum: `QR_PAYLOAD`, `STRUCTURED_DETAILS`, `CERTIFICATE_UPLOAD`.
- `qualificationNumber`, `studentNumber`, `holderFirstName`, `holderLastName`, `yearOfAward`, `qualificationName`, `institutionId`, `institutionName`.
- `rawPayloadHash`; never store full uploaded certificate text by default.
- `createdAt`.

## Data Flow

### QR Verification Flow

```text
Verifier -> POST /api/v1/verifications/qr
  -> Security filter validates JWT and `verification:write`
  -> QrPayloadParser validates AQVP QR format and extracts securityIdentifier
  -> VerificationRequestService creates request
  -> ConsentValidationService validates consent and decides disclosure scope
  -> QualificationLookupClient calls Qualification service internal snapshot endpoint
  -> VerificationMatchingService compares supplied evidence with authoritative snapshot
  -> VerificationResultService persists result and redacts response
  -> AuditEventPublisher emits verification event
  -> API returns result
```

### Request-Based Verification Flow

```text
POST /api/v1/verification-requests
  -> persist PENDING_CONSENT or READY request
POST /api/v1/verification-requests/{id}/consent-validation
  -> validate consent and move request to READY when valid
POST /api/v1/verification-requests/{id}/qr-verification
  -> process QR and evidence against authoritative snapshot
GET /api/v1/verification-requests/{id}
  -> return request and latest result
```

### Qualification Lookup Contract

```text
Verification service -> Qualification service
GET /api/v1/internal/qualifications/verification-snapshots/by-security-identifier/{securityIdentifier}
Authorization: Bearer <service-token or propagated JWT with qualification:verify>
```

The snapshot must be minimal and purpose-built for verification. It should not expose the full qualification entity.

## Request and Response Structures

### Create Verification Request

Endpoint:

```text
POST /api/v1/verification-requests
```

Required authority: `verification:write`.

Request:

```json
{
  "channel": "QR",
  "purpose": "EMPLOYMENT",
  "subject": {
    "holderFirstName": "Amina",
    "holderLastName": "Dube",
    "dateOfBirth": "2001-04-12",
    "studentNumber": "MSU123456"
  },
  "consent": {
    "consentType": "ATTESTED_BY_VERIFIER",
    "scope": "BASIC_DETAILS",
    "grantedAt": "2026-08-20T10:30:00Z",
    "expiresAt": "2026-09-19T10:30:00Z",
    "consentReference": "HR-FILE-2026-0091"
  },
  "evidence": {
    "qualificationNumber": "MSU-BSC-2024-0001",
    "qualificationName": "Bachelor of Science in Computer Science",
    "yearOfAward": 2024
  }
}
```

Response `201 Created`:

```json
{
  "id": "7f74bb0a-7e9a-4828-9487-b3caaeed7046",
  "requestReference": "VR-20260820-000001",
  "channel": "QR",
  "purpose": "EMPLOYMENT",
  "status": "READY",
  "consentStatus": "VALID",
  "expiresAt": "2026-08-27T10:30:00Z",
  "createdAt": "2026-08-20T10:30:00Z"
}
```

### Validate Consent

Endpoint:

```text
POST /api/v1/verification-requests/{id}/consent-validation
```

Required authority: `verification:write`.

Request:

```json
{
  "consentType": "ATTESTED_BY_VERIFIER",
  "scope": "BASIC_DETAILS",
  "holderFirstName": "Amina",
  "holderLastName": "Dube",
  "dateOfBirth": "2001-04-12",
  "grantedAt": "2026-08-20T10:30:00Z",
  "expiresAt": "2026-09-19T10:30:00Z",
  "consentReference": "HR-FILE-2026-0091"
}
```

Response `200 OK`:

```json
{
  "verificationRequestId": "7f74bb0a-7e9a-4828-9487-b3caaeed7046",
  "status": "VALID",
  "scope": "BASIC_DETAILS",
  "validatedAt": "2026-08-20T10:31:00Z",
  "failureReason": null
}
```

### QR Verification Shortcut

Endpoint:

```text
POST /api/v1/verifications/qr
```

Required authority: `verification:write`.

Request:

```json
{
  "qrPayload": "AQVP:v1:MSU:83a29aa2-8bf1-4f75-822f-f43c036fd2de",
  "purpose": "EMPLOYMENT",
  "consent": {
    "consentType": "ATTESTED_BY_VERIFIER",
    "scope": "BASIC_DETAILS",
    "grantedAt": "2026-08-20T10:30:00Z",
    "expiresAt": "2026-09-19T10:30:00Z",
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

Response `200 OK`:

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
    "issuedAt": "2026-08-18T14:30:00Z",
    "institutionName": "Midlands State University"
  },
  "holder": {
    "firstName": "Amina",
    "lastName": "Dube"
  },
  "matchDetails": [
    {
      "field": "holderName",
      "submitted": "Amina Dube",
      "matched": true
    },
    {
      "field": "yearOfAward",
      "submitted": "2024",
      "matched": true
    }
  ],
  "verifiedAt": "2026-08-20T10:31:12Z"
}
```

If consent is missing or invalid, return only non-sensitive fields:

```json
{
  "verificationRequestId": "7f74bb0a-7e9a-4828-9487-b3caaeed7046",
  "resultId": "d439a1c3-3eca-4db9-acb9-90e320c1bf62",
  "outcome": "CONSENT_REQUIRED",
  "confidence": "NONE",
  "matchScore": 0,
  "qualification": {
    "status": "RECORD_EXISTS"
  },
  "holder": null,
  "matchDetails": [],
  "verifiedAt": "2026-08-20T10:31:12Z"
}
```

### Request-Scoped QR Verification

Endpoint:

```text
POST /api/v1/verification-requests/{id}/qr-verification
```

Required authority: `verification:write`.

Request:

```json
{
  "qrPayload": "AQVP:v1:MSU:83a29aa2-8bf1-4f75-822f-f43c036fd2de",
  "evidence": {
    "qualificationName": "Bachelor of Science in Computer Science",
    "yearOfAward": 2024
  }
}
```

Response: same shape as `POST /api/v1/verifications/qr`.

### Get Verification Request

Endpoint:

```text
GET /api/v1/verification-requests/{id}
```

Required authority: `verification:read`.

Response:

```json
{
  "id": "7f74bb0a-7e9a-4828-9487-b3caaeed7046",
  "requestReference": "VR-20260820-000001",
  "channel": "QR",
  "purpose": "EMPLOYMENT",
  "status": "COMPLETED",
  "consentStatus": "VALID",
  "latestResult": {
    "id": "d439a1c3-3eca-4db9-acb9-90e320c1bf62",
    "outcome": "VERIFIED",
    "confidence": "HIGH",
    "matchScore": 100,
    "verifiedAt": "2026-08-20T10:31:12Z"
  },
  "createdAt": "2026-08-20T10:30:00Z",
  "completedAt": "2026-08-20T10:31:12Z"
}
```

### Internal Qualification Snapshot

Endpoint:

```text
GET /api/v1/internal/qualifications/verification-snapshots/by-security-identifier/{securityIdentifier}
```

Required authority: `qualification:verify` or `service:verification`.

Response:

```json
{
  "qualificationId": "2b253508-7be3-49c2-8a8e-b6ff2e66e184",
  "securityIdentifier": "83a29aa2-8bf1-4f75-822f-f43c036fd2de",
  "qualificationNumber": "MSU-BSC-2024-0001",
  "qualificationType": "DEGREE",
  "qualificationName": "Bachelor of Science in Computer Science",
  "classification": "First Class",
  "yearOfAward": 2024,
  "status": "ISSUED",
  "issuedAt": "2026-08-18T14:30:00Z",
  "revokedAt": null,
  "revocationReason": null,
  "student": {
    "studentId": "2bb5bf37-4d38-4e3f-a78a-e31e1af15384",
    "studentNumber": "MSU123456",
    "firstName": "Amina",
    "lastName": "Dube",
    "dateOfBirth": "2001-04-12"
  },
  "institution": {
    "institutionId": "0c9d50a6-16c6-4b94-9c31-18e61270e23b",
    "name": "Midlands State University",
    "code": "MSU"
  }
}
```

## Required Endpoints

### Verification Service

| Method | Path | Purpose | Authority |
|---|---|---|---|
| POST | `/api/v1/verification-requests` | Create request and optional consent/evidence. | `verification:write` |
| GET | `/api/v1/verification-requests` | List requests with filters. | `verification:read` |
| GET | `/api/v1/verification-requests/{id}` | Read request and latest result. | `verification:read` |
| POST | `/api/v1/verification-requests/{id}/consent-validation` | Validate or update consent. | `verification:write` |
| POST | `/api/v1/verification-requests/{id}/qr-verification` | Verify QR under an existing request. | `verification:write` |
| POST | `/api/v1/verifications/qr` | Create request and run QR verification in one call. | `verification:write` |
| GET | `/api/v1/verifications/{resultId}` | Read a persisted result. | `verification:read` |
| GET | `/actuator/health` | Health check. | Public |
| GET | `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` | API docs. | Public |

### Qualification Service

| Method | Path | Purpose | Authority |
|---|---|---|---|
| GET | `/api/v1/internal/qualifications/verification-snapshots/by-security-identifier/{securityIdentifier}` | Authoritative lookup by QR reference. | `qualification:verify` or `service:verification` |
| GET | `/api/v1/internal/qualifications/verification-snapshots/by-number/{qualificationNumber}` | Optional structured lookup fallback. | `qualification:verify` or `service:verification` |

### API Gateway

Current route already forwards:

```text
/verification/** -> http://localhost:8083/**
```

After implementation, calls through the gateway should use:

```text
POST http://localhost:8080/verification/api/v1/verifications/qr
```

## Validation Rules

### Request Validation

- `channel`, `purpose`, and consent scope must be valid enums.
- Request expiry defaults to seven days and must not exceed the configured max, e.g. 30 days.
- Authenticated users must have `verification:write` to create/process and `verification:read` to read.
- Request owner/organization scoping must be enforced once organization claims exist.
- Completed, cancelled, and expired requests cannot be processed again unless explicitly retried into a new result record.

### Consent Validation

- Consent is required for `BASIC_DETAILS` and `FULL_DETAILS`.
- `grantedAt` cannot be in the future.
- `expiresAt` must be after `grantedAt` and after current time.
- `consentReference` is required for `ATTESTED_BY_VERIFIER` and `DOCUMENTED_CONSENT`.
- `HOLDER_TOKEN` requires a valid token/reference once holder-facing consent exists.
- Holder identifiers in consent should be compared with authoritative holder fields when available.
- Invalid or expired consent must return `CONSENT_INVALID` or `CONSENT_REQUIRED` without sensitive details.

### QR Validation

- Payload must match an accepted AQVP QR format.
- `securityIdentifier` must be present, normalized, and within length limits.
- If the QR payload includes a signature, the signature must verify before lookup.
- A malformed QR returns `INVALID_QR` and should not call Qualification service.
- A valid QR with no authoritative record returns `NOT_FOUND`.
- A valid QR for a non-issued record returns `DRAFT_NOT_VERIFIABLE`, `REVOKED`, or `WITHDRAWN` according to status.

### Matching Rules

- Authoritative `status=ISSUED` or `AMENDED` is verifiable.
- `REVOKED` returns `REVOKED`, including revocation date/reason only if disclosure policy allows it.
- `DRAFT` is not verifiable.
- Exact comparisons: qualification number, year of award, student number, institution id/code.
- Case/space-insensitive comparisons: holder name, qualification name, institution name.
- Optional fields should not reduce confidence when absent from submitted evidence.
- Mismatched critical fields return `MISMATCH`.
- Match scoring: 100 exact match; 70-99 minor normalized differences; below 70 is mismatch or requires review.

### Persistence Rules

- Store raw QR payload only if necessary and encrypted; otherwise store a hash.
- Store `securityIdentifierHash`, not raw `securityIdentifier`, in Verification tables unless lookup replay requires the raw value.
- Never store full certificate uploads in the Verification service; store document metadata or hand off to Document service later.
- All state transitions must update timestamps and append a result/audit event.

## Error Handling

Follow the existing `ErrorResponse` pattern:

```json
{
  "timestamp": "2026-08-20T10:31:12Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/verifications/qr",
  "fieldErrors": {
    "qrPayload": "must be a valid AQVP QR payload"
  }
}
```

Recommended mappings:

| Condition | HTTP | Business outcome/code |
|---|---:|---|
| Bean validation failure | 400 | `VALIDATION_FAILED` |
| Invalid QR format | 400 | `INVALID_QR` |
| Missing authentication | 401 | `AUTHENTICATION_REQUIRED` |
| Missing permission | 403 | `ACCESS_DENIED` |
| Request id not found | 404 | `REQUEST_NOT_FOUND` |
| Qualification snapshot not found | 200 for verification result, or 404 for internal lookup | `NOT_FOUND` |
| Consent missing/expired/invalid | 200 for verification result, 400 for direct consent endpoint validation failure | `CONSENT_REQUIRED`, `CONSENT_INVALID`, `CONSENT_EXPIRED` |
| Duplicate request reference | 409 | `DUPLICATE_RESOURCE` |
| Request already completed/cancelled | 409 | `INVALID_STATE` |
| Qualification service unavailable | 503 | `UPSTREAM_UNAVAILABLE` |
| Timeout calling Qualification service | 504 | `UPSTREAM_TIMEOUT` |
| Unexpected error | 500 | `INTERNAL_ERROR` |

For verification endpoints, business negative outcomes such as `NOT_FOUND`, `MISMATCH`, or `REVOKED` should usually return `200 OK` with a persisted result. Transport or system failures should use non-2xx responses.

## Security and Permissions

Add Identity seed permissions:

- `verification:read`
- `verification:write`
- `verification:admin`
- `qualification:verify` or `service:verification`

Verification service security:

- Permit health and OpenAPI paths.
- Require `verification:read` for GET request/result endpoints.
- Require `verification:write` for request creation and verification actions.
- Require `verification:admin` for future administrative exports or overrides.
- Extract username and authorities from JWT using the same stateless filter pattern as Qualification.

Service-to-service security:

- Prefer a dedicated API client/service token from Identity for Verification to call Qualification.
- Do not expose internal qualification snapshot endpoints to normal frontend users unless explicitly authorized.
- Set short upstream timeouts and log correlation/request ids.

## Execution Order

### Phase 1: Setup and Contracts

1. Confirm local Java 21 and Maven build state with `mvn -B clean verify` or at least module-level tests.
2. Add verification permissions to Identity migrations and role seeds.
3. Add Verification service dependencies needed for security and mapping, mirroring Qualification as appropriate: Spring Security, jjwt libraries if not inherited, MapStruct, Lombok, Springdoc.
4. Define enums and DTO contracts for requests, consent, QR verification, results, errors, and qualification snapshots.
5. Add OpenAPI annotations and examples to planned controllers.

### Phase 2: Qualification Snapshot API

1. Add `QualificationVerificationSnapshotDto` in Qualification service.
2. Add service method using existing `findBySecurityIdentifier` and `findByQualificationNumber`.
3. Include student and institution details needed for matching; use existing repositories and entity relationships inside the same Qualification bounded context.
4. Add internal controller under `/api/v1/internal/qualifications/verification-snapshots`.
5. Add security matchers requiring `qualification:verify` or `service:verification`.
6. Add service and controller tests for found, not found, revoked, missing authority, and invalid token cases.

### Phase 3: Verification Persistence

1. Add Flyway migration `V1__init_verification_schema.sql`.
2. Create entities: `VerificationRequest`, `ConsentRecord`, `VerificationEvidence`, `VerificationResult`.
3. Add repositories and indexes for `requestReference`, requester fields, status, outcome, created time, and hashed security identifier.
4. Add domain enums and base entity pattern matching current modules.
5. Add mapper interfaces and DTO records.

### Phase 4: Core Verification Logic

1. Implement `QrPayloadParser` with tests for valid, malformed, unsupported version, missing identifier, and signature-invalid cases.
2. Implement `ConsentValidationService` with tests for valid, missing, expired, future grant date, invalid scope, and holder mismatch.
3. Implement `QualificationLookupClient` with configured base URL, authorization strategy, connect/read timeouts, and upstream exception mapping.
4. Implement `VerificationMatchingService` with deterministic field comparison and match scoring.
5. Implement `VerificationResultService` to persist outcomes and redact responses by consent scope.

### Phase 5: Verification APIs

1. Implement `VerificationRequestController`.
2. Implement `POST /api/v1/verification-requests`.
3. Implement `GET /api/v1/verification-requests` and `GET /api/v1/verification-requests/{id}`.
4. Implement `POST /api/v1/verification-requests/{id}/consent-validation`.
5. Implement `POST /api/v1/verification-requests/{id}/qr-verification`.
6. Implement shortcut `POST /api/v1/verifications/qr`.
7. Implement `GET /api/v1/verifications/{resultId}`.
8. Add `VerificationSecurityConfig`, JWT filter, and exception handler matching current module style.

### Phase 6: Audit and Operational Hooks

1. Add a simple `AuditEventPublisher` interface.
2. Publish/log events for request created, consent validated/failed, QR parsed/failed, result completed, upstream failure.
3. Add correlation id support in logs and response headers.
4. Add actuator health details for Qualification upstream reachability if practical.

### Phase 7: Frontend Follow-Up

1. Add `VITE_VERIFICATION_API_BASE_URL`, defaulting to `http://localhost:8083` until the gateway decision is settled.
2. Add `verificationApi` Axios client reusing the shared auth interceptor factory.
3. Replace the Verification placeholder with QR verification and request history screens.
4. Show outcome chips: Verified, Mismatch, Revoked, Not Found, Consent Required.
5. Hide actions by permissions using existing route/permission guards.

### Phase 8: Testing

1. Unit tests for parser, consent validator, matching service, result redaction, request state transitions, and upstream client error mapping.
2. Repository tests for migrations, indexes, relationships, and enum persistence.
3. Controller tests for every endpoint: success, validation failure, unauthorized, forbidden, not found, conflict, and upstream failures.
4. Integration tests for full QR flow:
   - valid QR + valid consent -> `VERIFIED`
   - valid QR + no consent -> `CONSENT_REQUIRED`
   - malformed QR -> `INVALID_QR`
   - unknown identifier -> `NOT_FOUND`
   - revoked qualification -> `REVOKED`
   - evidence mismatch -> `MISMATCH`
5. Security tests proving `verification:read` cannot write and `verification:write` cannot read where applicable.
6. Run:

```powershell
mvn test -pl aqvp-qualification-service,aqvp-verification-service -am
mvn -B clean verify
```

7. Frontend follow-up checks:

```powershell
cd frontend/aqvp-web
npm run build
npm run lint
```

## Acceptance Criteria

- Verification service has migrations, entities, repositories, DTOs, mappers, services, controllers, security config, exception handling, and OpenAPI docs.
- Qualification service exposes a secured internal verification snapshot API.
- QR verification never trusts the QR alone; it confirms against Qualification service.
- Consent status controls disclosure of holder and qualification details.
- Negative verification outcomes are persisted and returned consistently.
- Tests cover success, security, validation, consent, QR parsing, mismatch, revoked, not found, and upstream failure paths.
- Documentation updates include API reference, database guide, security guide, testing guide, memory changelog, and current state.

## Recommended First Implementation Slice

Build the smallest useful vertical slice:

1. Add Identity permissions.
2. Add Qualification internal snapshot by `securityIdentifier`.
3. Add Verification schema for request, consent, evidence, result.
4. Implement QR parser for `AQVP:v1:<issuerCode>:<securityIdentifier>`.
5. Implement `POST /api/v1/verifications/qr`.
6. Persist result and return redacted response based on consent.
7. Add tests for the five primary outcomes: `VERIFIED`, `CONSENT_REQUIRED`, `INVALID_QR`, `NOT_FOUND`, `REVOKED`.

