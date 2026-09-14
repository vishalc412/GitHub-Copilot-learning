# Copilot Extensions

## Hierarchy Level: Agentic Features

**Copilot Extensions** integrate third-party tools directly into Copilot
Chat as **chat participants** — invoked with `@extension-name` — built on
top of GitHub Apps plus an agent that can respond to natural language.
Where MCP (previous file) is the open protocol for *you* to wire up tools,
Extensions are *published, discoverable integrations* other vendors (and
your own org) ship for everyone on a plan to install from the GitHub
Marketplace.

---

## Extensions vs. MCP vs. Agent Mode Tools — What's the Difference?

| | Agent Mode built-in tools | MCP servers | Copilot Extensions |
|---|---|---|---|
| **Scope** | File edit, search, terminal | Any external system you wire up | Published, installable integrations |
| **Distribution** | Built into the IDE | You configure per-project/workspace | GitHub Marketplace, org-wide install |
| **Invocation** | Automatic within Agent Mode | Automatic within Agent Mode once registered | Explicit `@extension-name` in chat |
| **Typical use** | Editing your code | Connecting to your infra | Third-party SaaS (monitoring, docs, PM tools) |

They're complementary: a mature setup often uses all three — Agent Mode for
editing, an MCP server for your internal database, and an Extension for
your observability vendor.

---

## Using an Extension

```
@sentry What's the top error in production for the checkout service
this week?

@your-docs-tool Summarize the API contract for the /orders endpoint
```

Extensions appear in the `@` picker in Copilot Chat once installed for your
org/account. Each one exposes its own natural-language interface — some
also support "skillsets" that let the extension's agent take actions (open
a ticket, acknowledge an alert) rather than just answering questions.

---

## Building an Internal Extension

For proprietary internal tools, you can build a **Copilot Extension** as a
GitHub App with an agent backend that:

1. Receives the user's chat message + conversation context via webhook
2. Calls your internal service (deploy system, feature flags, runbooks)
3. Streams a response back into Copilot Chat

This is the right choice over an MCP server when:
- You want **org-wide, centrally managed** distribution (not per-developer
  config files)
- The integration should be usable **outside the IDE too** (Extensions can
  also work in GitHub.com chat, mobile)
- You want a **branded, discoverable** entry in your org's Copilot chat

Choose an **MCP server** instead when the integration is project-scoped,
developer-configured, and doesn't need Marketplace-style distribution.

---

## Governance Checklist for Rolling Out Extensions

- [ ] Extension is from a verified/trusted publisher, or built in-house
- [ ] Data the extension can access is scoped to what's actually needed
- [ ] Org admin has reviewed and explicitly enabled the extension (not
      auto-available to everyone by default)
- [ ] Team has documented **when** to use it (e.g., "@sentry for prod
      incidents, not for local dev errors")

---

## Exercise: Extension Audit

List every Copilot Extension currently enabled in your org (Org Settings →
Copilot → Extensions). For each, document: what it's for, who uses it, and
whether access scope matches actual need.

**Assignment**: Propose one internal tool your team would benefit from
exposing as either an MCP server (project-local) or a full Extension
(org-wide) — justify the choice using the comparison table above.
