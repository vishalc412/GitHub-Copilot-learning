# GitHub Copilot Complete Tutorial

## Instructor-Led Learning Guide

**Version**: 2.0
**Difficulty**: Beginner to Expert
**Duration**: 7 Weeks (includes agentic Copilot: Agent Mode, coding agent, MCP, multi-agent workflows)

---

## How to Use This Tutorial

This tutorial is structured as a progressive learning system:

1. **Read** the instruction section
2. **Practice** the exercises
3. **Complete** the assignments
4. **Build** the projects
5. **Take** the assessments

---

## Prerequisites

- Basic programming knowledge (any language)
- VS Code or JetBrains IDE installed
- GitHub account with Copilot subscription
- Willingness to learn and experiment

---

# WEEK 1: FOUNDATIONS

## Day 1-2: Setup and First Steps

### Learning Objectives
- Install and configure GitHub Copilot
- Understand how Copilot generates code
- Accept your first suggestions

### Instruction

#### Step 1: Install Copilot

**VS Code:**
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search "GitHub Copilot"
4. Click Install
5. Sign in with GitHub

**Verify Installation:**
Create a file `test.py` and type:
```python
# Function to calculate fibonacci
```
If Copilot suggests code, you're ready!

#### Step 2: Understanding the Interface

```
┌─────────────────────────────────────────────┐
│  Your Code                                  │
│                                             │
│  # Comment here         ← You write this    │
│  def function_name():   ← You start this    │
│      pass               ← Copilot suggests  │
│                           (grayed out)       │
│                                             │
│  Press Tab to accept                        │
│  Press Esc to reject                        │
│  Press Alt+] for next suggestion            │
└─────────────────────────────────────────────┘
```

### Exercise 1.1: First Generations

Create a new file and generate these functions:

```python
# 1. Function to add two numbers
# (Let Copilot complete, then accept)

# 2. Function to check if a string is palindrome
# (Let Copilot complete, then accept)

# 3. Function to find the largest number in a list
# (Let Copilot complete, then accept)
```

### Exercise 1.2: Multiple Suggestions

For each prompt below, cycle through at least 3 suggestions (Alt+] / Option+]):

```python
# Sort a list of dictionaries by a specific key

# Validate an email address

# Convert temperature from Celsius to Fahrenheit
```

**Document which suggestion you chose and why.**

### Assignment 1: Generation Log

Generate 20 different functions using comments. For each:
- Write the comment you used
- Note the suggestion quality (1-5)
- Record if you accepted or modified it

**Deliverable**: A table with 20 entries

---

## Day 3-4: The Art of Prompting

### Learning Objectives
- Write effective comments for code generation
- Understand context importance
- Use the signature-first approach

### Instruction

#### The Prompt Quality Spectrum

```
LOW QUALITY                    HIGH QUALITY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►

"do something"      →    "Function to validate..."
                         "...user registration data..."
                         "...with email format check,..."
                         "...password strength rules,..."
                         "...returns ValidationResult"
```

#### The Five Elements of Great Prompts

1. **Action**: What to do (validate, calculate, transform)
2. **Subject**: What to act on (user, order, data)
3. **Details**: Specific requirements
4. **Constraints**: Limits and rules
5. **Output**: Expected return

### Exercise 2.1: Prompt Improvement

Improve these prompts:

```python
# BAD: "process data"
# BETTER: Write your improved version
# BEST: Write your best version

# BAD: "send email"
# BETTER: ?
# BEST: ?

# BAD: "check user"
# BETTER: ?
# BEST: ?
```

### Exercise 2.2: Signature-First Approach

Complete these function signatures, then let Copilot implement:

```python
from typing import List, Dict, Optional
from datetime import datetime

# Define signature first, then add docstring
def calculate_order_total(
    items: List[Dict[str, float]],
    discount_code: Optional[str] = None,
    tax_rate: float = 0.08
) -> Dict[str, float]:
    """
    Calculate order total with itemized breakdown.

    Returns:
        {
            'subtotal': float,
            'discount': float,
            'tax': float,
            'total': float
        }
    """
    # Let Copilot complete

def filter_active_users(
    users: List[Dict],
    min_activity_days: int = 30,
    include_admins: bool = True
) -> List[Dict]:
    """Filter users based on activity criteria."""
    # Let Copilot complete
```

