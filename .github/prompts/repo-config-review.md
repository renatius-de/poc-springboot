# Repository configuration review

Review this repository's GitHub configuration with a focus on maintainability, contributor experience, and automation quality.

## Inspect

- `.github/copilot-instructions.md` and any `.instructions.md` files
- `.github/prompts/`
- `.github/skills/` or `.github/skills.md`
- `.github/workflows/`
- `.github/dependabot.yml`
- `.github/CONTRIBUTING.md`
- `.github/SECURITY.md`
- `.github/ISSUE_TEMPLATE/`

## Check for

- Missing or outdated Copilot guidance
- Reusable prompts that would reduce repeated setup work
- Skills that capture repository-specific workflows
- Workflow syntax, triggers, permissions, concurrency, and separation of concerns
- Documentation placeholders or missing contributor/security guidance
- Generic issue templates that are not tailored to a Spring Boot backend repository

## Output format

Return:

1. A short current-state summary
2. A prioritized list of recommended file additions or edits
3. One summary per file covering:
   - purpose
   - key rules or automation
   - validation concerns
4. A stop point that asks for approval before writing files
