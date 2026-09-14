---
mode: agent
description: "Add missing tests for existing code without touching production files"
---

Add tests for the code I point you at.

First, **report before you write**:
1. What behaviours the code has (happy path, each failure mode, each boundary)
2. Which of those are already covered by an existing test — name the test
3. Which are not covered
4. For each gap, the cheapest test level that proves it, per the table in
   `.github/instructions/tests.instructions.md`

Then write only the missing tests.

Requirements:
- `@DisplayName` / specific `it(...)` in plain English
- Arrange / Act / Assert, blank line between each
- AssertJ in Java, plain assertions in TS
- `@MockitoBean` not `@MockBean`
- JSON string literals in web tests — no autowired `ObjectMapper` (Jackson 3)
- `@Import(JpaAuditingConfig.class)` on `@DataJpaTest`
- Tests own their fixtures; never rely on demo seed data

Finally, run the relevant suite (`mvn test` or `npm test`) and report the real
count and status — read the output, not just the exit code.

**Do not modify production code.** If a behaviour is untestable as written,
stop and tell me what refactor it needs and why.
