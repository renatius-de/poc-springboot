---
name: maven-gradle-dependency-updates
description: "Safely update direct Maven and Gradle dependencies with mandatory CVE scanning, compatibility checks, and transitive-dependency safeguards."
applyTo: "**/pom.xml,**/build.gradle,**/build.gradle.kts,**/gradle/libs.versions.toml"
---

# Maven and Gradle Dependency Updates

Use this instruction for dependency maintenance in Maven POMs, Gradle build files, and Gradle version catalogs. Pair detailed task execution with the `direct-dependency-update` skill when a dependency update is requested.

## Scope and direct-dependency boundary

- Inspect and update only dependencies explicitly declared by the project in the affected Maven or Gradle configuration.
- Do not add a transitive dependency to a build file merely because a scanner reports a vulnerability.
- Do not change a transitive dependency through Maven dependency-management overrides, Gradle constraints, forced versions, resolution strategies, or exclusion workarounds unless the user explicitly promotes that library to a direct dependency.
- A dependency managed by a BOM, platform, parent POM, or version catalog is direct only when the project explicitly declares it in the relevant dependency configuration.
- If a transitive vulnerability cannot be resolved through a compatible direct-dependency update, preserve its classification, report the dependency path, and escalate the remediation decision.
- Preserve existing scopes, configurations, exclusions, repositories, platforms, catalogs, lockfiles, and dependency-verification settings unless the approved update requires a documented migration.

## Required workflow

### 1. Establish the build contract

Before editing, read the owning build files and relevant wrappers, settings, parent POMs, BOM imports, dependency-management files, version catalogs, repository instructions, and CI tasks. Determine:

- the build system, module, supported Java version, and wrapper version
- where each direct dependency and its version are declared
- whether versions are inline, Maven properties, parent-managed, platform-managed, or catalog-managed
- the existing compile, test, lockfile, and dependency-verification commands

Ask one focused clarification question if ownership, version source, supported runtime, or compatibility requirements are materially unclear. Do not guess versions or commands.

### 2. Inventory direct dependencies

For Maven, inspect declared entries under the relevant `<dependencies>` sections and distinguish them from entries shown only by `mvn dependency:tree` or `mvn help:effective-pom`.

For Gradle, inspect direct declarations such as `implementation`, `api`, `compileOnly`, `runtimeOnly`, test configurations, and project-specific configurations. Use `./gradlew dependencies` and `./gradlew dependencyInsight` only to understand resolution; resolved transitive entries are not edit targets.

Record the declaration file, configuration or scope, coordinates, current version source, resolved version, and direct/transitive classification for each candidate. Stop and report candidates that cannot be classified confidently.

### 3. Scan for vulnerabilities before every update

Run a vulnerability scan before changing each dependency. Prefer security tooling already configured by the project:

- Maven OWASP Dependency-Check, such as `mvn dependency-check:aggregate` or the project's configured verification task
- Gradle OWASP Dependency-Check, such as `./gradlew dependencyCheckAnalyze`
- GitHub Dependabot alerts when repository access is available
- Snyk with the project's configured command, such as `snyk test`
- another approved scanner already defined by the repository or CI workflow

Record the baseline scanner, command, coverage, CVE or advisory, severity, affected version range, dependency path, and direct/transitive classification. Do not invent credentials, scanner configuration, suppressions, or thresholds. Never expose secrets or private configuration in logs or reports.

### 4. Identify and assess update candidates

Use established update tooling rather than guessing:

- Maven: use the configured `org.codehaus.mojo:versions-maven-plugin`, commonly `mvn versions:display-dependency-updates`
- Gradle: use the configured `com.github.ben-manes.versions` Gradle Versions Plugin, commonly `./gradlew dependencyUpdates`
- Version catalogs: update an existing key in `gradle/libs.versions.toml` only when its alias is used as a direct dependency in the target project

Treat reports as candidate lists. Check official release notes, changelogs, migration guides, compatibility matrices, required Java/framework versions, deprecations, changed defaults, and known vulnerabilities. Prefer compatible patch or minor releases before major releases.

### 5. Apply updates conservatively

Change only the existing declaration or version source for the approved direct dependency. Keep coordinates, scopes, configurations, exclusions, aliases, platforms, ordering, and repository settings unchanged unless a documented migration requires otherwise.

Update one dependency or one tightly coupled, documented set at a time. A direct promotion of a previously transitive dependency requires explicit approval, a normal direct declaration, and a documented reason. Never use a forced version, resolution strategy, dependency constraint, or dependency-management override as a substitute for that approval.

### 6. Prevent breaking changes

Treat major releases as migrations rather than routine replacements:

- first move to the latest compatible patch or minor release where possible
- inspect changelogs and migration guides before each stage
- migrate deprecated APIs and configuration deliberately
- run focused compilation and tests after each stage
- assess public API, runtime, persistence, serialization, security, and build-plugin impact
- obtain approval before a major update that changes a public contract, runtime requirement, or security boundary without an established migration path

Do not combine unrelated major updates merely to reduce the number of edits.

### 7. Verify build behavior and scan again

After each update, run the narrowest relevant wrapper-based compile or test task, focused tests for affected integration points, and a dependency graph inspection. Confirm that only the intended direct declaration changed and that lockfiles or dependency-verification metadata remain consistent.

Run the same vulnerability scanner used for the baseline after every update, even when compilation and tests pass. Compare the results and confirm that the targeted CVE is resolved or reduced, no new critical or high-severity vulnerability was introduced, and the affected module was covered.

For unresolved vulnerabilities:

- prioritize compatible direct-dependency updates for critical and high-severity CVEs
- keep direct and transitive findings separate
- do not silently suppress, force, exclude, or override transitive findings
- document approved exceptions with the affected versions, evidence, compensating controls, owner, review or expiry date, and advisory reference
- report unavailable databases, network failures, rate limits, and false-positive review needs accurately

## Prohibited patterns

- Do not add or directly edit transitive dependencies without explicit promotion to direct status.
- Do not treat a passing build as evidence that known CVEs are resolved.
- Do not skip either the pre-update or post-update vulnerability scan.
- Do not update every dependency returned by a report without classifying directness and compatibility.
- Do not bypass wrappers, dependency locking, verification, signatures, TLS validation, or repository security controls.
- Do not expose tokens, passwords, private URLs, scanner secrets, or sensitive dependency data.

## Verification and report

Before finalizing, reread every changed build file and inspect the diff for accidental transitive changes, unapproved direct declarations, scope or configuration drift, lockfile changes, and major-update risks.

Report:

1. the direct-dependency inventory and excluded transitive-only candidates
2. the pre-update scanner, command, findings, and coverage
3. candidate versions, compatibility evidence, update type, and approvals
4. changed files and direct dependencies updated
5. build, test, dependency-graph, and post-update scan results
6. unresolved vulnerabilities, approved exceptions, unavailable checks, and remaining risks

## Success criteria

This instruction is satisfied only when every edit targets an existing direct dependency or an explicitly approved promotion, no transitive dependency is silently changed, CVE scans run before and after each update, compatibility evidence supports the selected versions, major changes follow a staged migration, and verified results are separated from unavailable checks and unresolved risks.
