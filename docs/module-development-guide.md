# AQVP Module Development Guide

Academic Qualification Verification Platform (AQVP)

Version: 1.0  
Prepared by: Principal Software Architect, Enterprise Solution Architect, Scrum Master, and Technical Documentation Specialist  
Last updated: 2026-07-29

---

# 1. Introduction

This document is the official implementation handbook for every developer, QA engineer, DevOps engineer, and technical lead working on the Academic Qualification Verification Platform (AQVP).

Its purpose is to ensure that every module is built using a single, consistent architecture, coding style, and delivery process. The **Identity & Access module** is the reference implementation. It establishes the project structure, package layout, security model, testing approach, API conventions, and operational configuration. Every new module must extend the existing project without recreating or modifying shared concerns.

All developers must read this guide before writing code.

---

# 2. Current System Status

The following capabilities are already complete and in the `develop` branch:

| Capability | Status |
|---|---|
| Project Foundation | Complete |
| GitHub Repository | Complete |
| CI/CD Foundation | Complete |
| Identity & Access Module | Complete, tagged `v0.1.0-identity` |
| JWT Authentication | Operational |
| Swagger / OpenAPI | Operational |
| PostgreSQL Configuration | Operational |
| Flyway Configuration | Operational |
| Docker Configuration | Operational |
| Logging Configuration | Operational |

Future modules must extend the existing project. They must not recreate the project foundation, build configuration, authentication system, CI/CD pipeline, or Docker setup.

---

# 3. Development Rules

All developers must obey the following rules:

- Do **not** recreate the project.
- Do **not** recreate Maven configuration.
- Do **not** recreate Docker or Docker Compose configuration.
- Do **not** recreate GitHub Actions workflows.
- Do **not** recreate authentication or authorization.
- Do **not** modify completed modules such as Identity & Access unless the change is an approved bug fix.
- Work only within the assigned module and its designated package boundaries.
- Use the Identity module APIs for users, roles, permissions, and authentication.
- Follow the package structure, naming conventions, and coding standards defined in this guide.

---

# 4. Module Assignment Matrix

| Team Member | Role | Module Ownership | Responsibilities |
|---|---|---|---|
| **Lovemore Johannes** | Technical Lead / DevOps Lead | Frontend Foundation; Integration Layer | Project Integration, Architecture, Frontend Foundation, CI/CD, GitHub Administration, Code Reviews, Merge Requests, Release Management |
| **Tsakane Sithole** | Backend Developer | Institution Module | Institution, Faculty, Department, Program, Institution CRUD, Program CRUD |
| **Takunda Mazambani** | Backend Developer | Qualification Module | Student, Qualification, Academic Records, Qualification Issuance, Qualification Revocation, Qualification Amendment |
| **Wonder Mangwendeza** | Backend Developer | Verification Module | Verification Requests, Verification Engine, Consent Validation, Verification Results, QR Verification |
| **Memory Chikomo** | Backend Developer | Document Module | PDF Generation, Certificate Generation, QR Code Generation, Digital Signature, Document Storage |
| **Tariro Mutunami** | QA Engineer / DevOps Engineer | Audit & Notification Module | Audit Logs, Email Notifications, SMS Notifications, Automated Testing, Integration Testing, Performance Testing |

---

# 5. Development Specification

Each module must deliver the following artefacts. Use the Identity module as the working reference for every item.

## 5.1 Module Purpose

A one-paragraph description of what the module does and why it exists in the platform.

## 5.2 Business Scope

The bounded context of the module. List the domain concepts owned by the module and the boundaries with other modules.

## 5.3 Business Rules

Document the business rules that govern the module. These are the invariants that services must enforce.

Examples:

- A qualification can only be issued to an active student.
- A verification request must have consent from the student.
- An institution code must be unique within the platform.

## 5.4 Dependencies

List internal and external dependencies.

Internal:

