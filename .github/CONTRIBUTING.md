# Contributing

Thanks for contributing to this repository.

## Project overview

- Root build: `build.gradle.kts`
- Active module: `restclient`
- Runtime and build baseline: Java 25
- Build tool: Gradle Wrapper (`./gradlew`)

## Local prerequisites

- Git
- Java 25
- A shell environment that can execute `./gradlew`

## Recommended workflow

1. Create a topic branch from `main`.
2. Keep changes focused on a single concern.
3. Prefer the smallest change that fully solves the issue.
4. Update related documentation when behavior or contributor workflow changes.

## Build and test

Run repository-wide verification before opening or updating a pull request:

```bash
./gradlew --no-daemon clean build
```

If you only need a quicker compile check while iterating on build-related work:

```bash
./gradlew --no-daemon clean compileJava
```

## Dependency changes

- Prefer managed dependency versions from the root build and imported BOMs.
- Do not duplicate versions in module build files when the root build already manages them.
- Keep test libraries in test scope.

## Code structure

- Keep package names under `de.renatius.poc.springboot.restclient`.
- Preserve architecture expectations enforced by ArchUnit tests.
- Follow the formatting and editor settings from `.editorconfig`.

## Pull requests

- Explain what changed and why.
- Mention any workflow, dependency, or configuration impact.
- Ensure GitHub workflows remain valid when editing files under `.github/`.

## Reporting issues

- Use the issue templates when possible.
- For security issues, follow the process described in `SECURITY.md` instead of opening a public bug report.
