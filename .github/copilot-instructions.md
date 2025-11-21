# GitHub Copilot Instructions

## Project Overview

This is a comprehensive GitHub Copilot learning system covering Python, Java, and React development. When assisting with this codebase, follow these guidelines.

## General Rules

### Code Quality Standards
- Always use type hints (Python), proper types (TypeScript), and strong typing (Java)
- Write comprehensive docstrings/JSDoc/JavaDoc for all public methods
- Follow language-specific naming conventions
- Include error handling and validation
- Write testable, modular code

### Documentation Requirements
- Every function/method needs documentation explaining purpose, parameters, and return values
- Include usage examples in docstrings where appropriate
- Add inline comments for complex logic only

### Security Best Practices
- Never hardcode secrets, API keys, or credentials
- Use environment variables for configuration
- Validate and sanitize all user inputs
- Follow OWASP security guidelines

## Language-Specific Guidelines

### Python
- Use Python 3.11+ features
- Follow PEP 8 style guide
- Use type hints for all function signatures
- Prefer dataclasses for data structures
- Use async/await for I/O operations
- Write tests with pytest

### Java
- Use Java 17+ features
- Follow Google Java Style Guide
- Use Spring Boot for web applications
- Implement builder pattern for complex objects
- Use Lombok where appropriate
- Write tests with JUnit 5 and Mockito

### React/TypeScript
- Use functional components with hooks
- Always use TypeScript (no plain JavaScript)
- Use React 18+ features
- Implement proper prop types with interfaces
- Use custom hooks for reusable logic
- Write tests with React Testing Library

## Copilot Interaction Patterns

### When Writing Comments for Code Generation
1. Start with the purpose: "Function to [action]"
2. Include parameters: "Parameters: [list]"
3. Specify return type: "Returns: [type and description]"
4. Add constraints: "Must handle: [edge cases]"

### Preferred Code Generation Style
- Generate complete, runnable code
- Include all necessary imports
- Add error handling by default
- Generate tests alongside implementation

## File Structure Conventions

```
project/
├── .github/
│   └── copilot-instructions.md      # This file
├── copilot-instructions/
│   ├── python/                       # Python-specific rules
│   ├── java/                         # Java-specific rules
│   ├── react/                        # React-specific rules
│   ├── patterns/                     # Design pattern templates
│   └── rules/                        # General coding rules
├── examples/
│   ├── python/                       # Python examples
│   ├── java/                         # Java examples
│   └── react/                        # React examples
└── docs/                             # Documentation
```

## Response Format Preferences

When generating code:
1. Start with imports
2. Add type definitions/interfaces
3. Write main implementation
4. Include usage examples as comments
5. Suggest tests to write

## Quality Checklist

Before accepting generated code, verify:
- [ ] Types are properly defined
- [ ] Error handling is present
- [ ] Documentation is complete
- [ ] Code follows project conventions
- [ ] Security considerations are addressed
