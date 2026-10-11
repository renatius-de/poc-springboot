---
name: code-quality-complexity
description: "Use for implementation work across languages; reduce complexity, prefer early exits, and keep logic readable and maintainable."
applyTo: "**"
---

## Scope

- Applies to all code, scripts, configuration, and automation files in this project.
- Use this rule for any implementation, refactor, or bug fix that changes runtime behavior or maintainability.
- This rule is limited to code structure and maintainability; it does not define OpenAPI contracts, README approval, or documentation language policy.

## Required behavior

- Reduce cyclomatic and cognitive complexity in all code you write or modify.
- Keep nesting shallow; prefer a maximum of two conditional levels before extracting a helper or returning early.
- Use guard clauses and early exits to handle invalid input and edge cases before deeper logic.
- Break complex behavior into small, single-responsibility functions or components with clear names.
- Prefer direct, declarative patterns over clever or over-abstracted solutions when the simpler option is clearer.
- Keep variable names, function names, and control flow explicit so the code is easy to reason about.
- Write English error messages that explain what failed, why it failed, and which context or input triggered the issue without leaking sensitive data.

## Do not

- Do not add unnecessary abstraction layers or indirection.
- Do not keep deeply nested conditionals when a guard clause or helper would reduce complexity.
- Do not use generic failure text such as "An error occurred" or "Invalid input" when a more specific message is available.
- Do not introduce unclear naming or mixed responsibilities in the same function or component.

## Quality standard

- Favor maintainability and readability over cleverness.
- Keep the logic easy for a future maintainer to trace in a single pass.
- Prefer the simplest correct implementation that still matches the project conventions.
