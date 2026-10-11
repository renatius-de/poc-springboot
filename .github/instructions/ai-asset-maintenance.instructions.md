---
name: ai-asset-maintenance
description: "Use when updating repository AI instructions and skills; enforce naming consistency, metadata quality, structural clarity, and final verification across all .instructions.md and SKILL.md files."
applyTo: "**/*.instructions.md,**/SKILL.md"
---

## Scope

- Applies to all repository files matching `**/*.instructions.md` and `**/SKILL.md`.
- Use this rule whenever instruction files, skill files, or their names are being reviewed, refactored, or synchronized anywhere in the workspace.
- Keep repository AI assets consistent, discoverable, and easy to maintain across all locations.
- This rule governs AI asset hygiene only: naming, metadata, structure, and final verification. It does not redefine Java quality rules, API specification rules, or documentation approval policy.

## Required behavior

### 1. Inventory and analyze

Before editing, read every relevant file in scope and identify:

- completeness and coverage
- clarity and maintainability
- redundancy and overlap
- naming inconsistencies
- missing or weak metadata in frontmatter
- structural drift from the repository's expected conventions

Do not assume a file is correct just because it exists. Check actual content, purpose, and consistency.

### 2. Improve content quality

Adjust content only when it improves correctness or maintainability:

- simplify duplicated guidance
- clarify the actual task and trigger conditions
- remove vague or speculative statements
- keep scope, workflow, and validation steps explicit
- preserve unique domain intent for each instruction or skill
- keep language concise, direct, and English only

Prefer smaller, higher-quality files over bloated or duplicated ones.

### 3. Standardize naming

Apply one consistent naming scheme across all AI asset files.

Use:

- `domain-focus.instructions.md` for instruction files
- `domain-focus/SKILL.md` for skill directories

Examples:

- `execution-direct-mode.instructions.md`
- `code-quality-complexity.instructions.md`
- `java-25-ecosystem-standards.instructions.md`
- `openapi-spec-authoring.instructions.md`
- `java-record-conversion/SKILL.md`
- `makefile-optimization/SKILL.md`
- `openapi-spec-refactoring/SKILL.md`

Use kebab-case, consistent verbs, and domain-first naming. Avoid legacy title casing or inconsistent identifiers.

### 4. Normalize metadata

For every instruction or skill file:

- ensure the YAML frontmatter starts with `---`
- include a `name` field
- keep `description` concise and action-oriented
- ensure `applyTo` is meaningful and specific when relevant
- keep metadata aligned with the file name and actual purpose

Do not leave orphaned or mismatched names, descriptions, or scopes.

### 5. Keep structure predictable

Use the repository's expected pattern:

- clear title or heading
- scope section
- required behavior / workflow
- prohibited patterns or anti-patterns
- validation or verification section
- concise success criteria or output expectations

Keep each file focused on one domain or one recurring task pattern.

### 6. Preserve purpose and avoid overreach

Do not broaden a file beyond its actual use case.

- instructions should define operating rules and guardrails
- skills should define focused expert workflows and decision rules
- do not merge unrelated responsibilities into one asset
- do not rewrite correct wording solely for style

### 7. Verify the result

After edits and renames:

- reread each changed file
- verify frontmatter and naming are valid
- confirm the resulting structure still matches the repository conventions
- ensure no broken references or mismatched metadata remain
- confirm there are no syntactic or logical regressions

Use the smallest direct validation that proves the file set is still valid.

## Do not

- Do not keep legacy names if a clearer standard name is available.
- Do not duplicate instructions or skills that overlap too strongly.
- Do not add speculative content without a real repository need.
- Do not leave missing `name` fields or malformed frontmatter.
- Do not change unrelated documentation or project files in the course of this cleanup.
- Do not invent workflow steps that are not grounded in actual repository usage.

## Quality standard

- Favor clarity, consistency, and maintainability over complexity.
- Keep all generated text in English unless the user explicitly requests otherwise.
- Prefer small, precise improvements over large rewrites.
- Treat naming, metadata, and verification as a required part of the work, not an optional cleanup step.

## Completion checklist

Before considering the update complete, confirm all of the following:

- all relevant files were reviewed
- the naming scheme is consistent
- metadata is normalized
- content is concise and purposeful
- redundant material was removed or reduced
- every changed file was reread and validated

## Success criteria

The maintenance task is successful when the AI asset set is:

- consistent
- easy to discover
- easier to maintain
- free of duplicate or weakly defined guidance
- verified as structurally valid after edits
