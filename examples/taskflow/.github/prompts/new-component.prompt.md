---
mode: agent
description: "Add a React component with typed props and a Vitest suite"
---

Add a new React component to the TaskFlow frontend.

Ask me for these if I have not already given them:
- What it renders and what it is for
- Which data it needs and which callbacks it raises
- Whether it needs new API data (if so, we do the hook first)

Then implement, in this exact order:

1. **Types** — if the backend supplies new data, add it to
   `frontend/src/types/task.ts` first, matching the Java DTO exactly.
2. **API** — if a new call is needed, add a typed function to
   `frontend/src/api/tasks.ts`.
3. **Hook** — any fetching or reusable logic goes in `frontend/src/hooks/`,
   with abort-on-unmount and abort-on-dependency-change (copy the pattern in
   `useTasks.ts`, including the serialized-dependency trick).
4. **Component** — in `frontend/src/components/`:
   - an `interface XProps` with a doc comment on anything non-obvious
   - presentational only: **no `fetch`, no `api/` import**
   - explicit loading and empty states where it renders a collection
   - accessible markup: real labels, `aria-invalid` / `aria-describedby` on
     errors, `role`/`aria-live` where content changes
   - render server-derived state (`allowedTransitions`, `overdue`) rather than
     re-deriving it
5. **Wire it up** in `App.tsx`, which owns the state.
6. **Test** — a Vitest + React Testing Library suite covering render, each
   interaction, the empty/edge cases, and the disabled state. Query by role or
   label. No jest-dom matchers.

Then run `cd frontend && npm test && npm run build` and report both results.

Follow `.github/instructions/frontend-react.instructions.md`. `strict` and
`noUncheckedIndexedAccess` are on — handle `T | undefined`, never cast it away.