- Identity module for users, roles, permissions, and JWT validation.
- Other modules only through public APIs, not direct database access.

External:

- PostgreSQL for persistence.
- Redis for caching where applicable.
- Kafka for event publishing where applicable.

## 5.5 Existing APIs Required

List the Identity module APIs that the module will consume. Examples:

- `POST /api/v1/auth/login` — obtain JWT.
- `GET /api/v1/auth/me` — get current user details.
- `GET /api/v1/users/{id}` — retrieve user information.
- `GET /api/v1/roles/{id}` — retrieve role details.

## 5.6 Database Tables

List the tables the module owns. Use the naming conventions defined in this guide. Example:

- `institutions`
- `programs`
- `departments`
- `faculties`

## 5.7 Entities

JPA entity classes in the `domain` package. Requirements:

- Use Lombok for boilerplate.
- Use UUID primary keys.
- Include audit fields (`created_at`, `updated_at`, `created_by`, `updated_by`, `version`).
- Define relationships and constraints.
- Do not expose entities in controllers.

## 5.8 DTOs

Java records in the `dto` package. Requirements:

- Request DTOs: `<Entity>RequestDto`, `<Entity>UpdateRequestDto`.
- Response DTO: `<Entity>ResponseDto`.
- Add validation annotations.
- Use only primitive types, strings, collections, or nested DTOs.

## 5.9 Repositories

Spring Data JPA interfaces in the `repository` package. Requirements:

- Extend `JpaRepository`.
- Use derived methods or `@Query`.
- Return `Optional<Entity>` for single lookups.

## 5.10 Services

Interfaces and implementations in the `service` package. Requirements:

- Interface named `<Entity>Service`.
- Implementation named `<Entity>ServiceImpl`.
- Use constructor injection and `@Transactional` on write methods.
- Return DTOs.
- Throw domain exceptions, not HTTP exceptions.

## 5.11 Controllers

REST controllers in the `controller` package. Requirements:

- Annotated with `@RestController`.
- Base path: `/api/v1/<module>`.
- Use constructor injection.
- Return `ResponseEntity`.
- Document every endpoint with OpenAPI annotations.

## 5.12 Validation Rules

Document the validation rules for the module. Implement them on DTOs with Jakarta Bean Validation and custom validators where needed.

## 5.13 Security Rules

- Endpoints require JWT authentication except those explicitly public.
- Use Identity permissions for authorization (e.g., `institution:read`, `qualification:write`).
- Do not create a separate authentication mechanism.

## 5.14 Exception Handling

Define custom runtime exceptions and map them to HTTP status codes in the global exception handler. Keep messages clear and avoid leaking internal details.

## 5.15 Swagger Requirements

Every endpoint must be documented with:

- `@Operation` summary and description
- `@ApiResponse` for success and error cases
- DTO examples where helpful

Maintain the module's Postman collection and request/response samples.

## 5.16 Flyway Migration Requirements

- Create migration scripts in `src/main/resources/db/migration/`.
- Name: `V<version>__<description>.sql`.
- Never modify a migration that has already been merged to `develop`.
- Include seed data scripts where appropriate.

## 5.17 Logging Requirements

Use SLF4J via Lombok `@Slf4j`. Log significant business events at `INFO`, errors at `ERROR`, and diagnostics at `DEBUG`. Never log secrets or PII.

## 5.18 Caching Requirements

Use Redis only where it provides clear benefit: frequently read, rarely changed reference data, or expensive computed results. Document cache keys and invalidation rules.

## 5.19 Kafka Events

Publish domain events for significant lifecycle changes. Example events:

- `QualificationIssuedEvent`
- `VerificationRequestedEvent`
- `DocumentGeneratedEvent`

Use a clear topic naming convention: `aqvp.<module>.<event>`.

## 5.20 Unit Tests Required

Every service, mapper, validator, and utility must have unit tests.

## 5.21 Integration Tests Required

