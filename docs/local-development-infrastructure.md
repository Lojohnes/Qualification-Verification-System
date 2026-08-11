# AQVP Local Development Infrastructure

This guide describes the shared local infrastructure for the Academic Qualification Verification Platform (AQVP). It provides PostgreSQL, Redis, and Apache Kafka for all backend modules during local development.

## Purpose

Every developer runs the same lightweight infrastructure stack on their machine using Docker Compose. The existing Identity & Access module and future modules connect to this stack.

## What is provided

| Service | Container name | Host port | Purpose |
|---------|----------------|-----------|---------|
| PostgreSQL | `aqvp-postgres` | `5433` | Primary relational database |
| Redis | `aqvp-redis` | `6379` | Caching layer |
| Apache Kafka | `aqvp-kafka` | `9092` | Asynchronous event backbone |

All services share a single Docker bridge network: `aqvp-network`.

## Required software

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows) or Docker Engine + Compose plugin (Linux)
- A terminal with `docker compose` support
- Java 21 and Maven 3.9+ for running Spring Boot services

## Environment variables

Copy the root example file before starting:

```powershell
copy .env.example .env
```

Edit `.env` and replace `JWT_SECRET` with a long random value (at least 32 characters). The default development values for PostgreSQL, Redis, and Kafka are safe for local use only.

| Variable | Default | Used by |
|----------|---------|---------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5433/aqvp_db` | All services |
| `DATABASE_USERNAME` | `aqvp` | PostgreSQL / services |
| `DATABASE_PASSWORD` | `aqvp` | PostgreSQL / services |
| `DATABASE_NAME` | `aqvp_db` | PostgreSQL container |
| `DATABASE_PORT` | `5433` | PostgreSQL port mapping |
| `REDIS_HOST` | `localhost` | Services |
| `REDIS_PORT` | `6379` | Redis port mapping |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Services |
| `KAFKA_HOST` | `localhost` | Advertised listener host |
| `KAFKA_PORT` | `9092` | Kafka port mapping |
| `JWT_SECRET` | `CHANGE_ME` | Identity service |

Per-service overrides are optional. If omitted, services fall back to the generic `DATABASE_*` values.

## Docker Compose configuration

The compose file is located at:

```text
docker/docker-compose.dev.yml
```

It defines:

- A single PostgreSQL database named `aqvp_db`
- Redis with append-only file persistence
- A single-node KRaft Kafka broker (no Zookeeper)
- Named volumes for persistence
- Health checks for every service
- The `aqvp-network` bridge network

## Commands

### Start infrastructure

From the repository root:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env up -d
```

Wait for all services to become healthy:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env ps
```

### Stop infrastructure

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env down
```

### View logs

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env logs -f
```

### Reset development data

This removes containers **and** volumes, erasing all local data:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env down -v
```

## Verify PostgreSQL

```powershell
docker exec -it aqvp-postgres psql -U aqvp -d aqvp_db -c "SELECT 1;"
```

Expected output includes a row with `1`.

## Verify Redis

```powershell
docker exec -it aqvp-redis redis-cli ping
```

Expected output:

```text
PONG
```

## Verify Kafka

```powershell
docker exec -it aqvp-kafka kafka-broker-api-versions --bootstrap-server localhost:19092
```

A list of broker API versions indicates the broker is healthy.

## Spring Boot configuration

Each service reads the infrastructure settings from environment variables. The relevant defaults are in each module's `src/main/resources/application.yml`:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

The `application-dev.yml` files use the shared database URL with optional per-service overrides:

```yaml
spring:
  datasource:
    url: ${IDENTITY_DB_URL:${DATABASE_URL:jdbc:postgresql://localhost:5432/aqvp_db}}
    username: ${IDENTITY_DB_USERNAME:${DATABASE_USERNAME:aqvp}}
    password: ${IDENTITY_DB_PASSWORD:${DATABASE_PASSWORD:aqvp}}
```

### Loading environment variables on Windows

PowerShell does not read `.env` files automatically. Before running a service, clear any stale shell variables and reload the file:

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

Then start the service:

```powershell
mvn -f aqvp-identity-service/pom.xml spring-boot:run `
  '-Dspring-boot.run.arguments=--spring.profiles.active=dev'
