# Usher URL Shortener

Microservices learning project built with Kotlin + Spring Boot.

## Phase 0 Scope

This phase only prepares the foundation:

- Gradle multi-module monorepo
- 5 empty Spring Boot services
- Shared `common` module
- PostgreSQL, Kafka + Zookeeper, and Redis through Docker Compose
- `/actuator/health` on every service

## Module Structure

- `auth-service` - starter module for user and token flows
- `url-service` - starter module for the URL shortening domain
- `gateway` - starter module for the external traffic entry point
- `analytics-service` - starter module for Kafka consumers and reporting
- `notification-service` - starter module for external notification integrations
- `common` - shared DTO/util module

## Running Locally

Start all infrastructure and empty services:

```bash
docker compose up --build
```

Service health check URLs:

- Auth Service: http://localhost:8081/actuator/health
- URL Service: http://localhost:8082/actuator/health
- Gateway: http://localhost:8080/actuator/health
- Analytics Service: http://localhost:8083/actuator/health
- Notification Service: http://localhost:8084/actuator/health

## Phase 0 Verification

List Kafka topics:

```bash
docker compose exec kafka kafka-topics --bootstrap-server kafka:29092 --list
```

Check PostgreSQL databases:

```bash
docker compose exec postgres psql -U usher -d usher_admin -c "\\l"
```

Check Redis:

```bash
docker compose exec redis redis-cli ping
```
