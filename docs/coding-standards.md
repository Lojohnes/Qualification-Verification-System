# AQVP Coding Standards

Academic Qualification Verification Platform (AQVP)

Version: 1.0  
Prepared by: Principal Software Architect  
Last updated: 2026-07-29

---

## 1. Purpose

This document defines the coding and architectural standards for the AQVP platform. The Identity & Access module is complete and is the **reference implementation** for all future modules. Every new module must follow the same architecture, package layout, naming conventions, and coding style used in the Identity module.

The Identity module must not be modified except for approved bug fixes. Its architecture and package structure must not be changed.

---

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| Migrations | Flyway |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| Containerisation | Docker |
| CI/CD | GitHub Actions |
| Messaging | Kafka |
| Caching | Redis |
| API Documentation | OpenAPI / Swagger |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers |

---

## 3. Java Standards

- Target Java 21 language level.
- Use modern Java features where they improve clarity: `var`, records, `Optional`, sealed types where appropriate.
- Prefer immutability. Use `final` fields and constructor injection.
- Avoid `null` in public APIs; return `Optional` or throw domain exceptions.
- Keep classes focused and small. A class should have a single reason to change.
- Do not use checked exceptions for application errors.
- Avoid raw types and unchecked casts.
- Favour composition over inheritance.

---

## 4. Naming Conventions

### 4.1 General

| Element | Convention | Example |
|---|---|---|
| Packages | lowercase, no underscores | `com.aqvp.platform.qualification` |
| Classes | PascalCase | `QualificationServiceImpl` |
| Interfaces | PascalCase, adjective/noun | `QualificationService`, `Validator` |
| Methods | camelCase | `findById`, `issueQualification` |
| Variables | camelCase | `qualificationId` |
| Constants | UPPER_SNAKE_CASE | `MAX_UPLOAD_SIZE` |
| DTO records | descriptive suffix | `QualificationRequestDto`, `QualificationResponseDto` |
| Test classes | `Test` suffix | `QualificationServiceTest` |

### 4.2 Module Base Package

Each module uses the base package:

```
com.aqvp.platform.<module-name>
```

Examples:

- `com.aqvp.platform.identity`
- `com.aqvp.platform.institution`
- `com.aqvp.platform.qualification`

### 4.3 Database Objects

- Tables: snake_case, plural where appropriate: `qualifications`, `verification_requests`
- Columns: snake_case: `created_at`, `issued_date`
- Sequences and constraints follow Flyway naming standards.

---

## 5. Package Structure

Every module must use the same package structure as the Identity module:

```
com.aqvp.platform.<module>
├── config              # Spring configuration, beans, security, caches
├── controller          # REST controllers
├── domain              # JPA entities and value objects
├── dto                 # Request/response records
├── exception           # Custom exceptions and exception handlers
├── mapper              # MapStruct mappers
├── repository          # Spring Data JPA repositories
├── security            # Security filters, handlers, principal (only if required)
├── service             # Service interfaces and implementations
└── validator           # Custom validators and constraints
```

Do not introduce alternative package layouts such as hexagonal, CQRS, or DDD tactical folders unless approved by the Principal Architect.

---

## 6. Folder Structure

Within a module, resources and tests follow this layout:

```
src/main/java/com/aqvp/platform/<module>/
src/main/resources/
  ├── application.yml
  ├── application-dev.yml
  ├── application-prod.yml
  ├── application-test.yml
  ├── application-local.yml (optional, for local H2/demo)
  └── db/migration/
       ├── V1__init_<module>_schema.sql
       ├── V2__seed_<module>_data.sql
       └── ...
src/test/java/com/aqvp/platform/<module>/
  ├── controller
  ├── integration
  ├── mapper
  ├── repository
  ├── security
  ├── service
  └── validator
src/test/resources/
  ├── application-test.yml
  └── testing/
       ├── docker/
       ├── open-api/
       ├── postman/
       ├── report/
       ├── requests-responses/
       ├── sql/
       └── ...
```

