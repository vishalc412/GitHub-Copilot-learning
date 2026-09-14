---
applyTo: "**/*Test.java,**/*.test.ts,**/*.test.tsx"
---

# Test instructions

Applies to every test file, both stacks.

## Pick the cheapest test that proves the thing

| What you are proving | Use |
|---|---|
| A business rule or pure logic | Plain JUnit / Vitest unit test, no framework context |
| A service's behaviour with collaborators | `@ExtendWith(MockitoExtension.class)` + `@Mock` |
| SQL, a Specification, or a mapping | `@DataJpaTest` + `@Import(JpaAuditingConfig.class)` |
| An HTTP status, header, or error body | `@WebMvcTest` + `@MockitoBean` |
| That the layers are actually wired together | `@SpringBootTest` + `@AutoConfigureMockMvc` — few of these, each a whole journey |
| A component's rendered behaviour | React Testing Library |

Putting a transition-matrix test in `@SpringBootTest` is the most common
mistake: it is 100× slower and proves less than the enum unit test.

## Naming and structure

- Java: `@DisplayName` on the class and every test, in plain English
  ("rejects a duplicate title without touching the database"). Group with
  `@Nested` per method under test.
- TS: `describe('Thing')` → `it('does the specific thing')`.
- Arrange / Act / Assert, in that order, with a blank line between.

## Assertions

- Java: AssertJ (`assertThat`), and `assertThatExceptionOfType(...)` for
  failures — never a bare `try/catch` with `fail()`.
- Assert the **behaviour**, not the implementation. `verify(repo, never())
  .save(any())` is a legitimate behavioural claim ("nothing was written");
  asserting a private field is not.

## What every new test must cover

1. The happy path
2. Each failure mode the code declares (`@throws`, each error status)
3. Boundaries: empty, null, too short, too long, zero, terminal state
4. For regressions: a comment naming the bug, so nobody "simplifies" the guard
   away later — see `unknownPathIsNotFound`

## Do not

- Use `@MockBean` (removed — use `@MockitoBean`)
- Autowire `com.fasterxml.jackson.databind.ObjectMapper` in a web test — Boot 4
  ships Jackson 3; use JSON string literals
- Depend on demo seed data — `taskflow.seed-demo-data` is `false` under test
  and each test owns its fixtures
- Write a test whose assertions still pass when you delete the production code
