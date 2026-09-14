# Copilot Coding Agent

## Hierarchy Level: Agentic Features

The **Copilot coding agent** is GitHub's autonomous, cloud-hosted agent that
takes a GitHub issue (or a chat request) and turns it into a **pull
request** — without you touching your local editor. It runs in an isolated,
ephemeral GitHub Actions-backed environment, has its own branch, and pushes
commits as it works.

This is fundamentally different from Agent Mode (previous file): Agent Mode
runs in *your* IDE against *your* working copy in real time. The coding
agent runs **remotely, asynchronously**, like assigning work to a teammate.

---

## How to Assign Work to the Coding Agent

### Option 1: Assign an Issue
1. Open a GitHub issue with a clear description
2. Assign it to **Copilot** (appears as an assignable "user")
3. Copilot comments that it's starting, opens a **draft PR**, and pushes
   commits as it makes progress
4. Review the draft PR like you would a junior teammate's — comment inline,
   Copilot pushes follow-up commits addressing feedback

### Option 2: Ask From Copilot Chat
```
@github Open a PR that adds retry logic with exponential backoff to the
HTTP client in src/utils/httpClient.ts. Cover it with unit tests.
```

### Option 3: From a Pull Request
Ask Copilot to address specific review comments, and it will push new
commits to the same PR.

---

## What the Coding Agent Reads Automatically

Before writing any code, the coding agent looks for:

| File | Purpose |
|------|---------|
| `AGENTS.md` | Repo-wide setup/conventions (open standard) |
| `.github/copilot-instructions.md` | Copilot-specific repo-wide rules |
| `.github/instructions/*.instructions.md` | Path-scoped rules matching files it touches |
| `.github/workflows/copilot-setup-steps.yml` | **Environment bootstrap** — install deps, set env vars, warm caches |

If your repo needs a specific Node/Python/Java version, private package
registry auth, or a database migration before tests can run, put it in
`copilot-setup-steps.yml` — see the working example at
`.github/workflows/copilot-setup-steps.yml` in this repo.

---

## The Environment Setup Workflow

```yaml
# .github/workflows/copilot-setup-steps.yml
name: "Copilot Setup Steps"

on: workflow_dispatch   # required trigger; GitHub also runs this
                         # automatically before the agent starts

jobs:
  copilot-setup-steps:
    runs-on: ubuntu-latest
    permissions:
      contents: read
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-node@v4
        with:
          node-version: "20"
          cache: "npm"

      - run: npm ci

      # Anything the agent needs pre-warmed goes here:
      # - database seed
      # - build artifacts
      # - private registry login (use secrets, never literal tokens)
```

The job name **must** be `copilot-setup-steps` for GitHub to recognize and
run it automatically as the agent's environment bootstrap.

---

## Writing Issues the Coding Agent Can Actually Complete

### Weak Issue
```
Title: Fix the bug
Body: Login is broken sometimes
```

### Strong Issue
```
Title: Fix race condition causing duplicate user creation on double-submit

Body:
## Problem
POST /api/users can be triggered twice if a user double-clicks submit
before the button disables, creating two rows with the same email.

## Expected behavior
The second request should return 409 Conflict, not create a duplicate.

## Suggested approach
Add a unique constraint on `users.email` at the DB level and handle the
constraint violation in the service layer with a friendly error.

## Acceptance criteria
- [ ] Duplicate email attempts return 409
- [ ] Existing user creation tests still pass
- [ ] New test reproduces the race condition
```

**Pattern**: Problem → Expected behavior → (optional) suggested approach →
explicit acceptance criteria as checkboxes. The coding agent uses the
checkboxes as its own definition of done.

---

## Reviewing Coding Agent PRs

Treat it exactly like reviewing a human contributor's PR:

1. **Read the PR description** — the agent summarizes what it did and why
2. **Check the CI status** — draft PRs run your existing checks
3. **Comment inline** on anything wrong — the agent picks up review
   comments and pushes fixes automatically
4. **Never merge without human review**, even if CI is green — the agent
   optimizes for "tests pass," not necessarily "this is the right design"

---

## Good Fits vs. Bad Fits

| Good Fit | Bad Fit |
|----------|---------|
| Well-scoped bug fixes with reproducible steps | Vague "improve performance" asks |
| Adding tests to existing, well-understood code | Novel architecture decisions |
| Mechanical refactors (rename, extract function) | Anything requiring a design discussion first |
| Dependency bumps + fixing resulting breakage | Security-sensitive auth/crypto changes without expert review |
| Documentation generation from existing code | Work needing access to systems outside the repo (no prod DB access) |

---

## Exercise: Delegate an Issue

1. Write an issue using the Strong Issue template above for a real (small)
   task in a sandbox repo
2. Assign it to Copilot
3. Track: time to first commit, number of review rounds before mergeable,
   whether `copilot-setup-steps.yml` needed adjusting

**Assignment**: File 3 issues of increasing ambiguity. Document how issue
clarity correlates with PR quality — this becomes team guidance for how to
write "agent-ready" issues.

---

## Multi-Agent Note

You can assign several issues to the coding agent **in parallel** — each
gets its own isolated environment and branch, so they don't collide. This
is the simplest form of "multi-agent workflow" in the Copilot ecosystem: run
N independent, well-scoped issues concurrently rather than one agent doing
everything sequentially. See
[07-multi-agent-workflows.md](07-multi-agent-workflows.md) for orchestration
patterns.
