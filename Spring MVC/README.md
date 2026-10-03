# spring mvc

## task-mvc-jsp

Task.txt, with jsp. Task has id, title, completed and a priority (added the priority myself because the search + validation parts need it).

1. GET /tasks lists with jstl, /tasks/{id} uses @PathVariable, /tasks/search?priority=HIGH uses an optional @RequestParam that defaults to HIGH
2. /tasks/new shows the form (spring form tags), POST /tasks saves it and redirects back to the list
3. @NotBlank on title and @NotNull on priority, errors show up next to the fields with form:errors
4. TaskNotFoundException has @ResponseStatus(NOT_FOUND) and a @ControllerAdvice renders error.jsp for it

run with `mvn spring-boot:run` then open localhost:8080/tasks. (it uses boot 3.3 just to host the jsp's, the mvc stuff is all normal spring mvc)

## product-rest-api

lab 3 from session 3.

- ProductRestController, full crud, 201 + Location on create, 204 on delete, ResponseEntity everywhere
- 404 goes through ProductNotFoundException
- @Valid on the body, validation errors come back as a field -> message map
- ApiExceptionHandler is the @RestControllerAdvice, ErrorResponse for not found and a generic fallback
- /api/products/quick has two methods with different consumes, json and form urlencoded
- ApiKeyInterceptor on /api/** gives a json 401 if X-Api-Key is missing
- bonus: ResponseWrapperAdvice wraps good responses as {"data":..., "timestamp":...}. its limited to ProductRestController (assignableTypes) so errors and the 401 dont get wrapped

try it:

```bash
curl -H "X-Api-Key: k" localhost:8080/api/products
curl -H "X-Api-Key: k" -H "Content-Type: application/json" -d '{"name":"Pen","price":2}' localhost:8080/api/products/quick
curl -H "X-Api-Key: k" -d "name=Pen&price=2" localhost:8080/api/products/quick
```

mvn test covers everything above
