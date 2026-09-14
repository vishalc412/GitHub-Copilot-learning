---
mode: ask
description: "Review the current diff against this project's invariants"
---

Review the current diff (`git diff` plus staged changes) for TaskFlow.

Work through these in order and stop at the first category with real findings:

1. **Correctness** — logic errors, null handling, wrong status code,
   transaction boundary in the wrong place, race conditions.
2. **Layer violations**
   - business logic in a controller or React component
   - a component importing `api/` or calling `fetch`
   - HTTP types leaking into the service layer
   - status/lifecycle logic anywhere except the `TaskStatus` enum
   - `PUT /tasks/{id}` able to change status
3. **Contract drift** — a DTO changed without the matching
   `frontend/src/types/task.ts` update, or vice versa.
4. **Version traps** — `@MockBean`; an autowired `com.fasterxml` `ObjectMapper`;
   an `org.springframework.boot.test.autoconfigure.*` import;
   `@EnableJpaAuditing` on the application class; a `Specification` returning
   `null`; `defineConfig` imported from `vite`; a blanket `Exception` handler
   without the `NoResourceFoundException` → 404 companion.
5. **Test gaps** — new behaviour without a test, or a test that would pass
   with the production code deleted.
6. **Style** — only if nothing above applies.

For each finding give: `file:line`, the concrete consequence, and the fix.

Cap at the top 5 by risk. If the diff is clean, say so plainly — do not invent
findings to look thorough.
