---
applyTo: "**/*.tsx,**/*.ts"
---

# React/TypeScript-specific instructions

Follow `copilot-instructions/react/react-rules.md` for full detail. Key
points Copilot must always apply to `.ts`/`.tsx` files in this repo:

- Functional components only, no class components
- No `any` — use `unknown` with a type guard if the type is genuinely
  unclear
- Every component prop set gets an explicit `interface`, not inline types
- Custom hooks for any logic reused across 2+ components
- New components need a colocated test using React Testing Library
