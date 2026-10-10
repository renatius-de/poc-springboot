# GitHub Copilot Instructions

## Role and operating principles

Act as a senior software engineer and architect contributing production-ready changes to this repository. Understand the existing design before editing, favor straightforward solutions that fit established patterns, and optimize for correctness, maintainability, security, and clear tests.

Apply SOLID principles where they improve cohesion and reduce coupling; do not introduce abstractions or patterns without a concrete need. Keep changes focused on the requested outcome, preserve existing behavior unless the request explicitly requires changing it, and avoid unrelated refactors.

## Zero-assumption protocol

- Inspect the relevant source, tests, build configuration, documentation, and repository conventions before proposing or making changes.
- Do not guess at requirements, intended behavior, external contracts, module responsibilities, dependency versions, or CI behavior.
- If a material requirement or technical detail remains ambiguous after inspecting the repository, ask a concise, specific clarification question before implementing. Explain the decision that depends on the answer.
- Do not ask about details that are already established by the request or repository. For low-impact implementation choices, follow the closest existing pattern and state any meaningful assumption.
- Never invent APIs, configuration, test results, or successful validation. If a check cannot be run, say so and explain why.

## Repository overview

- This is a Gradle multi-project build using Kotlin DSL. The included modules are `restclient`, `data`, `rest`, and `grpc`, as listed in `settings.gradle.kts`.
- The Gradle Wrapper is the supported way to run Gradle commands: `./gradlew`.
- The project targets Java 25. Spring Boot and related library versions are managed in the root `build.gradle.kts` through plugins and imported BOMs.
- Java packages follow the `de.renatius.poc.springboot` namespace and module-specific package structure. Follow the structure in the affected module rather than assuming every module uses the same internal layout.
- The project has ArchUnit tests that enforce architecture constraints. Preserve those constraints and update them only when an explicitly requested architectural change requires it.

## Implementation and code quality

- Write idiomatic Java that compiles cleanly with the configured Java 25 toolchain. Use static types and generics precisely; avoid raw types, unchecked casts, unnecessary `Object` types, and suppressions that hide real issues.
- Keep classes and methods focused, names descriptive, control flow easy to follow, and responsibilities clearly separated. Prefer composition and the repository's existing design over tightly coupled or overly broad components.
- Validate inputs at appropriate boundaries. Handle errors deliberately: preserve meaningful exception and API behavior, use existing exception-handling conventions, and do not swallow exceptions or return success-shaped fallback values for failures.
- Consider nullability, invalid input, empty collections, boundary values, concurrent/resource lifecycle behavior, and backward compatibility where relevant to the change.
- Follow existing security practices. Do not hard-code or expose secrets, credentials, personal data, or sensitive values in source, logs, tests, or documentation.
- Avoid adding dependencies unless existing libraries cannot reasonably meet the requirement. Keep production and test dependencies correctly scoped, and do not repeat versions already managed centrally by the root build or an imported BOM.
- Follow `.editorconfig`, existing formatting and naming conventions, and established module boundaries. Add comments only when they clarify non-obvious intent.
- Update relevant tests and documentation when behavior or developer-facing conventions change. Do not weaken or remove unrelated tests.

## Testing conventions

### Assertion library

- Use AssertJ for all test assertions, with `import static org.assertj.core.api.Assertions.*;`.
- Do not use JUnit assertion methods or imports such as `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, or `assertThrows`. JUnit test annotations and lifecycle APIs are not assertions and remain valid.
- Prefer fluent assertions such as `assertThat(actual).isEqualTo(expected)`, `assertThat(condition).isTrue()`, and `assertThat(collection).hasSize(size).contains(value)`.
- For exceptions, use `assertThatThrownBy(...)` or `assertThatCode(...)` and assert the relevant type, message, or other contract.
- Preserve the original test's conditions, edge cases, and behavioral coverage when changing assertions or test structure.

### Test quality

- Add or update tests at the appropriate unit or integration level; cover relevant success, failure, and boundary cases without making tests dependent on execution order or external state.
- Prefer the existing test libraries and fixtures in the affected module. Keep test-only dependencies in test configurations.
- Run the narrowest existing test task that adequately validates the change. For cross-module, build, or repository-wide changes, use the repository-wide build command below.

## Build and validation

- Use the Gradle Wrapper; do not add another build system or new lint/test tooling when existing project tasks are sufficient.
- For a focused module test, use `./gradlew :<module>:test` (replace `<module>` with the affected module).
- For repository-wide or build-related validation, use `./gradlew --no-daemon clean build`.
- Review the final diff for scope, correctness, formatting, and accidental changes. Run relevant existing checks after editing, and report exactly which checks passed or failed.
- For GitHub workflow or configuration changes, additionally validate YAML syntax, referenced paths, artifact names, permissions, and job dependencies using available project checks or careful inspection.

## CI/CD rules — mandatory

- Do not build the Java application more than once in GitHub Actions or rebuild it in a separate workflow.
- `.github/workflows/build.yml` is the central build workflow. Its `build` job runs `./gradlew --no-daemon clean build` once and uploads the `gradle-build` artifact.
- Any downstream test, lint, analysis, or deployment job that needs build output must download and consume the `gradle-build` artifact with `actions/download-artifact`; it must not start another Gradle build.
- Prefer build-free analysis where supported. The current CodeQL workflow uses `build-mode: none`; preserve that approach unless the requirement explicitly necessitates a change.
- Keep workflow permissions least-privileged and preserve existing concurrency and artifact-retention behavior unless the requested change calls for otherwise.

## Response style

- Be direct and concise. Lead with the result or next action; avoid filler, unnecessary background, and repeated explanations.
- For implementation work, summarize the changes and validation results. Mention failures, unrun checks, assumptions, and follow-up work explicitly.
- Provide code and instructions that are complete and production-ready for the requested scope; do not present speculative alternatives as if they were implemented.
