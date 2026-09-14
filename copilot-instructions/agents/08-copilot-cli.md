# Copilot CLI

## Hierarchy Level: Agentic Features

Copilot is also available as a **terminal-native agent**, for tasks better
done outside an editor: shell one-liners, git operations, and running
agentic tasks directly against your local filesystem from the command line.

---

## Two Related but Different Tools

| Tool | What It Does |
|------|--------------|
| `gh copilot suggest` / `gh copilot explain` | GitHub CLI extension: suggests/explains shell commands |
| Standalone `copilot` CLI agent | Full agentic terminal session — reads/edits files, runs commands, in a chat-like loop, similar to Agent Mode but without an IDE |

### `gh copilot` — Command Suggestion & Explanation

```bash
# Install once
gh extension install github/gh-copilot

# Ask for a shell command in plain English
gh copilot suggest "find all files larger than 100MB modified in the last week"

# Understand a command you don't recognize
gh copilot explain "find . -mtime -7 -size +100M"
```

This is narrow and safe: it suggests or explains, it does not execute
anything on your behalf.

### Standalone `copilot` Agent — Terminal Agent Mode

```bash
copilot
# Drops into an interactive agentic session in the current directory
```

From here you can give it the same kind of goal-oriented prompts as Agent
Mode ("run the test suite and fix any failing tests"), and it will read
files, propose edits, and run commands — with the same kind of
confirm-before-run guardrails as IDE Agent Mode. This is useful for:

- Headless/remote environments without a full IDE
- Scripting agentic workflows into CI or local automation
- Developers who prefer terminal-first workflows

---

## Respecting the Same Instruction Hierarchy

The CLI agent reads the same `AGENTS.md` and `.github/copilot-instructions.md`
files as Agent Mode and the coding agent — consistency across every surface
is the point of that hierarchy. See
[../instructions-files/path-specific-instructions.md](../instructions-files/path-specific-instructions.md).

---

## When to Reach for the CLI Instead of the IDE

| Use CLI When | Use IDE Agent Mode When |
|--------------|--------------------------|
| You're already in a terminal workflow (SSH, remote box) | You're actively editing and want inline diffs |
| Quick one-off command help | Multi-file feature work needing visual diff review |
| Scripting/automation contexts | Exploratory, conversational iteration |

---

## Exercise: CLI Fluency

1. Use `gh copilot suggest` for 5 shell tasks you'd normally look up
   (tar a directory excluding node_modules, find and kill a process on a
   port, etc.)
2. Use the standalone `copilot` agent to run: "add a `.gitignore` entry for
   build artifacts and remove any already-tracked build files from git"

**Assignment**: Document your team's decision rule for CLI-agent vs.
IDE-Agent-Mode vs. coding-agent, based on the comparison tables across this
whole `agents/` directory. This becomes onboarding material for new hires.
