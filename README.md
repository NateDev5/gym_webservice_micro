# Gym Webservice Microservices

Spring Boot microservices project for gym domain workflows.

## Services

- `api-gateway` (port `8080`)
- `enrollment-service` (port `8081`) - orchestrator/aggregator with HATEOAS, Ports, ACL
- `member-service` (port `8082`)
- `schedule-service` (port `8083`)
- `trainer-service` (port `8084`)

Each business service has its own PostgreSQL database in Docker Compose.

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Cloud Gateway
- Spring Data JPA + Flyway
- H2 (local profile)
- PostgreSQL (docker profile)
- springdoc OpenAPI / Swagger UI

## Project Structure

This repository is a Gradle multi-project build:

- `settings.gradle` includes all 5 services
- each service is an independent Spring Boot app
- `docker-compose.yml` runs 9 containers (5 apps + 4 postgres)

## Run Locally (H2)

Prerequisites:

- Java 17 (`JAVA_HOME` set)

Run all tests:

```bash
./gradlew test
```

Run one service (example):

```bash
./gradlew :member-service:bootRun
```

All services default to `h2` profile via `application.yml`.

## Run with Docker Compose (PostgreSQL)

Prerequisites:

- Docker + Docker Compose

Start all services:

```bash
docker compose up --build
```

## API Gateway and Swagger

Gateway base URL:

- `http://localhost:8080`

Gateway Swagger UI:

- `http://localhost:8080/swagger-ui.html`

Direct service docs (through gateway aggregation routes):

- `/swagger-docs/member-service`
- `/swagger-docs/schedule-service`
- `/swagger-docs/trainer-service`
- `/swagger-docs/enrollment-service`

## Postman

Collection file:

- `postman/gym_webservice.postman_collection.json`

Notes:

- `baseUrl` is `http://localhost:8080`
- service folders are split into `Positive` and `Negative` tests
- setup and cleanup requests are in `00 Setup` and `05 Cleanup`

## Ports and Container Mapping

- Gateway: `8080:8080`
- Enrollment: `8081:8081`
- Member: `8082:8082`
- Schedule: `8083:8083`
- Trainer: `8084:8084`
- Member DB: `54321:5432`
- Trainer DB: `54322:5432`
- Schedule DB: `54323:5432`
- Enrollment DB: `54324:5432`

## CORS

CORS is enabled for localhost origins on all services and gateway.
