# Deployment Guide

Last updated: 2026-08-10

## Current Deployment State

The repository contains local Docker Compose infrastructure and a GitHub Actions CI workflow. Production deployment manifests and rollback automation are not present in the current source.

## Local Infrastructure

Start local services:

```powershell
docker compose up -d
```

Services:

- PostgreSQL 16 on port 5432.
- Keycloak 24 on port 8080.
- Zookeeper on port 2181.
- Kafka on port 9092.

Note: Keycloak and API Gateway both default to host port 8080, so do not run both on the same host port without changing one configuration.

## Build

Backend:

```powershell
mvn -B clean verify
```

Frontend:

```powershell
cd frontend/aqvp-web
npm install
npm run build
```

## Run Services Locally

Identity:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```

API Gateway:

```powershell
mvn -f aqvp-api-gateway/pom.xml spring-boot:run
```

Frontend:

```powershell
cd frontend/aqvp-web
npm run dev
```

## Configuration

Production values should come from environment variables or a secrets manager.

Important variables include:

- `JWT_SECRET`
- `IDENTITY_DB_URL`
- `IDENTITY_DB_USERNAME`
- `IDENTITY_DB_PASSWORD`
- `QUALIFICATION_DB_URL`
- `QUALIFICATION_DB_USERNAME`
- `QUALIFICATION_DB_PASSWORD`
- `VERIFICATION_DB_URL`
- `VERIFICATION_DB_USERNAME`
- `VERIFICATION_DB_PASSWORD`
- `ADMIN_DB_URL`
- `ADMIN_DB_USERNAME`
- `ADMIN_DB_PASSWORD`
- `VITE_API_BASE_URL`

## CI/CD

GitHub Actions runs on push and pull request to `main` and `develop`:

- Checkout.
- Set up JDK 21.
- Run `mvn -B -P static-analysis verify`.
- Upload JaCoCo reports.

## Production Requirements Before Release

- Restrict CORS origins.
- Provide strong secrets through managed configuration.
- Enable HTTPS/TLS.
- Define database backup and restore procedures.
- Run Testcontainers/PostgreSQL migration tests in CI.
- Add deployment manifests or scripts for target environment.
- Define rollback procedure per service and database migration.
- Confirm gateway route strategy and frontend base URL.

## Rollback

No production rollback procedure exists yet. Before production release, document:

- Versioned artifact/image strategy.
- Database migration rollback policy.
- Service health checks.
- Gateway traffic switch/rollback method.
- Smoke tests after rollback.

