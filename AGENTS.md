# AGENTS.md

## What This File Is

`AGENTS.md` is an **open, tool-agnostic standard** (agents.md) for giving autonomous
coding agents instructions about a repository. Unlike `.github/copilot-instructions.md`
(which only GitHub Copilot reads), `AGENTS.md` is read by:

- **GitHub Copilot coding agent** (autonomous issue → PR agent)
- Other agentic coding tools that support the standard

Think of it as a **README for robots**: setup commands, conventions, and
guardrails an agent needs before it starts editing code unattended.

> This repo intentionally has BOTH `AGENTS.md` (this file) and
> `.github/copilot-instructions.md`. See
> [copilot-instructions/instructions-files/path-specific-instructions.md](copilot-instructions/instructions-files/path-specific-instructions.md)
> for how the two relate and which one wins when they overlap.

---

## Project Summary

This repository **is** a GitHub Copilot mastery curriculum — a set of
instruction files, tutorials, and examples for learning Copilot from
autocomplete basics through autonomous multi-agent workflows. There is no
runtime application to build or deploy; "the product" is the documentation
and example config files themselves.

## Setup Commands

There is no build step. This is a documentation-only repository.

```bash
# Verify markdown files render correctly (optional, if markdownlint installed)
npx markdownlint-cli2 "**/*.md"

# Verify example config files are valid
cat .vscode/mcp.json | python3 -m json.tool > /dev/null && echo "mcp.json OK"
```

If example projects are added under `examples/`, each subdirectory will
contain its own setup instructions in a local README.

## How an Agent Should Work in This Repo

1. **Read before writing.** Every new instruction file should slot into the
   existing hierarchy under `copilot-instructions/`. Don't create a
   competing top-level structure.
2. **Match the existing voice.** Files use second-person instructional tone,
   tables for quick reference, and runnable code blocks — not prose essays.
3. **Keep the hierarchy consistent.** When adding a new topic, update:
   - `README.md` (navigation table + structure diagram)
   - `COPILOT_TUTORIAL.md` (add exercises/assignments if it's a learning topic)
4. **Every code example must be accurate to real, current Copilot behavior.**
   Do not invent features. If uncertain whether a feature exists as
   described, say so explicitly rather than presenting speculation as fact.
5. **No secrets, ever.** All examples (MCP configs, workflow YAML) must use
   placeholder values or `${{ secrets.* }}` / environment variable
   references — never real tokens.

## Directory Map (for agents)

```
.
├── AGENTS.md                          # you are here
├── .github/
│   ├── copilot-instructions.md        # repo-wide Copilot instructions
│   ├── instructions/*.instructions.md # path-scoped instructions (applyTo globs)
│   ├── chatmodes/*.chatmode.md        # custom Copilot Chat personas
│   └── workflows/copilot-setup-steps.yml  # coding agent environment bootstrap
├── .vscode/mcp.json                   # example MCP server config for Agent Mode
├── copilot-instructions/              # the instructional content itself
│   ├── agents/                        # agentic Copilot features (Agent Mode, coding agent, MCP, etc.)
│   ├── python|java|react/             # language-specific rules
│   ├── patterns/                      # prompt engineering patterns
│   ├── rules/                         # core mastery principles
│   └── instructions-files/            # meta docs about the instruction system itself
├── COPILOT_TUTORIAL.md                # the full course, week by week
└── README.md                          # navigation hub
```

## Validation Before Finishing a Task

- [ ] New/edited `.md` files use correct relative links (test them)
- [ ] Any YAML/JSON example is syntactically valid
- [ ] `README.md` navigation table includes any new file
- [ ] No hardcoded secrets or personal tokens introduced
- [ ] Terminology matches GitHub's current product naming (e.g., "Copilot
      coding agent", not "autonomous Copilot bot")

## Commit Conventions

- Conventional, descriptive commit messages (`Add: ...`, `Update: ...`, `Fix: ...`)
- One logical change per commit where practical
- Do not rewrite history on shared branches
