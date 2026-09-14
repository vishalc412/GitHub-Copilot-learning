# MCP Integration with Copilot

## Hierarchy Level: Agentic Features

**Model Context Protocol (MCP)** is an open standard for connecting AI
agents to external tools and data sources — databases, issue trackers,
browsers, internal APIs — through a common interface. Copilot's Agent Mode
(and the coding agent) can use any MCP server you register, dramatically
expanding what "the agent" can actually do beyond reading/editing files and
running local terminal commands.

---

## Why This Matters

Without MCP, Agent Mode's world is: your files + your terminal. With MCP,
Agent Mode's world can include:

```
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  Database    │   │  Issue        │   │  Internal    │
│  MCP server  │   │  tracker MCP  │   │  API MCP     │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       └──────────────────┼──────────────────┘
                           │
                  ┌────────▼────────┐
                  │  Copilot Agent   │
                  │  Mode / coding   │
                  │  agent           │
                  └──────────────────┘
```

Example: "Find the customer whose signup is failing, check their record in
the database, and fix the validation bug" — impossible without DB access;
trivial with a database MCP server registered.

---

## Configuring MCP Servers for Agent Mode (VS Code)

Create `.vscode/mcp.json` (see the working example in this repo):

```json
{
  "servers": {
    "postgres": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-postgres"],
      "env": {
        "DATABASE_URL": "${input:database_url}"
      }
    }
  },
  "inputs": [
    {
      "id": "database_url",
      "type": "promptString",
      "description": "Postgres connection string",
      "password": true
    }
  ]
}
```

Key points:
- `type: "stdio"` runs the server as a local subprocess; `"http"`/`"sse"`
  connect to a remote MCP server instead
- `inputs` prompts you for secrets at connect time instead of hardcoding
  them in the file — **never commit real credentials**
- Once configured, VS Code shows the server's tools in Agent Mode's tool
  picker; you can enable/disable individual tools per session

## Configuring MCP for the Coding Agent

The coding agent (cloud-hosted, see
[02-coding-agent.md](02-coding-agent.md)) supports MCP server configuration
through repository settings (Settings → Copilot → Coding agent → MCP
servers), using the same JSON shape. Because the coding agent runs in an
isolated environment without your local secrets, any credentials must be
provided as **repository/environment secrets**, referenced by name — never
inlined.

---

## Security Model

MCP servers can read and act on real systems, so treat each one as a
privilege grant:

| Risk | Mitigation |
|------|------------|
| Overly broad DB access | Use a read-only DB role/connection for MCP where possible |
| Secret leakage in config | Use `inputs`/secret references, never literal tokens in `mcp.json` |
| Unreviewed tool calls | Keep "confirm before running" on for write-capable tools |
| Untrusted third-party MCP servers | Only install servers from sources you trust; review their source if feasible |

---

## Common MCP Servers Worth Knowing

| Server | Gives the Agent |
|--------|------------------|
| Filesystem | Access beyond the current workspace root |
| GitHub | Issues, PRs, Actions runs (already partly native in Copilot) |
| Postgres/SQLite | Query and inspect a real database schema |
| Browser/Playwright | Navigate and read live web pages |
| Slack | Post messages, read channel context |
| Internal/custom | Whatever your org exposes via its own MCP server |

You can also **build your own MCP server** to expose internal tools (a
deploy script, a feature-flag service, a data warehouse) to Copilot — this
is how organizations extend Copilot with proprietary context safely,
without sending that data to a third party.

---

## Exercise: Connect a Local MCP Server

1. Register a local read-only SQLite MCP server against a sample database
2. Ask Agent Mode: "What columns does the `orders` table have, and write a
   function that queries orders by customer_id using our existing DB
   client pattern"
3. Observe: does it call the MCP tool to inspect the schema before writing
   code, or does it guess?

**Assignment**: Identify one internal system your team wishes Copilot could
"see" (a docs wiki, a ticket system, a service catalog). Prototype an MCP
server — or find an existing one — that exposes it, and document the
security review you'd need before rolling it out to the team.
