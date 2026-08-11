# AQVP Developer Onboarding Guide

Academic Qualification Verification Platform (AQVP)

Version: 1.0  
Last updated: 2026-07-29

---

## 1. Purpose

This guide helps every new developer get the AQVP project running locally, understand the branch workflow, and submit their first pull request. Follow the steps in order. If you get stuck, check the troubleshooting section or ask in `#aqvp-dev`.

---

## 2. Prerequisites

Before you start, install the following tools:

- **Git** — `https://git-scm.com/downloads`
- **Java 21** — JDK from Oracle, Eclipse Temurin, or Microsoft Build of OpenJDK
- **Maven 3.9+** — `https://maven.apache.org/download.cgi`
- **IntelliJ IDEA** (recommended) or **VS Code**
- **PostgreSQL 15+** (optional for local H2 development)
- **Docker Desktop** (optional, for Testcontainers and integration tests)

Verify installations from a terminal:

```powershell
git --version
java -version
mvn -version
```

You should see Java 21 and Maven 3.9 or newer.

---

## 3. Clone the Repository

Open a terminal and run:

```powershell
git clone https://github.com/Lojohnes/Qualification-Verification-System.git
```

Move into the project folder:

```powershell
cd Qualification-Verification-System
```

---

## 4. Switch to the Develop Branch

All active development happens on `develop`.

```powershell
git checkout develop
git pull origin develop
```

If `origin/develop` does not exist yet, pull from the default remote and check available branches with:

```powershell
git branch -a
```

---

## 5. Create a Feature Branch

Create your branch from the latest `develop`. Use the sprint story number and a short description.

```powershell
git checkout develop
git pull origin develop
git checkout -b feature/S2-001-institution-module
```

Examples:

- `feature/S2-003-qualification-issuance`
- `bugfix/S2-007-verification-consent`

---

## 6. Import the Project into Your IDE

### 6.1 IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Select **File > Open**.
3. Navigate to `C:\Users\<YourName>\Qualification-Verification-System` and open the root `pom.xml`.
4. Choose **Open as Project**.
5. Wait for Maven to import modules.
6. Set the Project SDK to Java 21:
   - **File > Project Structure > Project SDK > Add SDK > Download or select JDK 21**.
7. Enable annotation processing:
   - **Settings > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing**.

### 6.2 VS Code

1. Open VS Code.
2. Select **File > Open Folder** and choose `C:\Users\<YourName>\Qualification-Verification-System`.
3. Install the **Extension Pack for Java** and **Spring Boot Extension Pack**.
4. Wait for Maven to import the project.
5. Verify the Java version in **Settings > Java > JRE Home** points to JDK 21.

---

## 7. Configure Java 21

### Windows

1. Install JDK 21.
2. Set `JAVA_HOME`:

```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
```

3. Add `%JAVA_HOME%\bin` to your `Path` environment variable.
4. Restart your terminal and verify:

```powershell
java -version
```

### Maven Toolchains

If you use multiple JDKs, add this to `~/.m2/toolchains.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
    </provides>
    <configuration>
      <jdkHome>C:\Program Files\Java\jdk-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

---

## 8. Local Infrastructure Setup

AQVP uses Docker Compose to provide PostgreSQL, Redis, and Kafka for local development. The full infrastructure reference is in `docs/local-development-infrastructure.md`.

### 8.1 Copy the environment example

From the repository root:

```powershell
copy .env.example .env
```

On Linux/macOS:

```bash
cp .env.example .env
```

Edit `.env` and replace `JWT_SECRET=CHANGE_ME` with a long random string (at least 32 characters). Leave the other development defaults as they are for local work.

### 8.2 Start Docker Desktop

Make sure Docker Desktop (or Docker Engine on Linux) is running.

### 8.3 Start PostgreSQL, Redis, and Kafka

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env up -d
```