### Assignment 2: Prompt Patterns Catalog

Create 10 effective prompts for each category:
- Data processing (10)
- API operations (10)
- String manipulation (10)
- File operations (10)
- Error handling (10)

**Deliverable**: 50 documented prompts with quality ratings

---

## Day 5: Context and File Structure

### Learning Objectives
- Understand how Copilot uses context
- Organize files for better suggestions
- Use related files effectively

### Instruction

#### Context Sources

```
┌──────────────────────────────────────────┐
│            CONTEXT SOURCES               │
├──────────────────────────────────────────┤
│ 1. Current file         (highest weight) │
│ 2. Open tabs            (high weight)    │
│ 3. Imports              (high weight)    │
│ 4. Recent edits         (medium weight)  │
│ 5. Project structure    (low weight)     │
└──────────────────────────────────────────┘
```

### Exercise 3.1: Context Experiment

1. Close all files except one
2. Generate a User class
3. Open a file with existing User class
4. Generate another User class
5. Compare the results

### Exercise 3.2: Project Setup

Create this file structure:
```
my_project/
├── models/
│   ├── __init__.py
│   └── user.py
├── services/
│   └── user_service.py
├── repositories/
│   └── user_repository.py
└── tests/
    └── test_user.py
```

With `models/user.py` open, create the service. Observe how Copilot uses the model context.

### Assignment 3: Context Analysis

Document how context affects suggestions:
1. Generate the same function in 5 different contexts
2. Record each variation
3. Analyze what caused differences

**Deliverable**: Report with screenshots and analysis

---

# WEEK 2: LANGUAGE MASTERY

## Day 1-2: Python with Copilot

### Learning Objectives
- Master Python-specific Copilot patterns
- Generate Pythonic code
- Create complete Python modules

### Instruction

See: `copilot-instructions/python/python-rules.md`

### Exercise 4.1: Python Patterns

Generate each pattern:

```python
# 1. Dataclass with validation
@dataclass
class Product:
    """Product with price validation."""
    # Let Copilot complete

# 2. Context manager for database connection
@contextmanager
def database_connection(connection_string: str):
    """Context manager for database transactions."""
    # Let Copilot complete

# 3. Async function for API call
async def fetch_user_data(user_id: int) -> Dict:
    """Fetch user data from external API with retry."""
    # Let Copilot complete

# 4. Decorator for timing
def timing_decorator(func):
    """Decorator to measure and log function execution time."""
    # Let Copilot complete
```

### Exercise 4.2: FastAPI Generation

Generate a complete REST API:

```python
# Create FastAPI CRUD endpoints for a Blog Post entity
# Fields: id, title, content, author_id, created_at, updated_at
# Include: validation, error handling, pagination
```

### Assignment 4: Python Project

Build a complete Python module:
- User management service
- Repository with database operations
- REST API with FastAPI
- Unit tests with pytest
- 80%+ Copilot-generated code

**Deliverable**: Working project on GitHub

---

## Day 3-4: Java with Copilot

### Learning Objectives
- Master Java-specific patterns
- Generate Spring Boot applications
- Create enterprise-grade code

### Instruction

See: `copilot-instructions/java/java-rules.md`

### Exercise 5.1: Java Patterns

Generate each pattern:

```java
// 1. Builder pattern for Order class
public class Order {
    // Fields: id, customerId, items, total, status, createdAt
    // Let Copilot generate builder
}

// 2. Spring Boot REST Controller
@RestController
@RequestMapping("/api/products")
public class ProductController {
    // CRUD endpoints with validation
    // Let Copilot complete
}

// 3. JPA Repository with custom queries
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom queries for orders by customer, date range, status
    // Let Copilot complete
}

// 4. Service with transactions
@Service
@Transactional
public class OrderService {
    // Order processing with inventory check
    // Let Copilot complete
}
```

### Assignment 5: Java Project

Build a Spring Boot microservice:
- Entity models with JPA
- Repository layer
- Service layer with transactions
- REST controllers with validation
- Unit and integration tests

**Deliverable**: Working Spring Boot project

---

## Day 5: React/TypeScript with Copilot

### Learning Objectives
- Master React patterns
- Generate TypeScript components
- Create custom hooks

### Instruction

See: `copilot-instructions/react/react-rules.md`

### Exercise 6.1: React Patterns

Generate each pattern:

