# Development Guide

Last updated: 2026-08-10

## Required Tools

- Git.
- Java JDK. Current root build and CI use Java 21.
- Maven 3.9+.
- Node.js 20+ and npm 10+ for frontend.
- Docker Desktop for PostgreSQL/Kafka/Keycloak infrastructure and Testcontainers tests.
- IntelliJ IDEA or VS Code with Java/Spring support.

## Important Version Note

The root `pom.xml` targets Java 21 and CI installs JDK 21. `aqvp-identity-service/pom.xml` currently overrides `maven.compiler.release` to 17, and older onboarding text references Java 17. Resolve this before making broad build changes.

## Setup

```powershell
git clone https://github.com/Lojohnes/Qualification-Verification-System.git
cd Qualification-Verification-System
```

Start local infrastructure:

```powershell
docker compose up -d
```

Build all Maven modules:

```powershell
mvn -B clean verify
```

Run the Identity service:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```

Run Identity with H2 local profile:

```powershell
mvn -f aqvp-identity-service/pom.xml -Plocal-h2 spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=local"
```

Run the frontend:

```powershell
cd frontend/aqvp-web
npm install
npm run dev
```

## Project Structure

```text
aqvp-<module>-service/
  src/main/java/com/aqvp/platform/<module>/
    config/
    controller/
    domain/
    dto/
    exception/
    mapper/
    repository/
    security/
    service/
    validator/
  src/main/resources/
    application.yml
    application-dev.yml
    application-prod.yml
    application-test.yml
    db/migration/
```

Current Identity package base is `com.aqvp.platform.identity`. Some scaffolded modules currently use shorter application package names such as `com.aqvp.qualification`; align new implementation work with the established project standard after review.

## Coding Standards

- Use constructor injection.
- Use DTO records for API request/response payloads.
- Do not expose JPA entities from controllers.
- Keep business logic in services.
- Use MapStruct for mapping.
- Use Flyway for schema changes.
- Use UUID identifiers and audit fields.
- Keep logs free of secrets and unnecessary PII.
- Use Conventional Commits.

## Workflow

1. Read required memory docs.
2. Read relevant reference docs.
3. Create a feature branch from `develop`.
4. Implement within the assigned module boundary.
5. Add or update tests.
6. Run module tests and relevant full build checks.
7. Update documentation.
8. Open a PR with testing notes and migration/API changes.

