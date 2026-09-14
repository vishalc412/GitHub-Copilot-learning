---
mode: agent
description: "Add a backend endpoint end to end, with tests and the TS type mirror"
---

Add a new endpoint to the TaskFlow backend.

Ask me for these if I have not already given them:
- HTTP method and path
- Request shape and validation rules
- Response shape
- Failure modes and the status code each should produce

Then implement, in this exact order:

1. **DTO** — a record in `backend/src/main/java/com/example/taskflow/web/dto/`
   with Bean Validation annotations and full JavaDoc on the record and each
   component.
2. **Service** — a method on `TaskService` with JavaDoc including `@throws`
   for every failure mode. Business rules go here or on the domain object,
   never in the controller. Add `@Transactional` if it writes.
3. **Exceptions** — a specific unchecked exception per failure mode in
   `service/exception/`, plus a handler in `GlobalExceptionHandler` using
   `ProblemDetail`, a stable `type` slug, and relevant `setProperty(...)`
   context.
4. **Controller** — a method on `TaskController` with `@Valid`, `@Operation`,
   `@ApiResponses`, the right status code, and a `Location` header on create.
5. **Tests** —
   - service unit test (`@ExtendWith(MockitoExtension.class)`) covering happy
     path and every failure mode
   - `@WebMvcTest` case per status code, asserting the `problem+json` body
   - extend `TaskApiIntegrationTest` only if this adds a new user journey
6. **Frontend mirror** — add the type to `frontend/src/types/task.ts` and the
   call function to `frontend/src/api/tasks.ts`, fully typed.

Then run `cd backend && mvn test` and report the count. Do not report success
unless the suite passes — and read the Maven output, not just an exit code.

Follow `.github/instructions/backend-java.instructions.md` throughout. Respect
the version gotchas table in `.github/copilot-instructions.md`.
