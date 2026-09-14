# Custom Chat Modes

## Hierarchy Level: Agentic Features

Custom chat modes let you define **reusable Copilot Chat personas** with a
fixed system prompt, a restricted toolset, and (optionally) a preferred
model — saved as a file in your repo so the whole team gets the same
specialized assistant.

Built-in modes are Ask / Edit / Agent (see
[01-agent-mode.md](01-agent-mode.md)). Custom modes let you go further:
"Architect" (plans only, never edits), "Reviewer" (read-only, critiques),
"Test Writer" (only touches `*.test.*` files), etc.

---

## File Format

Custom modes live in `.github/chatmodes/<name>.chatmode.md` — a Markdown
file with YAML frontmatter:

```markdown
---
description: "Plans changes without editing files"
tools: ["search", "usages"]
model: "GPT-4.1"
---

You are a staff engineer doing technical planning. Given a feature request:

1. Identify affected files and modules
2. List the design options with tradeoffs
3. Recommend one approach
4. Output a step-by-step implementation plan

Never edit files directly. Your output is a plan, not a diff.
```

Frontmatter fields:

| Field | Purpose |
|-------|---------|
| `description` | Shown in the mode picker |
| `tools` | Allow-list of tools this mode can use (omit = inherit Agent Mode defaults) |
| `model` | Pin this persona to a specific model regardless of your global default |

See working examples in this repo:
`.github/chatmodes/architect.chatmode.md` and
`.github/chatmodes/reviewer.chatmode.md`.

---

## Why Restrict Tools Per Mode

The single biggest source of "the agent did something I didn't want" is
giving it more capability than the task needs. Custom modes let you make
the safe default explicit:

```
Architect mode  → search + read only, NO edit, NO terminal
Reviewer mode   → read only, comments, NO edit
Test Writer     → edit restricted to test file globs, terminal for test runner only
Full Agent mode → everything (use deliberately, not by default)
```

This mirrors the principle of least privilege from security: **scope the
agent's blast radius to the task.**

---

## Example: Reviewer Mode

```markdown
---
description: "Reviews code for correctness, security, and style — never edits"
tools: ["search", "usages", "problems"]
---

You are a senior code reviewer. When given a diff or file:

1. Flag correctness bugs first (logic errors, off-by-one, null handling)
2. Flag security issues (injection, secrets, unsafe deserialization)
3. Flag style/consistency issues last, and only if they matter
4. For each finding: cite the exact line, explain the risk, suggest a fix

Do not rewrite the code yourself. Output findings as a numbered list.
Skip nitpicks if there are more than 5 real issues — prioritize.
```

---

## Combining With Instructions Files

Custom chat modes stack with, not replace, your project instructions:

```
.github/copilot-instructions.md      → always applied (global rules)
.github/instructions/*.instructions.md → applied when file paths match
.github/chatmodes/<mode>.chatmode.md   → applied only when you select that mode
```

A well-instructed repo has all three layers working together: global
conventions, path-specific rules, and task-specific personas.

---

## Exercise: Build a Team Persona

Create a custom chat mode for a role your team actually needs — e.g.
"Migration Assistant" that only edits files under `migrations/` and always
generates a rollback script alongside every change.

**Assignment**: Design and ship 3 custom chat modes for your team (e.g.
Architect, Reviewer, Test Writer). Get a teammate to use each one and
collect feedback on whether the tool restrictions felt right.
