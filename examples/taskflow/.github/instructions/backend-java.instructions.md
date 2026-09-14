---
applyTo: "backend/**/*.java"
---

# Backend (Java / Spring Boot 4) instructions

Applies to every Java file. Extends `.github/copilot-instructions.md`.

## Layer rules

```
web/         HTTP only — bind, validate, delegate, map to DTO. No business logic.
service/     Business rules, transactions, orchestration. No HTTP types.
repository/  Data access + Specifications. No business rules.
domain/      Entities and invariants. Rules that belong to the object live here.
```

A controller that contains an `if` about business state is a bug. Move it down.

## Adding an endpoint — the order that works

1. DTO record in `web/dto/` with Bean Validation annotations
2. Service method with JavaDoc and a domain exception for each failure mode
3. Exception → status mapping in `GlobalExceptionHandler` (if a new one)
4. Controller method: `@Valid`, OpenAPI annotations, correct status code
5. Tests: service unit test + `@WebMvcTest` contract test
6. Mirror the DTO into `frontend/src/types/task.ts`

## Required on every public method

```java
/**
 * One line on what it does.
 *
 * @param x what it is
 * @return what comes back
 * @throws SomeException when it happens
 */
```

## Patterns to copy

- **Validation**: `@NotBlank`, `@Size`, `@NotNull` on DTO records. Never
  hand-roll validation in a controller.
- **Errors**: throw a specific unchecked exception; map it in
  `GlobalExceptionHandler` with `ProblemDetail`, a stable `type` URI slug, and
  useful `setProperty(...)` context.
- **Queries**: add a factory to `TaskSpecifications` returning
  `unrestricted()` when the filter is absent, then chain with `.and(...)`.
- **Paging**: return `PageResponse.from(page, Mapper::toResponse)`. Never
  return Spring's `Page` directly.
- **Status codes**: 201 + `Location` on create, 204 on delete, 409 on a state
  conflict, 400 on validation.

## Do not

- Add Lombok, MapStruct, or any annotation processor
- Use field injection
- Return entities from controllers — map to a DTO
- Catch an exception only to rethrow it unchanged
- Put `@EnableJpaAuditing` on the application class (see gotchas table)
- Widen `@ExceptionHandler(Exception.class)` without keeping the
  `NoResourceFoundException` → 404 handler
