---
name: direct-dependency-update
description: "Safely identify, update, and verify direct Maven and Gradle dependencies with mandatory CVE scanning and transitive-dependency safeguards."
---

# Update Direct Maven and Gradle Dependencies

Use this skill when reviewing or updating dependencies declared directly in Maven `pom.xml` files, Gradle `build.gradle` files, Gradle `build.gradle.kts` files, or Gradle version catalogs. The objective is to keep declared direct dependencies current and secure while preserving build behavior and avoiding accidental changes to transitive dependencies.

## Scope and non-negotiable boundary

- Inspect and update only dependencies explicitly declared by the project in its Maven or Gradle configuration.
- Never add a transitive dependency directly to a build file merely because a vulnerability scanner reports it.
- Never change a transitive dependency through an override, exclusion replacement, resolution strategy, dependency constraint, or forced version unless the user explicitly promotes that library to a direct dependency.
- If a transitive vulnerability cannot be fixed through a safe direct-dependency update, report the path, affected component, and available remediation options. Do not silently add an override.
- Treat dependencies managed by a Maven BOM, Gradle platform, or version catalog as direct only when the project explicitly declares the dependency in the relevant dependency declaration. A managed version is not itself a direct dependency.
- Preserve existing repositories, plugin management, scopes, configurations, exclusions, classifiers, platforms, and version-catalog structure unless the requested update requires a documented change.

## Required workflow

### 1. Establish the build contract

Before editing, locate and read the relevant build files, wrapper files, settings, version catalogs, parent POMs, BOM imports, dependency-management configuration, and repository instructions.

Determine:

- whether the project uses Maven, Gradle Groovy DSL, Gradle Kotlin DSL, or more than one build system
- the Maven or Gradle wrapper version and supported Java version
- the module or subproject that owns each dependency declaration
- whether versions are declared inline, in Maven properties, a parent POM, a Gradle platform, `libs.versions.toml`, or another existing catalog
- applicable build profiles, source sets, test configurations, plugins, repositories, and CI verification tasks
- the project's compatibility requirements and whether the requested update is patch, minor, or major

If the owning build file, dependency version source, supported runtime, or compatibility contract is unclear, ask one focused clarification question before editing. Do not guess a version or build command.

### 2. Inventory direct dependencies only

Build a direct-dependency inventory from declarations in the owning configuration files.

For Maven:

- inspect `<dependencies>` in the relevant `pom.xml` files, including `groupId`, `artifactId`, `version`, scope, optionality, classifier, exclusions, and inherited or managed versions
- distinguish project declarations from dependencies shown only by `mvn dependency:tree`
- use `mvn dependency:tree` and `mvn help:effective-pom` as supporting evidence, not as permission to edit transitive dependencies
- account for multi-module parent and child POM ownership before changing a shared property or managed version

For Gradle:

- inspect direct declarations such as `implementation`, `api`, `compileOnly`, `runtimeOnly`, test configurations, and project-specific configurations
- inspect `build.gradle`, `build.gradle.kts`, `settings.gradle`, `settings.gradle.kts`, and `gradle/libs.versions.toml` where applicable
- use `./gradlew dependencies` and `./gradlew dependencyInsight` to understand resolution, but do not treat resolved transitive entries as direct declarations
- preserve the catalog's existing aliases, bundles, platforms, constraints, and plugin declarations

Record each candidate with its declaration file, configuration or scope, coordinates, current version source, resolved version, and whether the proposed change is direct. Stop and report any candidate that cannot be classified confidently.

### 3. Establish a pre-update security baseline

Run a vulnerability scan before every dependency update. The baseline must cover the relevant module and lockfile or resolved dependency graph without exposing secrets.

Use the security tooling already configured by the project. Supported approaches include:

- Maven OWASP Dependency-Check, for example `mvn dependency-check:aggregate` or the project's configured verification task
- Gradle OWASP Dependency-Check, for example `./gradlew dependencyCheckAnalyze`
- GitHub Dependabot alerts and security updates for the repository, when repository access is available
- Snyk using the project's configured command, for example `snyk test`, when Snyk is part of the project workflow
- another approved native or CI security scanner already defined by the repository