---

## 7. Layer Responsibilities

### 7.1 Domain Layer (`domain`)

- JPA entities with Lombok annotations.
- No business logic beyond basic field validation or lifecycle helpers.
- Entities must not expose database internals outside the module.

### 7.2 Repository Layer (`repository`)

- Spring Data JPA interfaces only.
- Keep queries simple; use derived query methods or `@Query` for custom SQL.
- No service logic in repositories.

### 7.3 Service Layer (`service`)

- Interfaces define contracts; implementations carry the suffix `Impl`.
- Transaction boundaries belong here.
- Validate inputs, orchestrate repositories, and return DTOs.
- Use constructor injection only.

### 7.4 Controller Layer (`controller`)

- REST endpoints with OpenAPI annotations.
- Handle HTTP concerns only; delegate to services.
- Return DTOs and appropriate HTTP status codes.
- Do not expose entities directly.

### 7.5 DTO Layer (`dto`)

- Use Java records for request/response objects.
- Validation annotations belong on DTO records.
- Naming convention: `<Entity>RequestDto`, `<Entity>ResponseDto`, `<Entity>UpdateRequestDto`.

### 7.6 Mapper Layer (`mapper`)

- MapStruct interfaces with `defaultComponentModel = spring`.
- Map entities to DTOs and back.
- Use `@Mapping` to ignore internal or sensitive fields.
- Test mappers with unit tests.

### 7.7 Exception Layer (`exception`)

- Custom runtime exceptions per domain.
- Global exception handler maps exceptions to HTTP status codes consistently.

### 7.8 Config Layer (`config`)

- Spring beans, security configuration, Kafka/Rabbit/Redis configuration.
- Keep configuration classes separate from business logic.

---

## 8. DTO Rules

- Use records, not classes, for all request/response DTOs.
- Place Jakarta Bean Validation annotations on record components.
- Never include entities or JPA-managed types in a DTO.
- Use nested DTOs for relationships instead of entity references.
- Example:

```java
public record InstitutionRequestDto(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 20) String code,
    @Size(max = 500) String description
) {}
```

---

## 9. Repository Rules

- Extend `JpaRepository` or `CrudRepository`.
- Use derived query methods for simple lookups.
- Use `@EntityGraph` or `@Query` to avoid N+1 problems.
- Do not expose raw `List` of entities to controllers; always map in the service layer.
- Methods should return `Optional<Entity>` for single-result lookups.

---

## 10. Service Rules

- Declare service interfaces and implement them with `Impl` suffix.
- Annotate implementation classes with `@Service` and `@RequiredArgsConstructor`.
- Use `@Transactional` for write operations.
- Throw domain exceptions, not HTTP-specific exceptions.
- Map entities to DTOs before returning.
- Keep methods cohesive and testable.
- Log meaningful events at `INFO` level and errors at `ERROR` level.

---

## 11. Controller Rules

- Annotate classes with `@RestController` and `@RequestMapping("/api/v1/<module>s")`.
- Use constructor injection.
- Return `ResponseEntity` for full control over status and headers.
- Use consistent HTTP status codes:
  - `200 OK` for successful reads
  - `201 Created` for successful creation
  - `204 No Content` for successful deletion or updates with no body
  - `202 Accepted` for async or non-blocking operations such as password reset
  - `400 Bad Request` for validation failures
  - `401 Unauthorized` for missing/invalid authentication
  - `403 Forbidden` for missing permissions
  - `404 Not Found` for missing resources
  - `409 Conflict` for duplicate or conflicting state
- Document endpoints with OpenAPI annotations.

---

## 12. Logging

- Use SLF4J via Lombok `@Slf4j`.
- Log at appropriate levels:
  - `ERROR`: failures that need attention
  - `WARN`: recoverable or unusual conditions
  - `INFO`: significant business events (creation, update, deletion, issuance)
  - `DEBUG`: diagnostic information
