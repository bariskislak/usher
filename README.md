# Usher URL Shortener

Microservices learning project built with Kotlin + Spring Boot.

## Current Status

The project has completed the Phase 0 foundation and Phase 1 authentication
flow. Phase 2 has started in `url-service`.

The project is being implemented with a TDD flow:

- Write endpoint contract tests first.
- Keep tests compiling while the next behavior is intentionally red.
- Implement each endpoint one by one.
- Prefer hexagonal boundaries over mocking application dependencies.

Current auth progress:

- `POST /auth/register` is implemented.
- User persistence is modeled behind an application-level `UserRepository` port.
- PostgreSQL persistence is implemented through a JPA adapter.
- Tests use a fake in-memory repository instead of mocks.
- `POST /auth/login`, `POST /auth/refresh`, and `GET /auth/me` are implemented.
- Auth endpoint contract tests are passing.

Current URL progress:

- `POST /urls` is implemented.
- URL persistence is modeled behind an application-level `ShortUrlRepository` port.
- PostgreSQL persistence is implemented through a JPA adapter.
- Tests use a fake in-memory repository and deterministic short code generator.
- Auth is intentionally loosely coupled for now through the `X-User-Id` request header.

## Phase 0 Scope

This phase only prepares the foundation:

- Gradle multi-module monorepo
- 5 empty Spring Boot services
- Shared `common` module
- PostgreSQL, Kafka + Zookeeper, and Redis through Docker Compose
- `/actuator/health` on every service
- Liquibase foundation on database-owning services
- Technology-independent messaging and cache ports in `common`

The gateway does not own a database in this plan.

## Module Structure

- `auth-service` - user registration and token flows
- `url-service` - starter module for the URL shortening domain
- `gateway` - starter module for the external traffic entry point
- `analytics-service` - starter module for Kafka consumers and reporting
- `notification-service` - starter module for external notification integrations
- `common` - shared DTO/util module

## Auth Service

Implemented so far:

- `users` Liquibase migration.
- `User` entity and `UserRole`.
- `UserRepository` application port.
- `JpaUserRepository` and `JpaUserRepositoryAdapter` for PostgreSQL persistence.
- `FakeUserRepository` for tests.
- BCrypt password hashing.
- Stateless Spring Security foundation.
- Validation and API error responses.
- `POST /auth/register`.
- `POST /auth/login`.
- `POST /auth/refresh`.
- `GET /auth/me`.

Current endpoint contract:

| Endpoint | Status | Notes |
| --- | --- | --- |
| `POST /auth/register` | Implemented | Creates a user and returns `201 Created`. |
| `POST /auth/login` | Implemented | Returns short-lived access token and opaque refresh token. |
| `POST /auth/refresh` | Implemented | Rotates refresh token and returns a new token pair. |
| `GET /auth/me` | Implemented | Returns the current user from a valid access token. |

Planned refresh-token design:

- Use opaque random refresh tokens, not refresh JWTs.
- Store only a SHA-256 token hash.
- Rotate refresh tokens on every refresh request.
- Revoke refresh tokens later for logout and password-change flows.

## URL Service

Implemented so far:

- `urls` Liquibase migration.
- `ShortUrl` entity and `ShortUrlStatus`.
- `ShortUrlRepository` application port.
- `JpaShortUrlRepository` and `JpaShortUrlRepositoryAdapter` for PostgreSQL persistence.
- `FakeShortUrlRepository` for tests.
- Random short code generation.
- Short code collision retry.
- Original URL validation.
- `POST /urls`.

Current endpoint contract:

| Endpoint | Status | Notes |
| --- | --- | --- |
| `POST /urls` | Implemented | Creates a short URL for the owner from `X-User-Id`. |

The URL service currently accepts `X-User-Id` as a temporary auth boundary. The
gateway/JWT integration will replace this loose coupling in a later phase.

## Common Ports

The `common` module defines contracts only. It does not contain Kafka, Redis, or other infrastructure adapters yet.

- `EventPublisher`
- `EventConsumer`
- `CachePort`

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

## Running Tests

Run service tests:

```bash
./gradlew :auth-service:test --no-daemon
./gradlew :url-service:test --no-daemon
```

At the current checkpoint, auth-service and url-service tests are expected to
pass.

## Phase 0 Verification

List Kafka topics:

```bash
docker compose exec kafka kafka-topics --bootstrap-server kafka:29092 --list
```

Check PostgreSQL databases:

```bash
docker compose exec postgres psql -U usher -d usher_admin -c "\\l"
```

Check Liquibase metadata tables after the services start:

```bash
docker compose exec postgres psql -U usher -d auth_db -c "\\dt"
docker compose exec postgres psql -U usher -d url_db -c "\\dt"
docker compose exec postgres psql -U usher -d analytics_db -c "\\dt"
docker compose exec postgres psql -U usher -d notification_db -c "\\dt"
```

Check Redis:

```bash
docker compose exec redis redis-cli ping
```
