---
applyTo: "frontend/**/*.ts,frontend/**/*.tsx"
---

# Frontend (React 19 / TypeScript) instructions

Applies to every TS/TSX file. Extends `.github/copilot-instructions.md`.

## Structure rules

```
types/       Mirrors the backend DTOs. Change here first.
api/         client.ts (fetch + ApiError) and one function per endpoint.
hooks/       All data fetching and reusable logic. use* naming.
components/  Presentational. Props in, callbacks out. No fetch calls.
App.tsx      Composition root: owns state, wires hooks to components.
```

**A component must never call `fetch` or an `api/` function directly.** It
receives data and callbacks as props, or uses a hook. This is what makes the
components testable without stubbing the network.

## Adding a feature — the order that works

1. Update `types/task.ts` to match the backend DTO
2. Add the endpoint function in `api/tasks.ts` with full types
3. Add or extend a hook in `hooks/`
4. Build the presentational component with a props `interface`
5. Wire it up in `App.tsx`
6. Add a Vitest test for the component and, if logic is non-trivial, the hook

## Required patterns

- **Props**: an exported or local `interface XProps`, one prop per line, with
  a doc comment on anything non-obvious.
- **Errors**: catch `ApiError`. Field-level messages come from
  `error.fieldErrors` and render next to the input. Network failures
  (`error.isNetworkError`) get an actionable message, not a raw string.
- **Loading and empty states are real UI.** A list that renders nothing while
  loading looks broken; one that renders nothing when empty looks failed.
- **Derived server state stays server-side.** Render transition buttons from
  `task.allowedTransitions`; do not re-derive what the backend already
  computed (`overdue`, allowed moves).
- **Abort in-flight requests** on unmount and dependency change. See
  `useTasks` for the pattern, including why the effect depends on a serialized
  copy of the filters.

## Testing

- React Testing Library, query by role/label, not by class or test id where a
  role exists.
- No `jest-dom` in this project — `getBy*` throws when absent, so existence is
  already asserted. Use plain assertions (`expect(el.value).toBe(...)`).
- Stub `fetch` with `vi.stubGlobal` per test; never globally.

## Do not

- Use `any`, non-null `!`, or a cast to silence the compiler
- Write class components or use `forwardRef` (React 19 passes `ref` as a prop)
- Put a `useEffect` without a dependency array
- Add a state-management library — hooks plus props are sufficient here
- Import `defineConfig` from `vite` in `vite.config.ts` (use `vitest/config`)
