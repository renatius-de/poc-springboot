---
name: readme-audit-and-update
description: "Audit a project and update each README.md so it matches the current technical and functional implementation without rewriting correct wording."
---

# Update Project READMEs

Use this skill to review every existing `README.md` in a project and update only statements that are technically or functionally outdated, incorrect, or necessary to keep the documentation aligned with the implementation.

## Goal

Produce accurate, maintainable README documentation without rewriting text for style alone. Preserve the author's existing structure, terminology, tone, and examples whenever they remain correct.

The update must:

- cover every existing `README.md` in the project, including nested module and repository directories
- reflect the current functional behavior, commands, paths, targets, configuration, dependencies, and architecture
- remove or correct obsolete instructions, broken paths, stale target names, and inaccurate technical claims
- preserve correct documentation unchanged whenever possible
- keep changes minimal and easy to review

## Required Working Method

### 1. Inventory the documentation

Before editing, locate every existing `README.md` from the project root, including files in nested directories and vendored or embedded repositories. Record the scope and read each relevant README in context.

Do not assume that the root README is the only documentation source. Treat module READMEs and nested repository READMEs as independent documents whose claims must also be checked.

### 2. Analyze the complete project

Inspect the implementation and configuration that each README describes. Depending on the project, check:

- source code and module boundaries
- root and module build files, Makefiles, scripts, and task definitions
- package manifests, lockfiles, runtime versions, and dependency configuration
- configuration files, environment variables, filesystem paths, and generated artifacts
- CI workflows, container definitions, deployment files, and developer tooling
- symlink, installation, upgrade, cleanup, and local override behavior
- references between modules and documented commands

Use the actual project as the source of truth. Run focused inspection commands or safe dry runs when they can confirm a documented command or target. Use version history only when current files do not explain whether a statement is intentionally retained.

### 3. Compare claims with behavior

For every README, verify at least:

- installation, upgrade, cleanup, test, and development commands
- relative and absolute paths, directory names, and file names
- target names, command options, prerequisites, and environment variables
- supported platforms, runtimes, tools, and dependency requirements
- described modules, responsibilities, architecture, and data flow
- links, anchors, examples, code blocks, and referenced files
- warnings, side effects, permissions, destructive operations, and local overrides

Classify each finding as one of:

1. Correct and leave unchanged.
2. Technically or functionally outdated and required to update.
3. Incorrect and required to correct.
4. Ambiguous but not disproven; do not change without evidence.
5. Purely stylistic; leave unchanged.

### 4. Apply a strict change threshold

Change README content only when the change is necessary to keep it factually correct or operationally usable. Examples include:

- a command no longer exists or no longer produces the described result
- a path, target, file, module, runtime, dependency, or environment variable changed
- an architectural or functional description contradicts the implementation
- an example would fail because of a technical change
- a link points to a missing or moved file
- a documented warning or side effect is materially incomplete or false

Do not rewrite correct paragraphs for clarity, consistency, tone, grammar, or preferred terminology alone. Restrict spelling, grammar, and wording corrections to the absolute minimum and make them only when they are needed to prevent misunderstanding or to correct a necessary technical change.

Do not add speculative features, undocumented requirements, future plans, generic best practices, or promotional text. Do not remove useful context merely because it is not directly executable.

### 5. Edit conservatively

Preserve each README's existing:

- language and voice
- section order and heading structure
- valid examples and explanations
- formatting conventions
- local terminology

Make the smallest coherent edit for each verified discrepancy. Avoid broad reformatting, wholesale regeneration, automatic grammar rewrites, and unrelated documentation cleanup. Do not create new README files unless explicitly requested.

If implementation and documentation disagree but the intended behavior cannot be established from the project, stop and report the ambiguity instead of guessing. Prefer documenting verified behavior over changing implementation during this workflow.

### 6. Verify the changes

After editing:

- reread every changed README and confirm each change has a concrete technical or functional reason
- verify all referenced files and paths exist
- check internal links and anchors where practical
- run safe, focused dry runs or validation commands for documented workflows, such as `make -n` for Makefile targets
- confirm commands, options, target names, environment variables, and examples still match the implementation
- review the diff for accidental rewriting, formatting churn, or changes outside the necessary documentation scope
- confirm that every existing README was considered, even if no change was needed

Report skipped checks, unresolved ambiguities, and commands that were not safe to execute.

## Decision Rules

- Accuracy takes precedence over completeness of prose.
- Technical evidence takes precedence over assumptions or remembered behavior.
- Minimal factual correction takes precedence over stylistic improvement.
- Preserve compatibility and existing documentation structure unless a verified discrepancy requires a change.
- When a claim cannot be confirmed or disproven, leave it unchanged and report it as an open question.

## Expected Output

After completing the review, report:

1. which README files were inspected
2. which README files were changed and the concrete reason for each change
3. which technical or functional claims were verified
4. which checks and dry runs were executed
5. any unresolved ambiguities, skipped commands, or remaining documentation risks

## Quality Bar

The result should be:

- factually aligned with the current project
- complete across all existing README files
- minimally changed
- free of unnecessary language rewrites
- consistent with the actual commands, paths, architecture, and workflows
- easy to audit from a focused diff

## Final Instruction

Analyze the entire project and inspect every existing `README.md` before making changes. Update only technically or functionally necessary content, preserve correct existing text, keep language and grammar edits to an absolute minimum, and verify the resulting documentation against the implementation and safe project checks.