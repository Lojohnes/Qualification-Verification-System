# Quick Reference

Last updated: 2026-08-10

## Common Commands

Build backend:

```powershell
mvn -B clean verify
```

Run Identity tests:

```powershell
mvn test -pl aqvp-identity-service -am
```

Run Identity static analysis:

```powershell
mvn verify -pl aqvp-identity-service -am -Pstatic-analysis
```

Run Identity with PostgreSQL/dev:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```

Run Identity with H2/local:

```powershell
mvn -f aqvp-identity-service/pom.xml -Plocal-h2 spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=local"
```

Start infrastructure:

```powershell
docker compose up -d
```

Frontend install and run:

```powershell
cd frontend/aqvp-web
npm install
npm run dev
```

Frontend build/lint:

```powershell
cd frontend/aqvp-web
npm run build
npm run lint
```

## Important URLs

- Identity service: `http://localhost:8081`
- Identity Swagger UI: `http://localhost:8081/swagger-ui.html`
- Identity OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- API Gateway: `http://localhost:8080`
- Frontend dev server: `http://localhost:3000`

## Important Files

- Root build: `pom.xml`
- CI: `.github/workflows/ci.yml`
- Local infrastructure: `docker-compose.yml`
- Identity migrations: `aqvp-identity-service/src/main/resources/db/migration`
- Identity security config: `aqvp-identity-service/src/main/java/com/aqvp/platform/identity/config/SecurityConfig.java`
- Frontend API constants: `frontend/aqvp-web/src/constants/api.ts`
- Frontend routes: `frontend/aqvp-web/src/constants/routes.ts`
- Memory docs: `docs/memory`
- Reference docs: `docs/reference`

## Seed Login

Local/dev seeded admin:

- Username: `admin`
- Password: `Admin123!`

Treat seed credentials as local/dev only.

