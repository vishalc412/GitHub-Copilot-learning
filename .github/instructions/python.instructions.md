---
applyTo: "**/*.py"
---

# Python-specific instructions

Follow `copilot-instructions/python/python-rules.md` for full detail. Key
points Copilot must always apply to Python files in this repo:

- Type hints on every function signature, no exceptions
- Google-style docstrings on all public functions/classes
- Never use bare `except:` — always name the exception type
- Prefer `dataclasses` over plain dicts for structured data
- Use `pathlib.Path`, not string path concatenation
- New code must include or update `pytest` tests in the matching `tests/`
  directory
