# Copilot Code Review

## Hierarchy Level: Agentic Features

Copilot can act as an **automated first-pass reviewer** on pull requests —
either requested manually or configured to run automatically — leaving
inline comments the same way a human reviewer would, before a person ever
looks at the diff.

---

## Ways to Trigger a Review

| Method | When It Runs |
|--------|--------------|
| Manual: click "Reviewers" → Copilot on a PR | On demand, once |
| `@copilot review` comment on a PR | On demand, whenever asked |
| Org/repo setting: automatic review on every PR | Every PR, automatically |
| Coding agent PRs | Often reviewed automatically as part of the loop |

---

## What It Actually Checks

Copilot code review focuses on the diff, not the whole codebase, and is
tuned toward:

- **Bugs**: logic errors, off-by-one, null/undefined handling, unhandled
  exceptions
- **Security**: obvious injection risks, secrets accidentally committed,
  unsafe deserialization
- **Consistency**: deviations from patterns visible elsewhere in the repo
  (helped enormously by a good `.github/copilot-instructions.md`)
- **Test coverage gaps**: new logic without corresponding tests

It is **not** a substitute for architectural review — it reviews the diff
in front of it, not whether the diff is the right thing to build.

---

## Making Copilot Review Better: Feed It Context

Review quality scales directly with how much project context exists:

```
Better reviews come from:
├── .github/copilot-instructions.md      (what "good" looks like here)
├── .github/instructions/*.instructions.md  (per-area conventions)
├── Existing tests in the same area       (shows expected behavior)
└── A clear PR description                (what problem this solves)
```

A repo with no instructions gets generic style feedback. A repo with clear,
specific instructions gets reviews that catch violations of *your*
conventions, not just generic best practices.

---

## Responding to Copilot's Review Comments

Two paths:

1. **Fix it yourself** — treat the comment like any reviewer's, push a
   commit
2. **Ask Copilot to fix it** — reply `@copilot please fix this` on the
   comment thread; if the PR was opened by the coding agent, it will push
   a follow-up commit addressing the specific comment

Always **evaluate before accepting**: Copilot review comments are
suggestions, not mandates. A comment flagging a "missing null check" might
be correct — or might not understand that the value is guaranteed
non-null upstream. Use judgment.

---

## Setting Up Automatic Review (Org/Repo Level)

Repo Settings → Rules → Rulesets → add a rule requiring Copilot as a
reviewer on PRs targeting protected branches. Combine with required human
approval so Copilot's review is a **gate before** human review, not a
replacement for it.

```
Recommended branch protection:
┌─────────────────────────────────────────────┐
│ 1. PR opened                                 │
│ 2. Copilot auto-review runs (fast feedback)  │
│ 3. Author addresses Copilot's comments       │
│ 4. Human reviewer reviews (higher signal now)│
│ 5. Merge                                     │
└─────────────────────────────────────────────┘
```

---

## Exercise: Calibrate on Your Own Repo

1. Open 5 recent merged PRs in a repo you own
2. Request a Copilot review on each (even though merged, you can often
   still request review, or open a fresh test PR with the same diff)
3. For each comment: mark it "would have caught a real bug", "correct but
   minor", or "not applicable/wrong"

**Assignment**: Based on the calibration exercise, write (or update) your
`.github/copilot-instructions.md` to close the biggest gap you found —
e.g., if Copilot kept missing a project-specific error-handling
convention, state that convention explicitly.
