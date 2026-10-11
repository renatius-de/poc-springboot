---
applyTo: ".github/workflows/**"
---

# GitHub Actions workflow conventions

## Build once and reuse artifacts

- Never build the Java application more than once in GitHub Actions, whether across jobs or workflows.
- `.github/workflows/build.yml` is the central build workflow. Its `build` job runs
  `./gradlew --no-daemon clean build` once and uploads the `gradle-build` artifact.
- Any downstream test, lint, analysis, or deployment job that needs build output must download and
  consume `gradle-build` with `actions/download-artifact`; it must not run Gradle to rebuild the
  application.
- Prefer build-free analysis where supported. The CodeQL workflow currently uses
  `build-mode: none`; preserve this unless the requirement explicitly requires a different
  approach.

## Workflow safety and validation

- Keep permissions least-privileged. Preserve existing concurrency controls, cancellation behavior,
  and artifact retention unless the requested change requires otherwise.
- Before finalizing workflow changes, validate YAML syntax and verify every referenced path,
  artifact name, permission, job dependency, and data flow.
- Keep workflow changes focused. Do not add duplicate build workflows or move the central
  application build into a separate analysis, test, or deployment workflow.
