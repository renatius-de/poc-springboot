# Copilot instructions for this repository

## Repository context

- This repository is a Gradle multi-module project with the root build in `build.gradle.kts`.
- The currently active application module is `restclient`.
- The codebase targets Java 25 and the Spring Boot line declared in the parent POM.
- Spring dependency versions are managed centrally through the root build and imported BOMs.

## Change rules

- Prefer small, focused changes that stay within the affected module.
- Use the Gradle Wrapper for repository commands: `./gradlew`.
- Do not add dependency versions in module build files when the version is already managed by the root build or imported BOMs.
- Keep production dependencies and test dependencies scoped correctly.
- Follow the existing package structure under `de.renatius.poc.springboot.restclient`.
- Preserve the existing architecture constraints covered by ArchUnit tests.

## Validation expectations

- Run the smallest existing verification command that proves the change is safe.
- For repository-wide or build-related changes, prefer `./gradlew --no-daemon clean build`.
- For workflow and GitHub configuration changes, also validate YAML syntax and file paths.
- Do not introduce new build tools when the existing Gradle workflow is sufficient.

## Style expectations

- Follow `.editorconfig` for indentation, line endings, and line length.
- Keep Markdown concise and task-oriented.
- Avoid adding comments unless they clarify non-obvious intent.

## Pull request expectations

- Summarize what changed and why.
- Mention any workflow, dependency, or contributor-experience impact.
- Call out follow-up work separately instead of mixing it into the same change.

## CI/CD rules (mandatory)

- Java applications must never be built twice in GitHub Actions, nor rebuilt in separate workflows.
- Use the central `build` job in `.github/workflows/build.yml`, which runs the Gradle build once and publishes the `gradle-build` artifact via `actions/upload-artifact`.
- Test, lint, and deploy jobs or workflows must consume that artifact via `actions/download-artifact` instead of running their own Gradle builds.
- Prefer build-free analysis modes (for example CodeQL `build-mode: none`) over additional Gradle builds.