- Include structured data where helpful: identifiers, usernames, action names.
- Never log passwords, tokens, secrets, or PII in plain text.

Example:

```java
log.info("Issued qualification id={} for student id={}", qualificationId, studentId);
```

---

## 13. Validation

- Apply validation constraints on DTO records.
- Use custom validators for domain-specific rules (e.g., strong passwords, unique codes).
- Custom validators must implement `ConstraintValidator` and be annotated with `@Constraint`.
- Service layer may perform additional business-rule validation, but input validation belongs on DTOs.

---

## 14. Exception Handling

- Define domain-specific runtime exceptions in the `exception` package.
- The global exception handler maps each exception to a clear HTTP status.
- Use `ErrorResponse` or similar standard body for API errors.
- Do not leak stack traces or internal details in production responses.
- Keep status codes consistent with the Identity module.

---

## 15. Testing Standards

### 15.1 Unit Tests

- Naming: `<ClassUnderTest>Test`.
- Use JUnit 5, Mockito, and AssertJ.
- Test one concept per test method.
- Use `@ExtendWith(MockitoExtension.class)` for Mockito tests.
- Verify interactions with mocks only when behaviour depends on them.

### 15.2 Controller Tests

- Use `@WebMvcTest` with mocked services and repositories.
- Test happy path, validation errors, and security/authorization outcomes.
- Use `MockMvc` and `ObjectMapper`.

### 15.3 Repository Tests

- Use `@DataJpaTest` with the H2 test profile.
- Test save, find, custom queries, and existence checks.

### 15.4 Integration Tests

- Use `@SpringBootTest` for module-level integration.
- Use Testcontainers for PostgreSQL/Flyway integration tests where Docker is available.
- Gate Testcontainers tests with a Docker availability condition.

### 15.5 Coverage

- Minimum target: 80% line coverage per module.
- Security, authentication, and financial/document paths must have full coverage.

---

## 16. API Standards

- Base path: `/api/v1/<module>`.
- Use nouns, not verbs, in resource paths.
- Use plural resource names: `/institutions`, `/qualifications`.
- Action semantics via HTTP methods:
  - `GET` — read
  - `POST` — create
  - `PUT` / `PATCH` — update
  - `DELETE` — remove
- Use query parameters for filtering, sorting, and pagination.
- Pagination defaults: `page=0`, `size=20`, `sort=id,asc`.
- Use consistent field names in JSON responses (camelCase).
- Version breaking changes in path (`/api/v2/...`) only after platform-wide agreement.

---

## 17. Swagger / OpenAPI Standards

- Document every public endpoint with `@Operation` and `@ApiResponse`.
- Provide example values for request/response DTOs.
- Group endpoints by module tag.
- Keep the Swagger UI accessible in local/profile-permitted environments.
- Maintain a Postman collection and request/response samples under `src/test/resources/testing/`.
- OpenAPI JSON path: `/v3/api-docs`
- Swagger UI path: `/swagger-ui.html`

---

## 18. Flyway Standards

- Place migration scripts in `src/main/resources/db/migration/`.
- Naming convention:

```
V<version>__<description>.sql
```

Examples:

- `V1__init_institution_schema.sql`
- `V2__seed_institution_data.sql`
- `V3__add_verification_index.sql`

- Use `baseline-on-migrate: true`.
- Never modify an existing migration that has been merged to `develop`.
- New environments get the latest version; existing environments get incremental migrations.
- Seed data should be idempotent where possible.

---

## 19. Docker Standards

- Each service has a `Dockerfile` at module root.
- Build context is the repository root for multi-module Maven projects.
- Docker Compose files belong under `src/test/resources/testing/docker/` for integration testing.
- Use official base images (e.g., `eclipse-temurin:21-jre-alpine`).
- Run the application as a non-root user where practical.
- Include health checks in production Dockerfiles.
- Docker images are tagged with semantic version and commit SHA.

