# GitHub Copilot Mastery System

## Complete Learning System for AI-Assisted Development

---

## Quick Navigation

| Resource | Description | Level |
|----------|-------------|-------|
| [COPILOT_TUTORIAL.md](COPILOT_TUTORIAL.md) | **START HERE** - Complete instructor-led tutorial | All |
| [training/](training/) | **3-day team training deck (HTML)** - ready to present | All |
| [AGENTS.md](AGENTS.md) | Open-standard agent instructions (coding agent + others) | Reference |
| [.github/copilot-instructions.md](.github/copilot-instructions.md) | Root-level Copilot configuration | Reference |
| [Path-Specific Instructions](copilot-instructions/instructions-files/path-specific-instructions.md) | Full 4-layer instruction hierarchy explained | Reference |
| [Python Rules](copilot-instructions/python/python-rules.md) | Python-specific guidelines | Intermediate |
| [Java Rules](copilot-instructions/java/java-rules.md) | Java-specific guidelines | Intermediate |
| [React Rules](copilot-instructions/react/react-rules.md) | React/TypeScript guidelines | Intermediate |
| [Prompt Patterns](copilot-instructions/patterns/prompt-patterns.md) | Effective prompt templates | All |
| [Mastery Rules](copilot-instructions/rules/copilot-mastery-rules.md) | Core rules and principles | Beginner |
| **Agentic Copilot** | | |
| [Agent Mode](copilot-instructions/agents/01-agent-mode.md) | Autonomous in-IDE editing | Intermediate |
| [Coding Agent](copilot-instructions/agents/02-coding-agent.md) | Issue → PR automation | Intermediate |
| [Custom Chat Modes](copilot-instructions/agents/03-custom-chat-modes.md) | Reusable agent personas | Intermediate |
| [MCP Integration](copilot-instructions/agents/04-mcp-integration.md) | Connect external tools/data | Advanced |
| [Extensions](copilot-instructions/agents/05-extensions.md) | Marketplace integrations | Advanced |
| [Code Review](copilot-instructions/agents/06-code-review.md) | Automated PR review | Intermediate |
| [Multi-Agent Workflows](copilot-instructions/agents/07-multi-agent-workflows.md) | Orchestration patterns | Expert |
| [Copilot CLI](copilot-instructions/agents/08-copilot-cli.md) | Terminal-native agent | Intermediate |

---

## Repository Structure

```
GitHub-Copilot-learning/
│
├── AGENTS.md                          # Open-standard agent instructions
├── COPILOT_TUTORIAL.md                # Main tutorial (START HERE)
├── README.md                          # This file
│
├── .github/
│   ├── copilot-instructions.md        # Root Copilot configuration
│   ├── instructions/                  # Path-scoped instructions (applyTo globs)
│   │   ├── python.instructions.md
│   │   ├── java.instructions.md
│   │   └── react.instructions.md
│   ├── chatmodes/                     # Custom Copilot Chat personas
│   │   ├── architect.chatmode.md
│   │   └── reviewer.chatmode.md
│   └── workflows/
│       └── copilot-setup-steps.yml    # Coding agent environment bootstrap
│
├── .vscode/
│   └── mcp.json                       # Example MCP server config for Agent Mode
│
├── copilot-instructions/
│   ├── python/python-rules.md         # Python-specific rules
│   ├── java/java-rules.md             # Java-specific rules
│   ├── react/react-rules.md           # React/TypeScript rules
│   ├── patterns/prompt-patterns.md    # Prompt pattern catalog
│   ├── rules/copilot-mastery-rules.md # Core mastery rules
│   ├── instructions-files/
│   │   └── path-specific-instructions.md  # Full 4-layer hierarchy explained
│   └── agents/                        # Agentic Copilot (2025-2026 features)
│       ├── 01-agent-mode.md
│       ├── 02-coding-agent.md
│       ├── 03-custom-chat-modes.md
│       ├── 04-mcp-integration.md
│       ├── 05-extensions.md
│       ├── 06-code-review.md
│       ├── 07-multi-agent-workflows.md
│       └── 08-copilot-cli.md
│
├── training/                          # 3-day team training (HTML deck)
│   ├── index.html                     # Landing page / agenda
│   ├── day1.html                      # Foundations & prompt engineering
│   ├── day2.html                      # Languages, testing, patterns
│   └── day3.html                      # Agents, MCP, multi-agent workflows
│
└── examples/
    ├── python/                        # Python code examples
    ├── java/                          # Java code examples
    └── react/                         # React code examples
```

