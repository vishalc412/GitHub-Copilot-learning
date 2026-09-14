# The Full Instructions Hierarchy

## Hierarchy Level: Meta (How the Instruction System Itself Works)

Copilot now supports **four layers** of instruction files, each with a
different scope and audience. Understanding how they stack — and which one
wins when they conflict — is essential to configuring Copilot precisely
instead of fighting it.

---

## The Four Layers

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. AGENTS.md                                                     │
│    Scope: whole repo · Read by: coding agent + other agentic     │
│    tools (open standard, not Copilot-exclusive)                   │
├─────────────────────────────────────────────────────────────────┤
│ 2. .github/copilot-instructions.md                                │
│    Scope: whole repo · Read by: ALL Copilot surfaces              │
│    (Chat, Agent Mode, coding agent, code review)                  │
├─────────────────────────────────────────────────────────────────┤
│ 3. .github/instructions/*.instructions.md                         │
│    Scope: files matching an `applyTo` glob · Read by: Copilot     │
│    Chat/Agent Mode/coding agent, only for matching files          │
├─────────────────────────────────────────────────────────────────┤
│ 4. .chatmode.md files + personal/IDE settings                     │
│    Scope: only when that chat mode is explicitly selected, or     │
│    only for you (personal instructions in IDE settings)           │
└─────────────────────────────────────────────────────────────────┘
```

They are **additive, not exclusive** — Copilot combines all applicable
layers for a given request. More specific layers add detail on top of
general ones; they don't need to repeat what's already stated globally.

---

## Layer 1: `AGENTS.md`

- Open, tool-agnostic standard, not a GitHub/Copilot invention
- Read by the coding agent as environment/setup/convention guidance
- Good home for: setup commands, directory map, "how an agent should work
  here" — things useful to *any* coding agent, not just Copilot
- See the working example at the repo root: [`../../AGENTS.md`](../../AGENTS.md)

## Layer 2: `.github/copilot-instructions.md`

- Copilot-specific, always active, repo-wide
- Good home for: code quality standards, security rules, response format
  preferences — anything you want **every** Copilot interaction to respect
- See the working example: [`../../.github/copilot-instructions.md`](../../.github/copilot-instructions.md)

## Layer 3: `.github/instructions/*.instructions.md`

This is the newest and most surgical layer. Each file uses YAML frontmatter
with an `applyTo` glob to scope itself to specific paths:

```markdown
---
applyTo: "**/*.test.ts,**/*.spec.ts"
---

When writing or editing tests in this repo:
- Use `describe`/`it`, not `test()`
- Always include an "edge cases" describe block
- Mock external HTTP calls with the existing `mockFetch` helper — never
  call real network in unit tests
```

Why this matters: your Python rules shouldn't leak into your React files.
Path-specific instructions let you say "when touching `**/*.py`, follow
these conventions" without polluting the global file with irrelevant
detail. See the working examples in this repo:
- `.github/instructions/python.instructions.md` (`applyTo: "**/*.py"`)
- `.github/instructions/java.instructions.md` (`applyTo: "**/*.java"`)
- `.github/instructions/react.instructions.md` (`applyTo: "**/*.tsx,**/*.ts"`)

You can have **as many of these as you need** — one per language, per
module, per team convention. Copilot applies every file whose `applyTo`
glob matches the file(s) in play.

## Layer 4: Chat Modes & Personal Settings

- `.chatmode.md` — only active when that mode is explicitly selected (see
  [../agents/03-custom-chat-modes.md](../agents/03-custom-chat-modes.md))
- Personal instructions (IDE settings, e.g. VS Code
  `github.copilot.chat.codeGeneration.instructions`) — scoped to you alone,
  useful for individual preferences that shouldn't be forced on the team

---

## How This Repo Maps to the Hierarchy

```
AGENTS.md                                       ← Layer 1
.github/copilot-instructions.md                 ← Layer 2
.github/instructions/python.instructions.md     ← Layer 3 (applyTo: **/*.py)
.github/instructions/java.instructions.md       ← Layer 3 (applyTo: **/*.java)
.github/instructions/react.instructions.md      ← Layer 3 (applyTo: **/*.tsx,**/*.ts)
.github/chatmodes/architect.chatmode.md         ← Layer 4
.github/chatmodes/reviewer.chatmode.md          ← Layer 4
copilot-instructions/**                          ← Human-readable curriculum
                                                   these config files are
                                                   TAUGHT from, not read by
                                                   Copilot itself
```

Note the distinction: `copilot-instructions/` (this directory tree) is our
**course material** explaining the system. The actual machine-read
configuration lives in `.github/` and `AGENTS.md` at the repo root — that's
what Copilot itself parses.

---

## Conflict Resolution: What Wins?

There's no single silent "override" — Copilot combines all applicable
context. In practice:

1. **More specific beats more general** when they'd otherwise contradict
   (a path-specific rule for `*.test.ts` about mocking style takes
   precedence over a vague global statement about tests, for that file)
2. **Chat mode instructions are scoped to that session** — they add
   task-specific constraints on top of the standing repo instructions,
   they don't erase them
3. **Keep them non-contradictory by design** — the real fix for conflicts
   isn't a precedence rule to memorize, it's writing instructions that
   don't fight each other in the first place

---

## Exercise: Audit Your Own Repo

1. Does your repo have `AGENTS.md`? `.github/copilot-instructions.md`?
2. List every distinct convention your team enforces in code review
   (naming, error handling, test structure, commit style)
3. For each, decide: global (Layer 2) or path-specific (Layer 3)?

**Assignment**: Migrate 5 conventions currently only enforced by human
reviewers into the appropriate instruction layer. Re-run Copilot code
review (see [../agents/06-code-review.md](../agents/06-code-review.md)) on
a past PR and check whether it now catches what it missed before.
