---
name: documentation-governance
description: "Use when evaluating whether documentation or README files should be updated; require explicit approval before modifying or creating README files."
applyTo: "**/README.md,**/*.md"
---

## Scope

- Applies to README files, documentation files, and any user-facing project guidance.
- Use this rule when project behavior, setup steps, configuration, or expectations change.
- This rule focuses on change control and approval for documentation. It does not define code quality, Java standards, or OpenAPI contract rules.

## Required behavior

- Review whether documentation needs updates whenever behavior, setup steps, configuration, or project expectations change.
- Keep documentation aligned with the repository’s current implementation.
- Make non-README documentation updates only when they are directly relevant and accurate.
- Treat README changes as a deliberate, approval-gated action, not a default side effect of code edits.
- Propose the documentation change briefly before editing a README file if the change is needed.

## Do not

- Do not create or modify README files without explicit user confirmation.
- Do not update documentation with speculative or unverified claims.
- Do not leave docs inconsistent with the actual repository behavior.
- Do not broaden documentation scope beyond the feature or change being discussed.

## Documentation standard

- Keep documentation concise, relevant, and maintainable.
- Prefer small, accurate updates over broad documentation rewrites.
- Follow the English-only repository standard in all documentation artifacts.