Wait until all containers report `healthy`:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env ps
```

### 8.4 Verify container health

PostgreSQL:

```powershell
docker exec -e PGPASSWORD=$env:DATABASE_PASSWORD aqvp-postgres psql -U aqvp -d aqvp_db -c "SELECT 1;"
```

Redis:

```powershell
docker exec -it aqvp-redis redis-cli ping
```

Kafka:

```powershell
docker exec -it aqvp-kafka kafka-broker-api-versions --bootstrap-server localhost:19092
```

### 8.5 Load environment variables

PowerShell does not read `.env` files automatically. Before starting a service, clear any stale shell variables and reload the file:

```powershell
foreach ($v in 'DATABASE_URL','DATABASE_USERNAME','DATABASE_PASSWORD','DATABASE_NAME','DATABASE_PORT','REDIS_HOST','REDIS_PORT','KAFKA_BOOTSTRAP_SERVERS','KAFKA_HOST','KAFKA_PORT','JWT_SECRET') {
  [System.Environment]::SetEnvironmentVariable($v, $null, 'Process')
}
Get-Content .env | ForEach-Object {
  if ($_ -match '^([^#][^=]*)=(.*)$') {
    [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
  }
}
```

On Linux/macOS:

```bash
set -a
source .env
set +a
```

### 8.6 Start the backend

With infrastructure running and environment variables loaded, start the Identity service:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=dev'
```

### 8.7 Start the frontend

```powershell
cd frontend/aqvp-web
npm install
npm run dev
```

The Vite dev server typically starts on `http://localhost:5173`.

### 8.8 Open Swagger

Once the Identity service is running:

```text
http://localhost:8081/swagger-ui.html
```

### 8.9 Log in with the demo account

Use the seeded local admin account:

```text
Username: admin
Password: Admin123!
```

### 8.10 Stop infrastructure

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env down
```

To erase all local data, add `-v`:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env down -v
```

### 8.11 Security rules

- **Never commit `.env` files.** They are ignored by `.gitignore`.
- **Never commit passwords, secrets, API keys, tokens, private keys, or certificates.** If you accidentally commit one, rotate it immediately.
- Use `git status` to confirm only `.env.example` is tracked, not `.env`.

---

## 9. Build the Project

From the repository root:

```powershell
mvn clean install -DskipTests
```

This builds the parent project, the shared kernel, and all modules.

---

## 10. Run the Application

### 10.1 Run with H2 (No PostgreSQL)

```powershell
mvn -f aqvp-identity-service/pom.xml -Plocal-h2 spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=local'
```

For future modules, replace `aqvp-identity-service` with the module you are working on.

### 10.2 Run with PostgreSQL

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=dev'
```

Make sure PostgreSQL is running and environment variables are set.

The application starts on port `8081` for the Identity service. Other modules use their own configured ports.

---

## 11. Access Swagger UI

Once the application is running, open a browser:

```text
http://localhost:8081/swagger-ui.html
```

The OpenAPI JSON document is available at:

```text
http://localhost:8081/v3/api-docs
```

Swagger endpoints are publicly accessible in the `local` profile. In production, access is controlled.

---

## 12. Run Tests

### Run all tests for a module

```powershell
mvn test -pl aqvp-identity-service -am
```

### Run tests with static analysis

```powershell
mvn verify -pl aqvp-identity-service -am -Pstatic-analysis
```

### Run a specific test class

```powershell
mvn test -pl aqvp-identity-service -Dtest=AuthControllerTest
```

### Expected result

```text
Tests run: 101, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

The one skipped test is the Testcontainers PostgreSQL integration test, which only runs when Docker is available.

---

## 13. Submit a Pull Request

When your feature is ready:

1. Make sure you are on your feature branch:

```powershell
git branch --show-current
```

2. Stage and commit your changes:

```powershell
git add .
git commit -m "feat(institution): add institution CRUD endpoints"
```

3. Push the branch to origin:

```powershell
git push origin feature/S2-001-institution-module
```

4. Open a pull request on GitHub.
5. Fill in the pull request template:
   - Story number
   - What changed
   - How to test
   - Database changes
   - Swagger/OpenAPI changes
   - Screenshots (if UI)
6. Request review from at least one teammate.
7. Ensure all GitHub Actions checks pass.
8. Merge after approval.

---

## 14. Daily Workflow Summary

```powershell
git checkout develop
git pull origin develop
git checkout -b feature/S2-XXX-description
# write code, tests, and docs
mvn test -pl aqvp-<module>-service -am
git add .
git commit -m "feat(module): description"
git push origin feature/S2-XXX-description
# create pull request
```

---

## 15. Important Reminders

- Do **not** modify the Identity module unless you have explicit approval from the Technical Lead.
- Do **not** duplicate authentication, authorization, user management, or JWT logic.
- Follow the package structure from the Identity module.
- Use Java records for DTOs.
- Use MapStruct for mapping.
- Use Lombok for boilerplate.
- Write tests for new code.
- Keep code coverage at 80% or higher.
- Update Swagger, Postman, and request/response samples when you change an API.

---

## 16. Troubleshooting

| Problem | Solution |
|---|---|
| `java: invalid source release 21` | Check that IntelliJ/VS Code and `JAVA_HOME` point to JDK 21. |
| Maven cannot find Java 21 | Verify `JAVA_HOME` and that `Path` includes `%JAVA_HOME%\bin`. |
| `Cannot load driver class: org.h2.Driver` | Add the `local-h2` Maven profile when running with the local profile. |
| `Connection refused` to PostgreSQL | Start PostgreSQL or use the `local-h2` profile. |
| Tests fail because Docker is unavailable | The Testcontainers integration test will skip; this is expected. |
| Swagger UI returns 401 | Ensure `/swagger-ui.html` and `/v3/api-docs` are in the security permit list. |
| Lombok getters/setters not generated | Enable annotation processing in your IDE. |
| MapStruct mapper not generated | Rebuild the module; verify `mapstruct-processor` is on the annotation processor path. |
| Import errors after pulling `develop` | Run `mvn clean install -DskipTests` from the root. |
| Git push rejected | Pull the latest `develop`, rebase your branch, and push again. |

---

## 17. Useful Resources

- `docs/local-development-infrastructure.md` — local Docker Compose infrastructure guide
- `docs/coding-standards.md` — coding and architectural standards
- `docs/sprints/sprint-2-plan.md` — current sprint plan
- `docs/module-development-guide.md` — module implementation handbook
- `docs/team-guidelines.md` — team working agreement

---

*Welcome to the AQVP team. Follow this guide, keep the Identity module as your reference, and ask questions in `#aqvp-dev`.*
