# Spring MVC

## task-mvc-jsp
Implements `Task.txt` with Spring MVC and JSP. The task has `id`, `title`, `completed`, and a `priority` because the search and validation parts need one.

| Part | Implementation |
|---|---|
| 1 | `GET /tasks` (JSTL list), `GET /tasks/{id}` with `@PathVariable`, `GET /tasks/search?priority=HIGH` with an optional `@RequestParam` that defaults to `HIGH` |
| 2 | `GET /tasks/new` shows a `form:form`, `POST /tasks` saves and redirects |
| 3 | `@NotBlank` on `title`, `@NotNull` on `priority`, shown with `form:errors` |
| 4 | `TaskNotFoundException` with `@ResponseStatus(NOT_FOUND)`, handled in a `@ControllerAdvice` that renders `error.jsp` |

Run with `mvn spring-boot:run` and open `http://localhost:8080/tasks`.

## product-rest-api
Lab 3 from Session 3.

1. `ProductRestController` at `/api/products` with list, get, create (201 and `Location`), update and delete (204).
2. `ResponseEntity` everywhere, 404 through `ProductNotFoundException`.
3. `@Valid @RequestBody`; `MethodArgumentNotValidException` returns a field to message map.
4. `ApiExceptionHandler` returns an `ErrorResponse` for not found and for any other exception.
5. `POST /api/products/quick` has one method per `consumes` type, JSON and form-urlencoded.
6. `ApiKeyInterceptor` on `/api/**` returns a JSON 401 without `X-Api-Key`.
7. Bonus: `ResponseWrapperAdvice` wraps successful controller responses in `{"data": ..., "timestamp": ...}`. It is scoped with `assignableTypes = ProductRestController.class`, so responses coming from `ApiExceptionHandler` and from the interceptor are not wrapped.

```bash
curl -H "X-Api-Key: k" http://localhost:8080/api/products
curl -H "X-Api-Key: k" -H "Content-Type: application/json" -d '{"name":"Pen","price":2}' http://localhost:8080/api/products/quick
curl -H "X-Api-Key: k" -d "name=Pen&price=2" http://localhost:8080/api/products/quick
```

`mvn test` covers all of it.