---

## Hierarchical Instruction System

### Layer 0-4: What Copilot Actually Reads (machine-facing)

```
┌───────────────────────────────────────────────────────────────┐
│ Layer 1  AGENTS.md                                             │
│          Open standard · read by coding agent + other tools    │
├───────────────────────────────────────────────────────────────┤
│ Layer 2  .github/copilot-instructions.md                       │
│          Repo-wide · read by EVERY Copilot surface              │
├───────────────────────────────────────────────────────────────┤
│ Layer 3  .github/instructions/*.instructions.md                │
│          Path-scoped via `applyTo` glob (python/java/react)     │
├───────────────────────────────────────────────────────────────┤
│ Layer 4  .github/chatmodes/*.chatmode.md                       │
│          Active only when that persona is explicitly selected   │
└───────────────────────────────────────────────────────────────┘
```
Full explanation: [path-specific-instructions.md](copilot-instructions/instructions-files/path-specific-instructions.md)

### The Curriculum (human-facing)

```
                    ┌─────────────────────────────┐
                    │  copilot-mastery-rules.md    │
                    │  (Core Principles)           │
                    └─────────────┬───────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
          ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  python-rules    │    │   java-rules    │    │  react-rules    │
│  (Language)       │    │   (Language)     │    │  (Language)      │
└────────┬────────┘    └────────┬────────┘    └────────┬────────┘
         │                      │                      │
         └──────────────────────┼──────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                    ▼                       ▼
          ┌─────────────────┐    ┌─────────────────────┐
          │ prompt-patterns  │    │ agents/ (01-08)      │
          │ (Techniques)     │    │ Agent Mode, coding   │
          │                  │    │ agent, MCP, chat     │
          │                  │    │ modes, multi-agent   │
          └─────────────────┘    └─────────────────────┘
```

---

## Learning Paths

### Path 1: Complete Beginner (6 weeks)

```
Week 1: Foundations
├── Day 1-2: Setup & first generations
├── Day 3-4: Prompt engineering basics
└── Day 5: Context and file structure

Week 2: Language Focus
├── Day 1-2: Primary language (Python/Java/React)
├── Day 3-4: Patterns and practices
└── Day 5: Build first project

Week 3-4: Advanced Techniques
├── Testing with Copilot
├── Design patterns
├── Complex generations
└── Code review skills

Week 5-6: Projects & Certification
├── Capstone project
├── Choose-your-own project
└── Assessment
```

### Path 2: Experienced Developer (2-3 weeks)

```
Week 1: Quick Mastery
├── Day 1: Review mastery rules
├── Day 2-3: Language-specific patterns
├── Day 4-5: Advanced prompting

Week 2-3: Application
├── Apply to real projects
├── Build prompt library
└── Achieve certification
```

### Path 3: Team Lead (1 week + ongoing)

```
Week 1: Overview
├── Day 1-2: Core concepts
├── Day 3-4: Best practices
├── Day 5: Team guidelines

Ongoing: Enable team
├── Conduct training sessions
├── Review Copilot usage
└── Share patterns
```

---

## Key Documents Explained

### 1. `.github/copilot-instructions.md`
**Purpose**: Project-wide Copilot configuration
**Use**: Copilot reads this to understand project standards
**Contains**: Code quality standards, security rules, response format

### 2. Language Rules (`python-rules.md`, `java-rules.md`, `react-rules.md`)
**Purpose**: Language-specific best practices
**Use**: Reference when working in specific language
**Contains**: Patterns, examples, anti-patterns, testing

### 3. `prompt-patterns.md`
**Purpose**: Effective prompt templates
**Use**: Copy and adapt patterns for your needs
**Contains**: 10 proven patterns with examples in all languages

### 4. `copilot-mastery-rules.md`
**Purpose**: Core principles for Copilot mastery
**Use**: Learn the fundamentals
**Contains**: 10 rules with exercises and assignments

### 5. `COPILOT_TUTORIAL.md`
**Purpose**: Complete learning curriculum
**Use**: Follow week by week
**Contains**: Lessons, exercises, assignments, certification