- Repository tests with `@DataJpaTest`.
- Controller tests with `@WebMvcTest`.
- Security tests for protected endpoints.
- Module integration tests with `@SpringBootTest` where applicable.

## 5.22 Acceptance Criteria

List the observable outcomes that QA will validate. Example:

- Institutions can be created with a unique code.
- Duplicate institution codes return `409 Conflict`.
- Unauthorized requests return `401 Unauthorized`.

## 5.23 Definition of Done

A module is done when:

- All stories meet acceptance criteria.
- Unit and integration tests pass.
- Code coverage is at least 80%.
- Checkstyle and SpotBugs pass.
- API documentation is updated.
- Flyway migrations are included.
- PR is reviewed and merged.
- QA signs off.

## 5.24 Common Mistakes to Avoid

- Duplicating Identity module concerns (users, roles, JWT, permissions).
- Returning entities from controllers.
- Writing business logic in controllers or repositories.
- Skipping tests for "simple" code.
- Modifying shared parent POMs without approval.
- Hard-coding configuration values.
- Exposing stack traces in API responses.

---

# 6. Integration Requirements

## 6.1 Inter-Module Communication

Modules communicate through the following mechanisms, in order of preference:

1. REST API calls for synchronous operations.
2. Kafka events for asynchronous, decoupled workflows.
3. Shared database access is **not allowed** across module boundaries.

## 6.2 Using Identity APIs

All authentication and authorization flows go through the Identity module. Obtain a JWT from `/api/v1/auth/login` and include it in the `Authorization` header:

```text
Authorization: Bearer <jwt-token>
```

Validate role and permission requirements before exposing sensitive operations.

## 6.3 Role-Based Access Control

Permissions follow the pattern `<resource>:<action>`. Examples:

- `institution:read`
- `qualification:write`
- `verification:execute`

Controllers use Spring Security method-level or request-level authorization based on these permissions. Permissions are stored and managed in the Identity module.

## 6.4 Maintaining Module Boundaries

- Each module owns its database schema.
- Do not import repositories or entities from another module.
- Do not bypass the Identity module for authentication.
- Cross-cutting concerns such as audit and notifications are provided as shared services and must be consumed, not duplicated.

---

# 7. API Development Standards

## 7.1 REST Conventions

- Use nouns, not verbs, in paths: `/qualifications`, not `/getQualifications`.
- Use plural resource names.
- Use sub-resources for relationships: `/students/{id}/qualifications`.

## 7.2 HTTP Methods

| Method | Use |
|---|---|
| GET | Retrieve a resource or collection |
| POST | Create a resource |
| PUT | Full update of a resource |
| PATCH | Partial update of a resource |
| DELETE | Remove a resource |

## 7.3 Status Codes

| Status | Use |
|---|---|
| 200 OK | Successful read or update |
| 201 Created | Successful creation |
| 204 No Content | Successful deletion or update with no body |
| 202 Accepted | Async or non-blocking operation accepted |
| 400 Bad Request | Validation failure |
| 401 Unauthorized | Missing or invalid authentication |
| 403 Forbidden | Missing permission |
| 404 Not Found | Resource does not exist |
| 409 Conflict | Duplicate or conflicting state |
| 500 Internal Server Error | Unexpected server error |

## 7.4 DTO Usage

- Use records for all request/response payloads.
- Never expose entities directly.
- Map entities to DTOs using MapStruct.

## 7.5 Pagination, Filtering, and Sorting

- Pagination defaults: `page=0`, `size=20`.
- Use query parameters for filtering: `?status=ISSUED&institutionId=...`.
- Use `sort` parameter: `sort=createdAt,desc`.
- Response shape:

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

## 7.6 Validation

- Validate inputs on DTOs using Jakarta Bean Validation.
- Add custom validators for domain-specific rules.
- Return `400 Bad Request` with field-level error details.

