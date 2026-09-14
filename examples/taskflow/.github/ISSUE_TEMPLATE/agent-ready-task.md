---
name: Agent-ready task
about: A task scoped tightly enough to assign to the Copilot coding agent
title: ""
labels: ["agent-ready"]
---

<!--
Assign this issue to Copilot once filled in.

Agent PR quality tracks issue clarity almost exactly. The acceptance criteria
below become the agent's definition of done, so make them checkable, not
aspirational.
-->

## Problem

<!-- Current behaviour, with reproduction steps or the exact failing input. -->

## Expected behaviour

<!-- What should happen instead. Be specific about status codes, messages, and UI states. -->

## Scope

**Touch only:**
<!-- e.g. backend/src/main/java/com/example/taskflow/service/, and its tests -->

**Do not touch:**
<!-- e.g. the TaskStatus enum, the public API contract, anything in frontend/ -->

## Suggested approach

<!-- Optional. A nudge, not a mandate — say "optional" if you are unsure. -->

## Acceptance criteria

- [ ] 
- [ ] 
- [ ] `cd backend && mvn test` passes (55+ tests)
- [ ] `cd frontend && npm test && npm run build` passes (32+ tests)
- [ ] New behaviour has a test that fails if the change is reverted
- [ ] If a DTO changed, `frontend/src/types/task.ts` changed in the same commit