### 6. `AGENTS.md` + `copilot-instructions/agents/*`
**Purpose**: The modern, agentic side of Copilot — Agent Mode, the
autonomous coding agent, MCP, custom chat modes, code review, extensions,
and multi-agent orchestration
**Use**: Reference once you've mastered inline generation and want to
delegate whole tasks, not just complete lines
**Contains**: 8 focused guides + working example config files
(`.github/instructions/`, `.github/chatmodes/`, `.vscode/mcp.json`,
`.github/workflows/copilot-setup-steps.yml`)

### 7. `training/` (HTML)
**Purpose**: A ready-to-present 3-day team training, one file per day
**Use**: Open `training/index.html` in a browser, present live or share
the link internally
**Contains**: Slide-style HTML covering foundations → language mastery →
agentic workflows, with live exercises embedded per session

---

## 3-Day Team Training (Ready to Present)

A condensed, presentation-ready version of this whole curriculum lives in
[`training/`](training/) as self-contained HTML — no build step, just open
in a browser or host as a static site.

| Day | Focus | File |
|-----|-------|------|
| 1 | Foundations: setup, prompt engineering, context, core rules | [training/day1.html](training/day1.html) |
| 2 | Language mastery: Python/Java/React patterns, TDD, design patterns | [training/day2.html](training/day2.html) |
| 3 | Agentic Copilot: Agent Mode, coding agent, MCP, multi-agent workflows | [training/day3.html](training/day3.html) |

Start at [`training/index.html`](training/index.html) for the agenda and
navigation between days.

---

## Quick Start (5 Minutes)

### Step 1: Install Copilot
```
1. Open VS Code
2. Install "GitHub Copilot" extension
3. Sign in with GitHub
```

### Step 2: Test Installation
```python
# Create test.py and type:
# Function to reverse a string
# Press Tab to accept suggestion
```

### Step 3: Begin Tutorial
Open [COPILOT_TUTORIAL.md](COPILOT_TUTORIAL.md) and follow Week 1, Day 1.

---

## Certification Levels

| Level | Requirements | Badge |
|-------|--------------|-------|
| **User** | Weeks 1-2 + 1 project | Copilot User |
| **Practitioner** | Weeks 1-4 + 2 projects + 60% generation | Copilot Practitioner |
| **Expert** | All weeks + 3 projects + 70% generation + teach | Copilot Expert |

---

## Assignments Overview

| Week | Assignment | Deliverable |
|------|------------|-------------|
| 1 | Generation Log | 20 function log |
| 1 | Prompt Catalog | 50 documented prompts |
| 1 | Context Analysis | Report with screenshots |
| 2 | Python Project | GitHub repository |
| 2 | Java Project | GitHub repository |
| 2 | React Project | GitHub repository |
| 3-4 | Test Suite | 60+ tests |
| 3-4 | Pattern Catalog | All patterns implemented |
| 5-6 | Capstone | Full-stack application |
| 5-6 | Choose Your Own | CLI/Pipeline/API project |

---

## Success Metrics

Track your progress:

```
Week 1: 30-40% code generation rate
Week 2: 40-50% code generation rate
Week 3: 50-60% code generation rate
Week 4+: 60-70%+ code generation rate
```

Quality indicators:
- Accepting suggestions without modification
- First suggestion is usually correct
- Context provides relevant suggestions
- Tests pass on first run

---

## Best Practices Summary

### DO
- Write detailed comments before code
- Use type hints everywhere
- Keep related files open
- Start with function signature
- Review before accepting
- Use Copilot Chat for explanations

### DON'T
- Accept code blindly
- Use vague variable names
- Skip type definitions
- Ignore security concerns
- Work in isolated files
- Forget to test generated code

---

## Contributing

To contribute to this learning system:
1. Fork the repository
2. Add examples or patterns
3. Improve documentation
4. Submit pull request

---

## Support

- Review relevant instruction file
- Use Copilot Chat: "Explain..."
- Check examples directory
- Open an issue on GitHub

---

## License

MIT License - Feel free to use and adapt for your learning needs.

---

**Start your journey**: [COPILOT_TUTORIAL.md](COPILOT_TUTORIAL.md)