## 7.7 OpenAPI Annotations

Use annotations such as:

- `@Operation`
- `@ApiResponse`
- `@Parameter`
- `@Schema`
- `@Tag`

Keep examples accurate and aligned with the DTOs.

---

# 8. Database Development Standards

## 8.1 Naming Conventions

- Tables: snake_case, plural where natural: `qualifications`, `verification_requests`.
- Columns: snake_case: `issued_date`, `created_at`.
- Primary keys: UUID.
- Constraints: descriptive names or let Flyway generate them.

## 8.2 Indexes

Add indexes for:

- Foreign keys.
- Frequently filtered columns.
- Searchable fields such as codes, names, and statuses.

Avoid unnecessary indexes on write-heavy tables.

## 8.3 Foreign Keys

- Define foreign keys at the database level through Flyway.
- JPA entities must declare relationships with `FetchType.LAZY` by default.
- Avoid cascading deletes unless explicitly required by business rules.

## 8.4 UUID Primary Keys

All entities use `UUID` primary keys generated by the application or database.

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

## 8.5 Audit Fields

Every entity must include:

- `created_at`
- `updated_at`
- `created_by`
- `updated_by`
- `version` for optimistic locking

## 8.6 Flyway Naming Conventions

```
V1__init_module_schema.sql
V2__seed_module_data.sql
V3__add_verification_index.sql
```

Use double underscore between version and description.

---

# 9. Testing Requirements

Every module must include:

- **JUnit 5** for unit and integration tests.
- **Mockito** for mocking dependencies.
- **Repository tests** with `@DataJpaTest`.
- **Controller tests** with `@WebMvcTest`.
- **Service tests** with Mockito.
- **Security tests** for protected endpoints.
- **Integration tests** for module-level workflows.

Minimum code coverage target: **80%**.

All tests must pass before a pull request can be merged.

---

# 10. Pull Request Requirements

Every pull request must include:

- Clear feature description.
- Issue or story number.
- Summary of database changes, including Flyway scripts.
- Summary of Swagger/OpenAPI changes.
- List of new or updated tests.
- Screenshots or UI notes if the change affects the frontend.
- A completed checklist confirming standards were followed.

---

# 11. Integration Checklist

Before a pull request can be merged, all of the following must be true:

- [ ] Project builds successfully.
- [ ] All tests pass.
- [ ] Swagger / OpenAPI documentation is updated.
- [ ] Flyway migration scripts are included.
- [ ] No duplicated code or duplicated Identity functionality.
- [ ] Static analysis passes (Checkstyle, SpotBugs, SonarQube where applicable).
- [ ] GitHub Actions passes.
- [ ] Documentation is updated.

---

# 12. Developer Workflow

1. Clone the repository.
2. Checkout `develop`.
3. Pull the latest changes.
4. Create a feature branch from `develop`.
5. Read this Module Development Guide.
6. Generate code within the assigned module.
7. Review your own code.
8. Run tests locally.
9. Commit using Conventional Commits.
10. Push the feature branch.
11. Create a pull request.
12. Participate in code review.
13. Wait for GitHub Actions to pass.
14. Merge into `develop` after approval.

---

# 13. Technical Lead Workflow

Lovemore Johannes performs the following workflow:

1. **Architecture Planning** — define module boundaries, integration points, and shared services.
2. **Sprint Planning** — assign modules, clarify acceptance criteria, and set sprint goals.
3. **Assign Modules** — confirm module ownership with each developer.
4. **Review Pull Requests** — review high-risk, architectural, security, and integration PRs.
5. **Resolve Merge Conflicts** — assist with complex merges and integration conflicts.
6. **Integrate Modules** — verify that modules work together and consume shared services correctly.
7. **Monitor CI/CD** — ensure pipelines are healthy and releases are automated.
8. **Approve Releases** — sign off on release candidates and production deployments.
9. **Deploy** — manage deployment to staging and production environments.

