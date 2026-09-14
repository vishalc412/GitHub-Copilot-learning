# TaskFlow — a GitHub Copilot Framework

A **reusable Copilot configuration layer** for Java + React projects, with a
small working codebase attached so the rules have something real to point at.

The product here is `.github/` and `AGENTS.md`. The application is the
reference material that makes Copilot's output correct.

---

## Use it on your own project (5 minutes)

Copy the framework layer, keep your own code:

```bash
cp -r examples/taskflow/.github    /path/to/your-project/
cp    examples/taskflow/AGENTS.md  /path/to/your-project/
cp -r examples/taskflow/.vscode    /path/to/your-project/
```

Then edit three things and you are done:

1. **`.github/copilot-instructions.md`** — replace the "What this project is",
   "Architectural invariants", and "Commands" sections with yours. Keep the
   structure; it is the part that works.
2. **`.github/instructions/*.instructions.md`** — fix the `applyTo` globs to
   match your layout, and replace the layer rules with your own.
3. **`AGENTS.md`** — replace the setup and verify commands.

Leave the chat modes and prompt files as-is initially; they are mostly
project-agnostic.

> **Important:** open the project folder itself as your VS Code workspace root.
> Copilot resolves `.github/instructions/**` relative to the workspace root, so
> a nested `.github/` (like this one, inside a larger repo) is not picked up
> unless that folder is the root.

---

## What is in the framework

| File | Layer | What it does |
|---|---|---|
| `AGENTS.md` | 1 | Tool-agnostic setup + workflow contract. Read by the Copilot coding agent. |
| `.github/copilot-instructions.md` | 2 | Repo-wide rules. Read by **every** Copilot surface. |
| `.github/instructions/backend-java.instructions.md` | 3 | Auto-applies to `backend/**/*.java` |
| `.github/instructions/frontend-react.instructions.md` | 3 | Auto-applies to `frontend/**/*.ts,tsx` |
| `.github/instructions/tests.instructions.md` | 3 | Auto-applies to test files only |
| `.github/chatmodes/architect.chatmode.md` | 4 | Plans changes, never edits |
| `.github/chatmodes/reviewer.chatmode.md` | 4 | Reviews diffs, never edits |
| `.github/chatmodes/test-writer.chatmode.md` | 4 | Writes tests only, cannot touch production code |
| `.github/prompts/*.prompt.md` | — | Reusable commands: `/new-endpoint`, `/new-component`, `/add-tests`, `/review-diff` |
| `.github/workflows/copilot-setup-steps.yml` | — | Bootstraps the coding agent's environment |
| `.github/ISSUE_TEMPLATE/agent-ready-task.md` | — | Issue shape the coding agent can actually complete |
| `.vscode/settings.json` | — | Wires instruction files into test/review/commit generation |
| `.vscode/mcp.json` | — | Optional MCP servers for Agent Mode |

Layers 1–4 are **additive**: Copilot combines every file that applies to the
file you are editing. More specific layers add detail; they do not repeat the
general ones.

---

## How to actually use it day to day

**Inline completions** — just work. The instruction files are already loaded,
so completions follow your layer rules without you prompting.

**A new endpoint or component** — use the prompt files. In Copilot Chat:

```
/new-endpoint     then answer the questions it asks
/new-component
/add-tests        point it at a file with gaps
/review-diff      before you open the PR
```

**A fuzzy change** — switch to the **architect** chat mode, get a plan, then
hand the plan to Agent Mode or paste it into an issue for the coding agent.

**Delegating to the coding agent** — open an issue with the
`agent-ready-task` template, fill in the acceptance criteria, assign it to
Copilot. The checkboxes become its definition of done.

**Before review** — switch to the **reviewer** chat mode and run it over your
diff. It catches contract drift and layer violations that CI does not.

---

## Why this framework has teeth

Most Copilot instruction templates restate generic best practice. The value
here is the **verified gotchas table** in `.github/copilot-instructions.md` —
every row was hit and fixed while building this project, not guessed:

- Spring Boot 4 split the test slices into per-technology starters, and moved
  their packages
- Boot 4 ships Jackson 3 under `tools.jackson`, so the familiar
  `com.fasterxml` `ObjectMapper` is not a bean
- `@EnableJpaAuditing` on the application class breaks every `@WebMvcTest`
- A blanket `@ExceptionHandler(Exception.class)` turns every mistyped URL into
  a 500
- The H2 console needs its own Boot 4 dependency
- Vitest's `defineConfig` must come from `vitest/config`

Encoding this class of knowledge is the whole point: it is exactly what a
model guesses wrong, and exactly what a reviewer would otherwise catch by hand
every time.

---

## The reference application

Small, layered, and fully working — the context that makes the rules concrete.

```
backend/    Spring Boot 4.1 · Java 21 · Maven · H2      55 tests
frontend/   React 19 · TypeScript 5.9 · Vite 8 · Vitest  32 tests
```

Run it:

```bash
cd backend  && mvn spring-boot:run     # :8080 · Swagger at /swagger-ui.html
cd frontend && npm install && npm run dev   # :5173 · proxies /api to :8080
```

Verify it:

```bash
cd backend  && mvn test        # Tests run: 55, Failures: 0, Errors: 0
cd frontend && npm test        # Tests 32 passed (32)
cd frontend && npm run build   # typecheck + production build
```

No database server, no Docker, no credentials required.

### What the code demonstrates

- Lifecycle rules defined **once** on an enum, exposed to the UI as
  `allowedTransitions` so the client never re-implements the state machine
- Dynamic filtering with JPA Specifications that return no-op predicates
  instead of `null`
- RFC 9457 `problem+json` errors with field-level validation detail
- Optimistic locking via `@Version`
- A typed `fetch` wrapper that turns every failure into one `ApiError` type
- Request cancellation on dependency change, so a slow response cannot
  overwrite a newer one
- Test pyramid in practice: enum unit tests → service unit tests → `@DataJpaTest`
  → `@WebMvcTest` → a handful of full-stack journeys

---

## License

MIT. Copy it, strip the app, keep the framework.