Do not invent scanner configuration, credentials, suppression files, or severity thresholds. If no scanner is configured, use the least invasive available command and report what was and was not covered. Never print tokens, credentials, private URLs, or full secret-bearing configuration.

Record the baseline findings, including component, CVE or advisory identifier, severity, affected version range, reachable path when available, and whether the component is direct or transitive. Prioritize critical and high-severity findings, but do not ignore lower-severity findings when the update changes the affected graph.

### 4. Identify safe candidate versions

Use the project's established update tooling to identify newer direct dependency versions:

- Maven: use the configured `org.codehaus.mojo:versions-maven-plugin`, commonly with `mvn versions:display-dependency-updates` and, where appropriate, `mvn versions:display-plugin-updates`
- Gradle: use the configured `com.github.ben-manes.versions` Gradle Versions Plugin and its `dependencyUpdates` task, commonly `./gradlew dependencyUpdates`
- Gradle version catalogs: update the existing version key in `gradle/libs.versions.toml` only when the alias is a direct dependency used by the target project
- use official release notes, changelogs, migration guides, and compatibility matrices to confirm candidates and identify breaking changes

Treat tool output as a candidate list, not an approval to update everything. Exclude transitive-only components, plugins, platforms, and unrelated modules unless they are explicitly in scope. Prefer the newest compatible patch or minor release before considering a major release.

### 5. Assess compatibility and breaking-change risk

For each direct dependency candidate:

- check release notes, migration guides, deprecations, required Java or framework versions, changed defaults, and known CVEs
- identify whether the update is patch, minor, or major according to the dependency's versioning policy
- inspect the project's usage, configuration, tests, and public API boundaries for affected behavior
- update one dependency or one tightly coupled, documented set at a time
- for major updates, use an explicit staged plan: update to the latest compatible minor line, migrate deprecated APIs, run focused checks, then evaluate the next major line
- do not combine unrelated major updates merely to reduce the number of edits
- preserve lockfiles and dependency verification metadata according to existing project practice

Ask for approval before applying a major update when it changes public APIs, runtime requirements, persistence behavior, security configuration, or build-system compatibility and the project does not already establish that migration path.

### 6. Apply the smallest direct update

Change only the version declaration or existing version source for the approved direct dependency. Keep coordinates, configuration or scope, exclusions, classifiers, aliases, and dependency ordering unchanged unless a documented migration requires otherwise.

- Maven: update the direct declaration or its existing property in the owning POM; do not add a dependency-management override for a transitive component
- Gradle: update the direct declaration or its existing version-catalog key; do not add `resolutionStrategy.force`, a new constraint, or a direct declaration for a transitive component as a workaround
- when a direct dependency is intentionally promoted from transitive use, obtain explicit approval, add it as a normal direct declaration, and document the reason and ownership
- do not change generated files, caches, or unrelated module build files

### 7. Verify the build and dependency graph

After each update, run the narrowest relevant checks before moving to another dependency:

1. Resolve or compile the affected Maven module with the repository's wrapper, such as `./mvnw -pl <module> -am test` or the documented focused command.
2. Resolve or compile the affected Gradle project with the repository's wrapper, such as `./gradlew <project>:test` or the documented focused command.
3. Run focused tests covering integration points, serialization, security, persistence, and public API behavior affected by the dependency.
4. Inspect the resolved graph with `mvn dependency:tree` or `./gradlew dependencies` and confirm that only the intended direct declaration changed.
5. Check for lockfile, dependency-verification, generated metadata, or reproducibility changes required by the existing build workflow.

Use wrapper commands and repository-provided tasks. Do not download arbitrary tools or bypass verification, TLS checks, signature checks, or dependency locking to make a build pass.

### 8. Run the mandatory post-update vulnerability scan

Run the same security scanner used for the baseline after every dependency update, even when compilation and tests pass. Examples include:

- `mvn dependency-check:aggregate` for a configured Maven OWASP Dependency-Check workflow
- `./gradlew dependencyCheckAnalyze` for a configured Gradle OWASP Dependency-Check workflow
- `snyk test` for a configured Snyk workflow
- the repository's GitHub Dependabot or CI security check when that is the established verification path

