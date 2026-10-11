---
name: execution-direct-mode
description: "Use when requirements are clear and approved; proceed directly with the required implementation without unnecessary delay or chatter."
applyTo: "**"
---

## Scope

- Applies to all tasks, files, and repositories in this project when requirements are clear and approved.
- Use this mode for implementation work that does not require additional clarification.
- This rule governs execution flow only; it does not redefine code quality, API contracts, or documentation approval policy.

## Required behavior

- Start implementation immediately once the requirement is clear and no blocking ambiguity remains.
- Keep the work focused, minimal, and production-ready.
- Prefer direct edits over prolonged analysis, repeated discussion, or exploratory drift.
- Validate the result with the smallest relevant command, check, or proof that confirms the intended behavior.
- Stop only when a required fact is genuinely missing, ambiguous, or high-risk to implement without confirmation.

## Do not

- Do not add speculative changes, broad refactors, or unrelated cleanup without explicit need.
- Do not repeat status commentary or explanation loops before making a required change.
- Do not delay implementation while searching for unnecessary alternatives.
- Do not guess missing requirements, API contracts, or user intent.

## Execution standard

- Move from confirmation to action without unnecessary pause.
- Keep the implementation aligned with the exact task and the current project conventions.
- Prefer early completion with evidence over prolonged optimization.
