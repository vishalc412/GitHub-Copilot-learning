# Copilot Agent Mode

## Hierarchy Level: Agentic Features

Agent Mode is Copilot Chat's autonomous, multi-step editing mode inside your
IDE (VS Code, Visual Studio, JetBrains). Unlike "Ask" (answers questions) or
"Edit" (edits the files you select), **Agent Mode plans and executes a task
end-to-end**: it decides which files to open, edits multiple files, runs
terminal commands, reads the output, and iterates until the task is done or
it needs your input.

---

## The Three Chat Modes

| Mode | Scope | Autonomy | Best For |
|------|-------|----------|----------|
| **Ask** | Read-only, current context | None — just answers | "Explain this regex", "why is this slow?" |
| **Edit** | Files you explicitly add to context | Edits only, no terminal | Targeted multi-file edits you've scoped yourself |
| **Agent** | Whole workspace | Full — reads, edits, runs commands, self-corrects | "Add pagination to this API and update the tests" |

Switch modes from the dropdown at the top of the Chat panel (or `Ctrl/Cmd+Shift+I`
to open Chat, then pick the mode).

---

## How Agent Mode Actually Works

```
┌─────────────────────────────────────────────────────────┐
│  1. You give a goal (not a diff)                         │
│     "Add rate limiting to the /login endpoint"           │
│                                                           │
│  2. Agent builds a plan                                  │
│     - Searches codebase for relevant files                │
│     - Reads existing patterns (e.g. other middleware)    │
│                                                           │
│  3. Agent acts, tool by tool                              │
│     - Edits files                                         │
│     - Runs `npm install`, `pytest`, `mvn test`, etc.      │
│     - Reads terminal output                                │
│                                                           │
│  4. Agent self-corrects                                    │
│     - If tests fail, it reads the error and retries        │
│                                                           │
│  5. You review the diff and accept/reject per file         │
└─────────────────────────────────────────────────────────┘
```

Agent Mode has access to **tools**: file search, file edit, terminal
execution, and (if configured) any **MCP servers** you've registered — see
[04-mcp-integration.md](04-mcp-integration.md).

---

## Writing Effective Agent Mode Prompts

Agent Mode rewards **goals with acceptance criteria**, not step-by-step
instructions (it will figure out the steps itself).

### Weak Prompt
```
Fix the bug in the login page
```

### Strong Prompt
```
Users report the login form submits twice when they double-click Submit.
Fix the race condition, add a test that reproduces the double-submit bug,
and make sure the existing auth test suite still passes.
```

### Prompt Template
```
Context: [what's happening / current behavior]
Goal: [desired end state]
Constraints: [things it must not break, style to follow]
Definition of done: [tests pass / lints clean / specific check]
```

---

## Guardrails You Control

| Control | What It Does |
|---------|--------------|
| **Terminal command approval** | Agent asks before running commands unless you've allow-listed them |
| **File edit review** | Every edit appears as a diff you approve, per-file or all-at-once |
| **`.github/copilot-instructions.md`** | Sets project-wide rules Agent Mode follows automatically |
| **`.github/instructions/*.instructions.md`** | Path-scoped rules (e.g., only for `**/*.test.ts`) |
| **Undo checkpoint** | VS Code snapshots state so you can revert an entire agent turn |

**Rule of thumb**: The more your instruction files (`copilot-instructions.md`,
`AGENTS.md`, `*.instructions.md`) already encode your conventions, the less
Agent Mode needs to guess — and the fewer bad edits you'll see.

---

## Exercise: Agent Mode Task

Try this in a scratch repo:

```
Goal: Add input validation to the createUser function.
Constraints: Use the same validation library already imported elsewhere
             in the project. Don't change the function signature.
Definition of done: Add at least 3 unit tests covering invalid email,
             empty name, and duplicate email; all tests pass.
```

Observe:
1. Which files it opened before editing (context gathering)
2. Whether it asked permission before running the test command
3. Whether it iterated after a failing test

**Assignment**: Run 5 Agent Mode tasks of increasing complexity. For each,
record: the prompt, files touched, commands run, and whether you had to
intervene. This becomes your baseline for how well-instructed your repo is.

---

## When NOT to Use Agent Mode

- Exploratory "what does this even do" questions → use **Ask**
- A single, precisely-known edit → use **Edit** (faster, less risk)
- Anything touching production secrets, infra, or destructive migrations →
  do it yourself, with Copilot as a reviewer, not the driver
- Long-running/background work spanning many files across a whole issue →
  consider the **coding agent** instead (see
  [02-coding-agent.md](02-coding-agent.md)), which runs in an isolated
  environment and opens a PR rather than editing your live working copy
