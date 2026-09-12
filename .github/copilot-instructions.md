# Copilot instructions for this repository

## Repository context

- This repository is a Maven multi-module project with the parent build in `pom.xml`.
- The currently active application module is `restclient`.
- The codebase targets Java 25 and Spring Boot 4.
- Spring dependency versions are managed centrally through the parent POM and imported BOMs.

## Change rules

- Prefer small, focused changes that stay within the affected module.
- Use the Maven Wrapper for repository commands: `./mvnw`.
- Do not add dependency versions in module POM files when the version is already managed by the parent POM or imported BOMs.
- Keep production dependencies and test dependencies scoped correctly.
- Follow the existing package structure under `de.renatius.poc.springboot.restclient`.
- Preserve the existing architecture constraints covered by ArchUnit tests.

## Validation expectations

- Run the smallest existing verification command that proves the change is safe.
- For repository-wide or build-related changes, prefer `./mvnw -B -ntp verify`.
- For workflow and GitHub configuration changes, also validate YAML syntax and file paths.
- Do not introduce new build tools when the existing Maven workflow is sufficient.

## Style expectations

- Follow `.editorconfig` for indentation, line endings, and line length.
- Keep Markdown concise and task-oriented.
- Avoid adding comments unless they clarify non-obvious intent.

## Pull request expectations

- Summarize what changed and why.
- Mention any workflow, dependency, or contributor-experience impact.
- Call out follow-up work separately instead of mixing it into the same change.
