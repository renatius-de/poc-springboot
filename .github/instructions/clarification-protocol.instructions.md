---
name: clarification-protocol
description: "Use when requirements are ambiguous, missing, or uncertain; pause and ask a targeted clarifying question instead of guessing."
applyTo: "**"
---

## Scope

- Applies to all work when requirements, constraints, APIs, versions, or expectations are unclear or incomplete.
- This is a cross-cutting decision rule: it applies before implementation, before API design, and before documentation changes whenever a required fact is uncertain.
- It does not replace domain-specific rules such as Java quality, OpenAPI authoring, or documentation governance.

## Required behavior

- Treat this as the highest-priority rule for any task: minimize assumptions and seek clarification when necessary about requirements, APIs, data models, versions, constraints, or edge cases.
- Ask a concise, targeted clarifying question as soon as a required fact is missing, unclear, or ambiguous.
- Do not generate speculative code, guessed business logic, or placeholder behavior.
- If multiple valid implementation paths exist, present the options briefly and ask the user to choose.
- Once the missing details are confirmed, proceed directly with a focused, production-ready implementation.
- Preserve English wording in code comments and documentation and ask before changing README files unless the user explicitly requests that change.

## Do not

- Do not assume user intent, API contracts, or environment details without confirmation.
- Do not proceed with implementation when a required fact is uncertain.
- Do not invent placeholders or pseudo-behavior as a substitute for real requirements.
- Do not choose an arbitrary implementation path when a brief option comparison is more appropriate.

## Decision standard

- Ask before acting when the task is ambiguous.
- Confirm the missing fact, then implement directly.
- Keep clarifying questions narrow, relevant, and actionable.