Compare the post-update result with the baseline. Confirm that the targeted CVE is resolved or reduced as expected, that no new critical or high-severity vulnerability was introduced, and that the result covers the affected module. A clean build is not evidence of a clean vulnerability scan.

If a vulnerability remains:

- prioritize a compatible direct-dependency update for critical and high-severity findings
- determine whether the remaining finding is direct or transitive and preserve that classification
- do not suppress, force, exclude, or override a transitive vulnerability without explicit approval and a documented rationale
- document affected versions, exploitability or reachability evidence, compensating controls, owner, expiry or review date, and the issue or advisory reference for any approved exception
- report unavailable scanner data, rate limits, offline databases, and false-positive review needs instead of claiming resolution

### 9. Review and report

Before finalizing, reread every changed build file and review the diff for:

- accidental changes to transitive dependencies
- new direct declarations that were not explicitly approved
- changes to scopes, configurations, exclusions, platforms, repositories, or plugin behavior
- lockfile or verification metadata drift
- major-update API or runtime risks
- leaked credentials or scanner output containing secrets

Report each updated direct dependency with its declaration location, old and new version, update type, reason, and validation result. Report direct and transitive security findings separately. Include commands that were run, scanner coverage, unavailable checks, exceptions, and remaining risks.

## Decision rules

- Direct dependency declarations are the only editable dependency surface by default.
- A transitive dependency is not an update target unless the user explicitly promotes it to a direct dependency.
- Prefer the smallest compatible update that resolves the issue and preserves the build contract.
- Treat critical and high CVEs as priority work, but never bypass compatibility, integrity, or verification controls.
- Treat major updates as migrations, not routine version replacements.
- Preserve existing BOMs, platforms, catalogs, lockfiles, dependency verification, repositories, and plugin conventions.
- Ask for clarification when ownership, version source, directness, compatibility, scanner coverage, or exception authority is materially unclear.
- Never expose secrets or claim a vulnerability is fixed without post-update scan evidence.

## Anti-patterns to avoid

- Do not add a transitive dependency directly only to silence a scanner.
- Do not use Maven dependency-management overrides or Gradle forced versions as an unapproved substitute for a direct update.
- Do not update every dependency returned by an update report without classifying ownership, directness, compatibility, and risk.
- Do not skip the vulnerability scan before or after an update.
- Do not treat a passing compile or test suite as proof that known CVEs are resolved.
- Do not suppress a vulnerability without a documented, time-bounded exception and an accountable owner.
- Do not combine unrelated major upgrades or skip release notes and migration guides.
- Do not bypass wrappers, lockfiles, dependency verification, TLS validation, signatures, or repository security controls.

## Expected output

Report results in this order:

1. **Dependency inventory**
   - Build system, affected modules, direct declarations, version sources, and excluded transitive-only candidates.
2. **Security baseline**
   - Scanner, command, coverage, direct and transitive findings, and prioritized CVEs.
3. **Update assessment**
   - Candidate versions, release and compatibility evidence, update type, breaking-change risks, and approvals.
4. **Implementation summary**
   - Changed build files, direct dependencies updated, preserved configuration, and any explicitly approved promotion to direct dependency.
5. **Validation result**
   - Build and focused-test commands, dependency-graph comparison, lockfile or verification changes, and post-update scan comparison.
6. **Exceptions and remaining risks**
   - Unresolved vulnerabilities, documented exceptions, unavailable checks, assumptions, and follow-up owners.

## Success criteria

The update is successful only when:

- every changed dependency was explicitly direct or explicitly approved for promotion to direct
- no transitive dependency was silently added, overridden, forced, or changed in a build configuration
- the baseline and post-update vulnerability scans were run and compared for every update
- critical and high-severity findings were resolved, escalated, or documented with an approved exception
- compatible updates preserve build, test, runtime, and public API behavior
- major updates follow a staged migration plan with release-note evidence
- lockfiles and dependency-verification metadata remain consistent with repository practice
- the final report distinguishes verified results from unavailable checks and unresolved risks
