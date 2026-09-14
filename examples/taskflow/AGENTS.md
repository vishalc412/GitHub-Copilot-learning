# AGENTS.md

Instructions for autonomous coding agents working in TaskFlow. Read by the
GitHub Copilot coding agent and other tools that support the open agents.md
standard.

Copilot-specific rules live in `.github/copilot-instructions.md`; this file is
the tool-agnostic setup and workflow contract.

## Project

- `backend/` — Spring Boot 4.1, Java 21, Maven, H2 in-memory. 55 tests.
- `frontend/` — React 19, TypeScript 5.9, Vite 8, Vitest 5. 32 tests.

No database server, no Docker, no cloud credentials are needed. Everything
runs locally out of the box.

## Setup

```bash
# Backend — requires JDK 21 and Maven
cd backend && mvn -B test

# Frontend — requires Node 20+
cd frontend && npm ci && npm test
```

## Verify before you finish

Run **both** suites and read the output, not just the exit code:

```bash
cd backend  && mvn -B test          # expect: Tests run: 55, Failures: 0, Errors: 0
cd frontend && npm test             # expect: Tests 32 passed (32)
cd frontend && npm run build        # typecheck + production build
```

A wrapper script's exit code is not the tool's exit code. Confirm the
`BUILD SUCCESS` / `passed` line before claiming green.

## How to work here

1. **Read the layer rules first** in
   `.github/instructions/backend-java.instructions.md` and
   `frontend-react.instructions.md`. Code in the wrong layer will be rejected
   even if it works.
2. **Follow the "order that works"** in the relevant instructions file — DTO
   before service before controller before test; types before API before hook
   before component.
3. **Keep the contract in sync.** A change to a Java DTO requires the matching
   change in `frontend/src/types/task.ts` in the same commit.
4. **Every behaviour change ships a test.** See
   `.github/instructions/tests.instructions.md` for choosing the level.
5. **Do not add dependencies** without saying why the current stack cannot do
   it. This project deliberately has no Lombok, no MapStruct, and no frontend
   state library.

## Known version traps

Full table in `.github/copilot-instructions.md`. The short version, all
verified against this build:

- `@WebMvcTest` / `@DataJpaTest` need their own Boot 4 starters and live in
  `boot.webmvc.test.autoconfigure` / `boot.data.jpa.test.autoconfigure`
- `@MockitoBean`, not `@MockBean`
- Boot 4 ships Jackson 3 (`tools.jackson`) — there is no `com.fasterxml`
  `ObjectMapper` bean
- `@EnableJpaAuditing` must stay in `config/JpaAuditingConfig`
- `defineConfig` comes from `vitest/config`, not `vite`

## Done means

- [ ] Both suites pass, verified from their output
- [ ] New behaviour has a test that fails if the code is removed
- [ ] DTO ↔ TS types in sync
- [ ] No new dependency without justification
- [ ] No secrets, no hardcoded credentials