---

# 14. Appendix

## 14.1 Glossary

| Term | Definition |
|---|---|
| Module | A bounded context implemented as a Maven module in the AQVP monolith. |
| DTO | Data Transfer Object used for API request and response payloads. |
| RBAC | Role-Based Access Control enforced by the Identity module. |
| JWT | JSON Web Token used for stateless authentication. |
| Flyway | Database migration tool. |
| Kafka | Distributed event streaming platform for async communication. |
| Redis | In-memory data store used for caching. |

## 14.2 Abbreviations

| Abbreviation | Meaning |
|---|---|
| AQVP | Academic Qualification Verification Platform |
| CI/CD | Continuous Integration / Continuous Delivery |
| DDD | Domain-Driven Design |
| DTO | Data Transfer Object |
| JPA | Java Persistence API |
| JWT | JSON Web Token |
| RBAC | Role-Based Access Control |
| REST | Representational State Transfer |
| UUID | Universally Unique Identifier |

## 14.3 Folder Structure

```
aqvp-<module>-service/
├── src/main/java/com/aqvp/platform/<module>/
│   ├── config/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── exception/
│   ├── mapper/
│   ├── repository/
│   ├── security/
│   ├── service/
│   └── validator/
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── application-test.yml
│   ├── application-local.yml
│   └── db/migration/
├── src/test/java/com/aqvp/platform/<module>/
│   ├── controller/
│   ├── integration/
│   ├── mapper/
│   ├── repository/
│   ├── security/
│   ├── service/
│   └── validator/
└── src/test/resources/testing/
    ├── docker/
    ├── jwt/
    ├── openapi/
    ├── postman/
    ├── report/
    ├── requests-responses/
    └── sql/
```

## 14.4 Package Structure

```
com.aqvp.platform.<module>
├── config
├── controller
├── domain
├── dto
├── exception
├── mapper
├── repository
├── security
├── service
└── validator
```

## 14.5 Git Branch Naming

```
feature/S2-XXX-short-description
bugfix/S2-XXX-short-description
hotfix/short-description
release/vX.Y.Z
```

## 14.6 Conventional Commit Examples

```
feat(institution): add institution CRUD endpoints
fix(qualification): prevent duplicate qualification codes
docs(verification): update API request examples
test(document): add PDF generation service tests
refactor(audit): extract audit event builder
chore(notification): add Kafka topic configuration
```

## 14.7 Useful Commands

```bash
# Run tests for a module
mvn test -pl aqvp-<module>-service -am

# Run with static analysis
mvn verify -pl aqvp-<module>-service -am -Pstatic-analysis

# Start module locally with H2
mvn -f aqvp-<module>-service/pom.xml -Plocal-h2 spring-boot:run \
  '-Dspring-boot.run.arguments=--spring.profiles.active=local'

# Build Docker image from repo root
docker build -t aqvp/<module>:latest -f aqvp-<module>-service/Dockerfile .
```

## 14.8 Troubleshooting Tips

| Problem | Likely Cause | Solution |
|---|---|---|
| Cannot start module locally | PostgreSQL not running | Use the `local-h2` Maven profile and `application-local.yml` |
| 401 on Swagger UI | Missing permitAll for Swagger paths | Add `/swagger-ui.html` and `/v3/api-docs` to the security permit list |
| Flyway migration fails | Existing migration was modified | Never modify merged migrations; create a new migration |
| Duplicate code warning | Same logic exists in Identity or another module | Extract into shared kernel or reuse existing service |
| Tests fail after rebase | Outdated schema or DTOs | Clean and rebuild; verify Flyway scripts and DTO mappings |
| Kafka listener not firing | Wrong topic or group ID | Verify topic naming and consumer configuration |

---

*This document is the official development handbook for the AQVP project. All team members must follow it. Updates require approval from the Technical Lead and should be recorded in the project documentation.*
