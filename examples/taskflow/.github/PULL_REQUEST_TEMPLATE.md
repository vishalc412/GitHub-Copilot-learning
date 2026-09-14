## What and why

<!-- What changed, and the reason. Link the issue. -->

## Verification

<!-- Paste the actual summary lines, not "tests pass". -->

```
backend:  Tests run: __, Failures: 0, Errors: 0
frontend: Tests __ passed (__)
build:    built in __
```

## Checklist

- [ ] Business logic is in the domain or service layer, not a controller or component
- [ ] Status/lifecycle logic is only on `TaskStatus`
- [ ] `PUT /tasks/{id}` still cannot change status
- [ ] DTO changes mirrored in `frontend/src/types/task.ts`
- [ ] New behaviour has a test that fails without the change
- [ ] No new dependency (or justified below)
- [ ] No secrets or credentials added

## Copilot usage

<!-- Optional but useful: which surface did the work (inline / Chat / Agent Mode /
     coding agent), which prompt file, and anything the instructions should have
     caught but did not — that is a signal to improve .github/instructions/. -->
