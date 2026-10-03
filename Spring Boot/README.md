# spring boot

## session1-task-tracker-api

in memory task api, no database. endpoints:

- POST /api/tasks -> 201 + Location. 400 if title is blank, 409 when we already have tasktracker.max-tasks tasks
- GET /api/tasks -> optional ?completed= and ?limit=, limit falls back to tasktracker.default-page-size
- GET /api/tasks/{id} -> 200 or 404
- PUT /api/tasks/{id} -> 200 or 404
- PATCH /api/tasks/{id}/complete -> 200 or 404
- DELETE /api/tasks/{id} -> 204 or 404

there is a MockMvc test that goes through all of them (it lowers the limits with @TestPropertySource so the 409 is easy to hit).

## session2-config-logging-actuator

- application.yml + application-dev.yml and application-prod.yml, switch with --spring.profiles.active
- DevDataSeeder only exists in the dev profile, it adds a keyboard and a mouse
- ShopProperties is a @ConfigurationProperties record, you can see it at /api/info
- ProductService logs with slf4j (info + debug), log level for the package changes per profile
- devtools is in the pom as optional
- actuator exposes health, info and metrics. had to set management.info.env.enabled=true or /actuator/info just returns {} (found that out the hard way)