```typescript
// 1. Typed functional component
interface UserCardProps {
  user: User;
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
}

export const UserCard: React.FC<UserCardProps> = ({ user, onEdit, onDelete }) => {
  // Let Copilot complete
};

// 2. Custom hook for form handling
function useForm<T>(initialValues: T, validate: (values: T) => Errors<T>) {
  // Let Copilot complete
}

// 3. Context with reducer
interface AppState {
  user: User | null;
  theme: 'light' | 'dark';
  notifications: Notification[];
}

// Let Copilot generate context, reducer, provider, hook
```

### Assignment 6: React Project

Build a React application:
- Component library (5+ components)
- Custom hooks (3+ hooks)
- State management with Context
- Form handling with validation
- Tests with React Testing Library

**Deliverable**: Working React application

---

# WEEK 3-4: ADVANCED PATTERNS

## Testing Mastery

### Learning Objectives
- Generate comprehensive tests
- Use TDD with Copilot
- Create test fixtures and mocks

### Exercise 7.1: TDD Cycle

Practice the Red-Green-Refactor cycle:

```python
# Step 1: Write failing tests first
class TestShoppingCart:
    def test_add_item(self):
        cart = ShoppingCart()
        cart.add_item(Product("Widget", 10.00))
        assert len(cart.items) == 1

    def test_remove_item(self):
        # Write more tests

    def test_calculate_total(self):
        # Write more tests

    def test_apply_discount(self):
        # Write more tests

# Step 2: Let Copilot implement ShoppingCart
class ShoppingCart:
    """Shopping cart with discount support."""
    # Copilot implements based on tests
```

### Assignment 7: Test Suite

Create a comprehensive test suite:
- 50+ unit tests
- 10+ integration tests
- Mocking examples
- Fixture usage
- Edge case coverage

---

## Design Patterns

### Learning Objectives
- Implement all major patterns
- Recognize when to use each
- Combine patterns effectively

### Exercise 8.1: Pattern Implementation

Implement each pattern with Copilot:

1. **Creational Patterns**
   - Singleton
   - Factory
   - Builder
   - Prototype

2. **Structural Patterns**
   - Adapter
   - Decorator
   - Facade
   - Proxy

3. **Behavioral Patterns**
   - Strategy
   - Observer
   - Command
   - State

### Assignment 8: Pattern Catalog

Create a pattern catalog with:
- Implementation in 3 languages
- Use case documentation
- Comparison of approaches
- Real-world examples

---

# WEEK 5: AGENTIC COPILOT & MULTI-AGENT WORKFLOWS

## Day 1: Agent Mode

### Learning Objectives
- Understand the difference between Ask, Edit, and Agent modes
- Write goal-oriented prompts (not step-by-step instructions)
- Configure guardrails (terminal approval, file review)

### Instruction
See: [`copilot-instructions/agents/01-agent-mode.md`](copilot-instructions/agents/01-agent-mode.md)

### Exercise 9.1: Your First Agent Mode Task

In a scratch repo, give Agent Mode this goal (don't break it into steps —
let the agent plan):

```
Context: The createOrder function accepts unvalidated input.
Goal: Add input validation using the same validation library already
      used elsewhere in the project.
Constraints: Don't change the function signature.
Definition of done: Add tests covering at least 3 invalid-input cases;
      all tests pass.
```

Record: which files it opened before editing, whether it asked before
running commands, and whether it self-corrected after a failing test.

### Assignment 9: Agent Mode Task Log

Run 5 Agent Mode tasks of increasing complexity. For each: the prompt used,
files touched, commands run, and whether you had to intervene.

---

## Day 2: The Coding Agent

### Learning Objectives
- Assign a GitHub issue to Copilot's autonomous coding agent
- Write "agent-ready" issues with explicit acceptance criteria
- Configure `copilot-setup-steps.yml` for the agent's environment
- Review and iterate on agent-generated PRs

### Instruction
See: [`copilot-instructions/agents/02-coding-agent.md`](copilot-instructions/agents/02-coding-agent.md)

### Exercise 9.2: Delegate an Issue

Write an issue using the Problem → Expected behavior → Acceptance criteria
template, assign it to Copilot, and track time-to-first-commit and number
of review rounds before it's mergeable.

### Assignment 10: Agent-Ready Issue Writing

