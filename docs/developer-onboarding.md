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
- **Java 17** — JDK from Oracle, Eclipse Temurin, or Microsoft Build of OpenJDK
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

You should see Java 17 and Maven 3.9 or newer.

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
6. Set the Project SDK to Java 17:
   - **File > Project Structure > Project SDK > Add SDK > Download or select JDK 17**.
7. Enable annotation processing:
   - **Settings > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing**.

### 6.2 VS Code

1. Open VS Code.
2. Select **File > Open Folder** and choose `C:\Users\<YourName>\Qualification-Verification-System`.
3. Install the **Extension Pack for Java** and **Spring Boot Extension Pack**.
4. Wait for Maven to import the project.
5. Verify the Java version in **Settings > Java > JRE Home** points to JDK 17.

---

## 7. Configure Java 17

### Windows

1. Install JDK 17.
2. Set `JAVA_HOME`:

```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot", "User")
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
      <version>17</version>
    </provides>
    <configuration>
      <jdkHome>C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

---

## 8. Environment Configuration

The project uses `.env.example` files as safe templates. These files contain only example values and can be committed to Git. Real credentials and secrets must stay in local `.env` files, which are ignored by Git.

### 8.1 Copy the example files

**Frontend:**

```powershell
copy frontend\aqvp-web\.env.example frontend\aqvp-web\.env
```

**Backend:**

```powershell
copy backend\.env.example backend\.env
```

On Linux/macOS use `cp` instead of `copy`:

```bash
cp frontend/aqvp-web/.env.example frontend/aqvp-web/.env
cp backend/.env.example backend/.env
```

### 8.2 Replace CHANGE_ME placeholders

Open each `.env` file and replace every `CHANGE_ME` value with a local development value. At minimum:

- `VITE_API_BASE_URL` — the backend API base URL (default `http://localhost:8081` is usually fine)
- `JWT_SECRET` — a long random string, at least 32 characters
- `IDENTITY_DB_PASSWORD`, `ADMIN_DB_PASSWORD`, `QUALIFICATION_DB_PASSWORD`, `VERIFICATION_DB_PASSWORD`

### 8.3 Load backend environment variables

Spring Boot does not read `.env` files automatically. Load them with your shell before starting services.

**Windows PowerShell:**

```powershell
Get-Content backend/.env | ForEach-Object {
  if ($_ -match '^([^#][^=]*)=(.*)$') {
    [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
  }
}
```

**Generic terminal (bash/zsh with dotenv support):**

```bash
set -a
source backend/.env
set +a
```

### 8.4 Option A: Use the Local H2 Profile (Recommended for Quick Start)

No PostgreSQL installation is required. The `local-h2` Maven profile and `application-local.yml` start the Identity service with an in-memory H2 database.

```powershell
mvn -f aqvp-identity-service/pom.xml -Plocal-h2 spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=local'
```

### 8.5 Option B: Use PostgreSQL

1. Install PostgreSQL or use Docker:

```powershell
docker run --name aqvp-postgres -e POSTGRES_USER=aqvp -e POSTGRES_PASSWORD=aqvp -e POSTGRES_DB=identity_db -p 5432:5432 -d postgres:15
```

2. Create databases for each module as needed (`identity_db`, `admin_db`, `qualification_db`, `verification_db`).
3. Load the backend `.env` file (see 8.3).
4. Start a service with the `dev` profile:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=dev'
```

### 8.6 Security rules

- **Never commit `.env` files.** They are ignored by `.gitignore`.
- **Never commit passwords, secrets, API keys, tokens, private keys, or certificates.** If you accidentally commit one, rotate it immediately.
- Use `git status` to confirm only `.env.example` files are staged, not `.env` files.

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

- `docs/coding-standards.md` — coding and architectural standards
- `docs/sprints/sprint-2-plan.md` — current sprint plan
- `docs/module-development-guide.md` — module implementation handbook
- `docs/team-guidelines.md` — team working agreement

---

*Welcome to the AQVP team. Follow this guide, keep the Identity module as your reference, and ask questions in `#aqvp-dev`.*
