# Spring Boot

## session1-task-tracker-api
In-memory REST API for tasks.

| Method | Path | Result |
|---|---|---|
| POST | `/api/tasks` | 201 with `Location`, 400 for a blank title, 409 when `tasktracker.max-tasks` is reached |
| GET | `/api/tasks` | 200, optional `?completed=` and `?limit=` (falls back to `tasktracker.default-page-size`) |
| GET | `/api/tasks/{id}` | 200 or 404 |
| PUT | `/api/tasks/{id}` | 200 or 404 |
| PATCH | `/api/tasks/{id}/complete` | 200 or 404 |
| DELETE | `/api/tasks/{id}` | 204 or 404 |

`mvn test` runs a MockMvc test that walks through every row, with the limits lowered through `@TestPropertySource`.

## session2-config-logging-actuator
- `application.yml` plus `application-dev.yml` and `application-prod.yml`, selected with `--spring.profiles.active`.
- `DevDataSeeder` is a `@Profile("dev")` configuration that seeds two products.
- `ShopProperties` is a `@ConfigurationProperties` record served at `/api/info`.
- `ProductService` logs with SLF4J at `INFO` and `DEBUG`; the package level changes per profile.
- DevTools is on the classpath as an optional runtime dependency.
- Actuator exposes `health`, `info` and `metrics`. `/actuator/info` reads `info.*` because `management.info.env.enabled` is true.
