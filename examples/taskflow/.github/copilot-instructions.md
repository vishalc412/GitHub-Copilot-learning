# Copilot Instructions — TaskFlow

Repo-wide rules. Read by every Copilot surface: inline completions, Chat,
Agent Mode, the coding agent, and code review.

## What this project is

A Java + React reference application used as a **Copilot framework**: the
instruction files in `.github/` are the product, and the working code is the
context that makes Copilot's output correct.

- `backend/` — Spring Boot 4.1, Java 21, Maven, H2. Layered: domain →
  repository → service → web.
- `frontend/` — React 19, TypeScript, Vite 8, Vitest. Typed API client +
  custom hooks + presentational components.

## Non-negotiables

1. **Never invent API shapes.** The contract is
   `backend/src/main/java/com/example/taskflow/web/dto/` and its mirror
   `frontend/src/types/task.ts`. Change the DTO first, then the TS type, then
   the call sites.
2. **Business rules live in the domain or service layer**, never in a
   controller or a React component. Controllers bind and delegate. Components
   render and dispatch.
3. **Every change ships with a test.** New service method → unit test. New
   endpoint → `@WebMvcTest` case. New component → Vitest case.
4. **No secrets in code.** Config comes from `application.yml` properties or
   environment variables.
5. **Errors are typed.** Backend throws a domain exception mapped in
   `GlobalExceptionHandler` to RFC 9457 `problem+json`. Frontend catches
   `ApiError` and reads `fieldErrors`.

## Architectural invariants

- **Status transitions** are defined once, on the `TaskStatus` enum. The API
  returns `allowedTransitions` so the UI never re-implements the state machine.
  Do not add status logic anywhere else.
- **`PUT /tasks/{id}` must not change status.** Lifecycle changes go through
  `PATCH /tasks/{id}/status` so the rules cannot be bypassed.
- **Dynamic queries use JPA Specifications** (`TaskSpecifications`), which
  return no-op predicates when a filter is absent — never `null`, and never
  `:param IS NULL OR ...` JPQL.

## Version gotchas that will bite you

These are verified against this project's actual build — not guesses. Getting
them wrong produces confusing failures.

| Area | Rule |
|---|---|
| Test slices | Spring Boot 4 split them out. `@WebMvcTest` needs `spring-boot-starter-webmvc-test`; `@DataJpaTest` needs `spring-boot-starter-data-jpa-test`. `spring-boot-starter-test` alone is not enough. |
| Slice packages | `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`, `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`. The old `boot.test.autoconfigure.*` paths are gone. |
| Mocking beans | Use `@MockitoBean`. `@MockBean` is removed. |
| JSON | Boot 4 ships **Jackson 3** (`tools.jackson`). Do not autowire `com.fasterxml.jackson.databind.ObjectMapper` — there is no such bean. Prefer JSON string literals in web tests. |
| JPA auditing | `@EnableJpaAuditing` lives in `config/JpaAuditingConfig`, **not** on the application class — on the app class it breaks every `@WebMvcTest` with "JPA metamodel must not be empty". Slice tests needing auditing use `@Import(JpaAuditingConfig.class)`. |
| Blanket exception handler | A `@ExceptionHandler(Exception.class)` masks Spring's `NoResourceFoundException`, turning wrong URLs into 500s. Keep the explicit `NoResourceFoundException` → 404 handler. |
| H2 console | Needs the `spring-boot-h2console` dependency in Boot 4; the property alone does nothing. |
| Vitest config | `defineConfig` must come from `vitest/config`, not `vite`, or the `test` block fails to typecheck. |

## Java conventions

- Records for DTOs, explicit accessors for JPA entities. **No Lombok** — this
  project has no annotation processor by design.
- Constructor injection only. Never field `@Autowired`.
- JavaDoc on every public type and method: what it does, `@param`, `@return`,
  `@throws`.
- Unchecked exceptions for business errors, each with its own type.
- `@Transactional(readOnly = true)` at class level; writable overrides per method.

## TypeScript / React conventions

- Functional components only. No class components.
- No `any`. Use `unknown` plus a narrowing check.
- An `interface` for every component's props — never inline prop types.
- Reusable logic goes in a `use*` hook under `src/hooks/`.
- Data fetching aborts on unmount and on dependency change (see `useTasks`),
  so a slow response cannot overwrite a newer one.
- `strict` plus `noUncheckedIndexedAccess` are on. Index access is
  `T | undefined` — handle it, do not cast it away.

## Commands

```bash
# backend
cd backend && mvn test          # 55 tests
cd backend && mvn spring-boot:run   # :8080, Swagger at /swagger-ui.html

# frontend
cd frontend && npm install
cd frontend && npm test         # 32 tests
cd frontend && npm run build    # typecheck + production build
cd frontend && npm run dev      # :5173, proxies /api to :8080
```

## When you are unsure

Say so. Do not generate a plausible-looking API call, property name, or
annotation you have not seen in this repo — check the file and cite it.
