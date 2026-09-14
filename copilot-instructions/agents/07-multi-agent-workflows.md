# Multi-Agent Workflows with Copilot

## Hierarchy Level: Agentic Features (Capstone)

This file ties together every agentic surface covered in this directory
into **orchestration patterns** — how to run multiple Copilot agents
(or agent modes) together, in parallel or in a pipeline, to move faster
than any single agent session could.

Read first: [01-agent-mode.md](01-agent-mode.md),
[02-coding-agent.md](02-coding-agent.md),
[03-custom-chat-modes.md](03-custom-chat-modes.md).

---

## The Core Insight

A single Copilot Chat session is **one agent, one context, one task at a
time**. Real leverage comes from treating Copilot's different surfaces as
**specialized workers** you can run concurrently:

```
                     ┌─────────────────────────┐
                     │   YOU (orchestrator)      │
                     └────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
        ▼                         ▼                         ▼
┌───────────────┐       ┌───────────────┐        ┌───────────────┐
│ Coding agent   │       │ Coding agent   │        │ IDE Agent Mode │
│ (Issue #101)   │       │ (Issue #104)   │        │ (you, live)    │
│ Add rate limit │       │ Fix flaky test │        │ Design new     │
│ → draft PR     │       │ → draft PR     │        │ feature (WIP)  │
└───────────────┘       └───────────────┘        └───────────────┘
```

Each coding-agent task runs in its **own isolated environment and branch**
— they cannot collide with each other or with your local working copy.
This is what makes true parallelism safe.

---

## Pattern 1: Fan-Out Issue Delegation

**Use when**: You have a backlog of well-scoped, independent tasks (bug
fixes, dependency bumps, small refactors).

```
1. Write N issues, each following the "Strong Issue" template
   (see 02-coding-agent.md) — Problem, Expected behavior, Acceptance criteria
2. Assign all N to Copilot simultaneously
3. Each spins up its own environment + draft PR, in parallel
4. You triage the resulting PRs as they arrive — review, request changes,
   or merge
```

**Guardrail**: Only fan out tasks that are genuinely independent (don't
touch overlapping files/logic) — otherwise you'll get merge conflicts
between the resulting PRs, which defeats the purpose.

---

## Pattern 2: Pipeline (Plan → Build → Review)

**Use when**: A feature is too fuzzy to hand straight to the coding agent,
but you still want to minimize manual typing.

```
Stage 1 — Architect chat mode (read-only, plans)
    "Given this feature request, propose an implementation plan"
    → Output: a step-by-step plan, posted as the issue body

Stage 2 — Coding agent (executes the plan)
    Issue assigned to Copilot with the plan as acceptance criteria
    → Output: draft PR

Stage 3 — Reviewer chat mode (read-only, critiques)
    Run against the diff before requesting human review
    → Output: list of findings, addressed before human review starts
```

Each stage uses a **different, narrowly-scoped persona** (see
[03-custom-chat-modes.md](03-custom-chat-modes.md)) so no single agent
session is trying to plan, build, and self-review all at once — the same
reason human teams separate design review from code review.

---

## Pattern 3: Human-in-the-Loop Supervision

**Use when**: Tasks are important enough that unattended completion is
risky, but still mechanical enough to benefit from agent execution.

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐
│ Agent drafts │ →  │ You checkpoint│ →  │ Agent continues  │
│ first pass   │    │ (approve/    │    │ or stops here    │
│              │    │  redirect)   │    │                   │
└─────────────┘    └──────────────┘    └─────────────────┘
```

In IDE Agent Mode, this is just: reviewing the plan before letting it run
long, and reviewing diffs per-file instead of all-at-once. For the coding
agent, this is: reviewing the draft PR early and leaving comments rather
than waiting for a "final" PR.

---

## Pattern 4: Specialist Ensemble

**Use when**: A single task genuinely spans domains Copilot handles better
separately (e.g., DB migration + API change + frontend update).

```
Sub-task A → Coding agent, scoped to migrations/ only
Sub-task B → Coding agent, scoped to api/ only
Sub-task C → IDE Agent Mode, scoped to frontend/, run by you
             (kept local because it needs visual/manual verification)

→ Merge order matters: A before B before C
```

Split the parent issue into sub-issues with explicit dependency order
stated in each ("Depends on #101 — do not open PR until #101 is merged").

---

## What Makes Multi-Agent Workflows Fail

| Failure Mode | Root Cause | Fix |
|---|---|---|
| Merge conflicts between parallel PRs | Tasks weren't actually independent | Scope tasks to non-overlapping files before fanning out |
| Agent "completes" the wrong thing | Ambiguous issue, no acceptance criteria | Use the Strong Issue template every time |
| Reviewer bottleneck | Too many PRs land at once | Stagger delegation; batch review sessions |
| Inconsistent code style across agent PRs | No shared instructions | Strong `.github/copilot-instructions.md` + `AGENTS.md` shared by every surface |
| Silent scope creep | Agent "helpfully" touches unrelated files | State constraints explicitly ("don't touch X") |

---

## Exercise: Run a Fan-Out Batch

1. Pick 3 small, independent, well-scoped issues from your backlog
2. Write each using the Strong Issue template
3. Assign all 3 to Copilot at once
4. Time how long until all 3 have a reviewable draft PR
5. Review and merge in dependency-safe order

**Assignment (Capstone)**: Design a full pipeline (Pattern 2) for a real
feature in your codebase — write the Architect-mode plan, the resulting
issue, and a review checklist for the Reviewer-mode stage. Present the
end-to-end flow to your team as a proposed standard workflow.

---

## Summary: Choosing the Right Surface

| Situation | Use |
|---|---|
| Small, independent, well-defined tasks, want parallelism | **Fan-out coding agent** |
| Ambiguous feature needing a plan before code | **Architect mode → coding agent → reviewer mode pipeline** |
| High-stakes change, need tight control | **IDE Agent Mode, human-in-the-loop** |
| Cross-domain task with clear sub-boundaries | **Specialist ensemble, ordered merges** |
| Terminal-only environment | **Copilot CLI agent** (see [08-copilot-cli.md](08-copilot-cli.md)) |