File 3 issues of increasing ambiguity. Document how issue clarity
correlates with resulting PR quality.

---

## Day 3: Custom Chat Modes & Code Review

### Learning Objectives
- Build restricted-tool personas (Architect, Reviewer, Test Writer)
- Understand automated Copilot code review and how to calibrate it

### Instruction
See: [`copilot-instructions/agents/03-custom-chat-modes.md`](copilot-instructions/agents/03-custom-chat-modes.md)
and [`copilot-instructions/agents/06-code-review.md`](copilot-instructions/agents/06-code-review.md)

### Exercise 9.3: Ship a Team Persona

Create a custom chat mode for a role your team needs (e.g. "Migration
Assistant" scoped only to `migrations/`). Have a teammate use it and
collect feedback on the tool restrictions.

### Assignment 11: Calibrate Code Review

Request a Copilot review on 5 recent PRs. Mark each comment: would have
caught a real bug / correct but minor / not applicable. Update
`.github/copilot-instructions.md` to close the biggest gap found.

---

## Day 4: MCP & Extensions

### Learning Objectives
- Connect Agent Mode to an external tool via an MCP server
- Understand when to use MCP vs. a Copilot Extension
- Apply the security checklist for any new integration

### Instruction
See: [`copilot-instructions/agents/04-mcp-integration.md`](copilot-instructions/agents/04-mcp-integration.md)
and [`copilot-instructions/agents/05-extensions.md`](copilot-instructions/agents/05-extensions.md)

### Exercise 9.4: Connect an MCP Server

Register a local read-only database MCP server (see `.vscode/mcp.json` in
this repo for the config shape). Ask Agent Mode to write a query function
and observe whether it inspects the schema via the MCP tool before coding.

### Assignment 12: Integration Proposal

Identify one internal system your team wishes Copilot could access.
Propose whether it should be an MCP server or a full Extension, and
document the security review required before rollout.

---

## Day 5: Multi-Agent Workflows (Capstone)

### Learning Objectives
- Fan out independent tasks to multiple coding agent sessions in parallel
- Build a Plan → Build → Review pipeline using different chat mode personas
- Recognize and avoid the common failure modes of multi-agent work

### Instruction
See: [`copilot-instructions/agents/07-multi-agent-workflows.md`](copilot-instructions/agents/07-multi-agent-workflows.md)

### Exercise 9.5: Fan-Out Batch

Pick 3 small, independent, well-scoped issues. Write each with the Strong
Issue template, assign all 3 to Copilot at once, and time how long until
all 3 have reviewable draft PRs.

### Assignment 13: Design a Pipeline (Capstone)

Design a full Plan → Build → Review pipeline for a real feature in your
codebase:
1. Architect-mode plan for the feature
2. The resulting issue (with acceptance criteria) for the coding agent
3. A Reviewer-mode checklist to run before human review

Present the end-to-end flow to your team as a proposed standard workflow.

---

# WEEK 6-7: PROJECTS & CERTIFICATION

## Capstone Projects

### Project 1: Full-Stack Application (Required)

Build a complete application using Copilot:

**Requirements:**
- Backend: Python (FastAPI) or Java (Spring Boot)
- Frontend: React with TypeScript
- Database: PostgreSQL or MongoDB
- Tests: 80%+ coverage
- Documentation: Complete API docs

**Copilot Usage Requirements:**
- 70%+ code generated by Copilot
- Document all prompts used
- Track generation success rate

**Deliverable**: GitHub repository with documentation

---

### Project 2: Choose Your Own (Required)

Choose one:

**A. CLI Tool**
- Command-line application
- Multiple commands
- Configuration handling
- Tests and documentation

**B. Data Pipeline**
- ETL process
- Data validation
- Error handling
- Monitoring

**C. API Integration**
- External API consumption
- Rate limiting
- Caching
- Error recovery

---

### Project 3: Agentic Workflow (Required for Expert Certification)

Build and document a complete agentic workflow on a real (or realistic
sandbox) repository:

**Requirements:**
- `AGENTS.md` and `.github/copilot-instructions.md` written for the repo
- At least 2 path-specific `.github/instructions/*.instructions.md` files
- At least 1 custom chat mode (`.github/chatmodes/*.chatmode.md`)
- `copilot-setup-steps.yml` configured and verified working
- At least 3 issues completed end-to-end by the coding agent
- One multi-agent fan-out batch (3+ parallel issues) documented
- One MCP server connected and used successfully in Agent Mode

**Deliverable**: Repository + a written retrospective covering what worked,
what failed, and what you'd change

---

## Certification Assessment

### Written Assessment (30 points)

1. Explain how Copilot uses context (5 points)
2. List 5 effective prompt patterns (5 points)
3. Describe the TDD workflow with Copilot (5 points)
4. Compare Copilot behavior across languages (5 points)
5. Identify issues in Copilot-generated code (5 points)
6. Design a prompt for a complex function (5 points)
7. Explain the 4-layer instructions hierarchy and when each applies, and
   contrast Agent Mode, the coding agent, and a custom chat mode (5 points)
   *(bonus — required only for Expert certification)*

### Practical Assessment (70 points)

1. **Generate Functions** (20 points)
   - Generate 10 functions from comments
   - Judged on: prompt quality, code quality, efficiency

2. **Build Feature** (25 points)
   - Build a complete feature in 30 minutes
   - Judged on: completeness, code quality, Copilot usage

3. **Debug & Improve** (15 points)
   - Fix issues in Copilot-generated code
   - Judged on: identification, fixes, explanations

4. **Teaching Demo** (10 points)
   - Explain Copilot to a beginner
   - Judged on: clarity, completeness, practical examples

---

## Certification Levels

### Level 1: Copilot User
- Complete Weeks 1-2
- Pass written assessment (60%+)
- Submit 1 project

### Level 2: Copilot Practitioner
- Complete Weeks 1-4
- Pass written assessment (75%+)
- Submit 2 projects
- 60%+ code generation rate

### Level 3: Copilot Agentic Practitioner
- Complete Weeks 1-5 (includes Agent Mode, coding agent, MCP, chat modes)
- Pass written assessment including question 7 (80%+)
- Submit Project 3 (Agentic Workflow)
- Successfully delegate at least 3 issues end-to-end to the coding agent

### Level 4: Copilot Expert
- Complete all 7 weeks
- Pass written assessment (90%+)
- Submit all 3 projects, including the Agentic Workflow retrospective
- 70%+ code generation rate
- Design and present a multi-agent pipeline (Assignment 13) to your team
- Teach another developer

---

## Resources

### Files in This Repository

```
AGENTS.md                              # Open-standard agent instructions
.github/copilot-instructions.md        # Root Copilot configuration
.github/instructions/*.instructions.md # Path-scoped instructions
.github/chatmodes/*.chatmode.md        # Custom chat personas
.github/workflows/copilot-setup-steps.yml  # Coding agent environment
.vscode/mcp.json                       # Example MCP server config

copilot-instructions/
├── python/python-rules.md             # Python-specific instructions
├── java/java-rules.md                 # Java-specific instructions
├── react/react-rules.md               # React-specific instructions
├── patterns/prompt-patterns.md        # Prompt pattern catalog
├── rules/copilot-mastery-rules.md     # Core rules and principles
├── instructions-files/
│   └── path-specific-instructions.md  # The full 4-layer hierarchy explained
└── agents/                            # Agentic Copilot (Weeks 5 + Expert cert)
    ├── 01-agent-mode.md
    ├── 02-coding-agent.md
    ├── 03-custom-chat-modes.md
    ├── 04-mcp-integration.md
    ├── 05-extensions.md
    ├── 06-code-review.md
    ├── 07-multi-agent-workflows.md
    └── 08-copilot-cli.md

training/                              # 3-day presentation-ready HTML deck
├── index.html
├── day1.html
├── day2.html
└── day3.html
```

### External Resources

- [GitHub Copilot Docs](https://docs.github.com/copilot)
- [VS Code Copilot](https://code.visualstudio.com/docs/copilot)
- [Copilot Best Practices](https://github.blog/developer-skills/github/how-to-write-better-prompts-for-github-copilot/)
- [Copilot Coding Agent Docs](https://docs.github.com/en/copilot/using-github-copilot/coding-agent/about-assigning-tasks-to-copilot)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [AGENTS.md Standard](https://agents.md/)

---

## Support

If you're stuck:
1. Review the relevant instruction file
2. Try different prompt patterns
3. Use Copilot Chat to ask questions
4. Check the examples in this repository

---

**Good luck on your Copilot mastery journey!**
