---
applyTo: "**/*.java"
---

# Java-specific instructions

Follow `copilot-instructions/java/java-rules.md` for full detail. Key
points Copilot must always apply to Java files in this repo:

- Constructor injection only — never field-level `@Autowired`
- JavaDoc on all public classes and methods
- Runtime (unchecked) exceptions for business logic errors, with a custom
  exception hierarchy — not checked exceptions
- Repository methods return `Optional<T>` for single-result lookups
- Every new service method needs a corresponding JUnit 5 + Mockito test