---

## 20. Security Standards

- All endpoints except explicitly public ones require authentication.
- Use JWT-based authentication through the Identity module.
- Authorise using Identity permissions (e.g., `institution:read`, `qualification:write`).
- Never store secrets in source control; use environment variables or external secret managers.
- Hash passwords with BCrypt; never log credentials.
- Validate and sanitise all inputs.
- Use HTTPS in production; never transmit tokens over plain HTTP.
- Apply the principle of least privilege to roles and permissions.

---

## 21. Git Commit Standards

### 21.1 Conventional Commits

Every commit message follows the Conventional Commits format:

```
<type>(<scope>): <short description>

<body>

<footer>
```

### 21.2 Types

| Type | Use When |
|---|---|
| `feat` | New feature or capability |
| `fix` | Bug fix |
| `docs` | Documentation changes only |
| `style` | Formatting, no functional change |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `test` | Adding or correcting tests |
| `chore` | Build, dependency, or tooling changes |
| `ci` | CI/CD configuration changes |

### 21.3 Scopes

Use module or layer names:

- `institution`, `qualification`, `verification`, `document`, `audit`, `notification`, `identity`
- `security`, `controller`, `service`, `repository`, `mapper`, `dto`, `config`

Examples:

```
feat(institution): add institution CRUD endpoints
fix(qualification): correct status transition validation
test(document): add PDF generation unit tests
docs(audit): update API examples in OpenAPI
```

### 21.4 Body and Footer

- Body explains what changed and why.
- Footer references issue IDs and breaking changes.

---

## 22. Code Review Checklist

Reviewers must verify that the code:

- Follows this coding standards document.
- Mirrors the Identity module architecture and package structure.
- Has no duplicated Identity functionality.
- Uses DTOs, not entities, in controllers.
- Includes unit and integration tests.
- Passes static analysis (Checkstyle, SpotBugs).
- Has clear, meaningful commit messages.
- Does not contain secrets or hard-coded credentials.
- Handles errors and edge cases appropriately.
- Uses constructor injection and immutable DTOs.
- Documents public APIs with OpenAPI annotations.

---

## 23. Pull Request Checklist

Before requesting review, the author confirms:

- Branch is up to date with `develop`.
- All tests pass locally.
- Checkstyle and SpotBugs pass.
- README, Swagger, Postman, and request/response samples are updated if needed.
- PR title follows `[Sprint-XXX] Short description`.
- PR description links the story and explains how to test.
- Acceptance criteria are met.
- No Identity module code was modified.

---

## 24. Performance Guidelines

- Use pagination for all list endpoints.
- Avoid N+1 queries through entity graphs, fetch joins, or DTO projections.
- Cache frequently read, rarely changed data using Redis where appropriate.
- Use asynchronous processing (Kafka) for heavy or non-blocking tasks such as PDF generation and bulk notifications.
- Optimistic locking with JPA `@Version` for concurrent updates.
- Keep API response payloads small; avoid returning unnecessary nested data.
- Profile slow queries and add indexes where justified.

---

## 25. Documentation Standards

- Every module has a README with build, run, and test instructions.
- OpenAPI annotations provide living API documentation.
- Postman collections and sample requests/responses are kept under `src/test/resources/testing/`.
- ADRs are added to `docs/adr/` for significant architectural decisions.
- Keep this coding standards document updated when practices change.

---

## 26. Identity Module Reference

The Identity module is the authoritative reference for:

- Package structure and layer responsibilities
- DTO, mapper, service, controller, and repository patterns
- JWT and permission-based security configuration
- Exception handling and HTTP status conventions
- OpenAPI/Swagger annotations and Postman collections
- Testing structure and static analysis setup
- Flyway migration conventions
- Docker and Maven module layout

All future modules must follow this reference. Any deviation requires explicit approval from the Principal Software Architect.

---

*These standards are a living document. Propose changes via pull request or architecture decision record.*