```

## Flyway behavior

The Identity service runs Flyway migrations automatically when started with the `dev` profile and a PostgreSQL database. Migration scripts are located in:

```text
aqvp-identity-service/src/main/resources/db/migration
```

Other modules will add their own migration scripts when their bounded contexts are implemented. All modules share the same local `aqvp_db` database, so coordinate migration version numbers with the team.

## Health checks

Each container has a Docker Compose health check:

- PostgreSQL: `pg_isready`
- Redis: `redis-cli ping`
- Kafka: `kafka-broker-api-versions`

Spring Boot Actuator is enabled in all services. Once a service is running, check:

```text
http://localhost:<service-port>/actuator/health
```

Service ports:

- Identity: `8081`
- Qualification: `8082`
- Verification: `8083`
- Admin: `8084`
- API Gateway: `8080`

## Database development model

All developers share the same AQVP database architecture locally. Each developer runs their own `aqvp_db` inside their own Docker container.

Modules contribute tables, indexes, and migrations to the same shared database. Each module owner is responsible for their schema additions and migration version numbers. Do not modify another developer's schema without coordination.

Database ownership:

- **Lovemore Johannes** — infrastructure, Docker Compose, environment configuration, integration, database governance
- **Tsakane Sithole** — Institution/Program schema and migrations
- **Takunda Mazambani** — Student/Qualification schema and migrations
- **Wonder Mangwendeza** — Verification schema and migrations
- **Memory Chikomo** — Document schema and migrations
- **Tariro Mutunami** — Audit/Notification schema and migration testing

## Redis development rule

Redis is infrastructure for caching only. PostgreSQL remains the system of record. Do not use Redis as the primary database. Verification-related caching will be implemented when the Verification module is developed.

## Kafka development rule

Kafka is the asynchronous event backbone. The infrastructure is ready for future events such as:

- `QualificationIssuedEvent`
- `QualificationRevokedEvent`
- `VerificationCompletedEvent`

Do not implement those business events in this task. Only the local broker and configuration are established now.

## Development seed data

The Identity service seeds an admin user, roles, and permissions when started with the `local` profile. This is development-only data and is never used in production.

Default admin credentials for local testing:

```text
Username: admin
Password: Admin123!
```

These values are hardcoded only for local development and are not committed as production secrets.

## Troubleshooting

### Docker is not installed or not running

Install Docker Desktop and ensure it is running before executing any `docker compose` command.

### Port already in use

The default PostgreSQL host port is `5433` (set in `.env.example`) to avoid conflicts with a local PostgreSQL installation. If any mapped port is already in use, change the host-side port mappings in your `.env` file or stop the conflicting service.

### Kafka client cannot connect from the host

The broker advertises `PLAINTEXT_HOST://localhost:9092` by default. If you change `KAFKA_HOST`, use that host in `KAFKA_BOOTSTRAP_SERVERS`.

### Flyway migration errors

Ensure the PostgreSQL container is healthy and that `DATABASE_URL` points to `aqvp_db`. If you previously ran with separate per-service databases, reset with `docker compose -f docker/docker-compose.dev.yml --env-file .env down -v` and start fresh.

### Spring Boot cannot find Redis or Kafka

The properties are read only when the corresponding starter dependencies are on the classpath. The current skeleton services do not yet include Redis or Kafka starters, so missing infrastructure does not block startup.

## Common errors

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Connection refused` to PostgreSQL | Container not ready | Wait for `healthy` status with `docker compose --env-file .env ps` |
| `FATAL: password authentication failed for user "aqvp"` | Shell environment variables override `.env` or values are stale | Clear shell variables with the snippet above and reload `.env` |
| PostgreSQL uses port `5432` instead of `.env` value | Compose did not read `.env`; shell variable/default used | Use `--env-file .env` on every `docker compose` command |
| `Cannot load driver class: org.h2.Driver` | Missing H2 profile | Add `-Plocal-h2` or start PostgreSQL |
| `invalid source release 21` | Wrong JDK | Install Java 21 and set `JAVA_HOME` |
| Swagger returns 401 | Security permit list | Use the `local` profile or verify public endpoints |

## Resetting development infrastructure

To completely reset all local data and start fresh:

```powershell
docker compose -f docker/docker-compose.dev.yml --env-file .env down -v
docker compose -f docker/docker-compose.dev.yml --env-file .env up -d
```

## Security notes

- The `.env` file is ignored by Git. Never commit it.
- Development credentials (`aqvp`/`aqvp`) are for local use only.
- `JWT_SECRET=CHANGE_ME` in `.env.example` must be replaced in your local `.env` file.
- Do not use development values in production, staging, or shared environments.

## Getting a new developer running

1. Install Docker Desktop, Java 21, and Maven 3.9+.
2. Clone the repository and switch to `develop`.
3. Copy `.env.example` to `.env`.
4. Replace `JWT_SECRET` with a long random value.
5. Start Docker Desktop.
6. Run `docker compose -f docker/docker-compose.dev.yml --env-file .env up -d`.
7. Verify all containers are healthy with `docker compose -f docker/docker-compose.dev.yml --env-file .env ps`.
8. Load environment variables and start the Identity service.
9. Open Swagger at `http://localhost:8081/swagger-ui.html`.
10. Start the React frontend and log in with the demo admin account.
