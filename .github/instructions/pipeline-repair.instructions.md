---
name: pipeline-repair
description: "Use when diagnosing, repairing, or optimizing GitHub Actions workflows and CI pipeline failures."
applyTo: "**/.github/workflows/**"
---

# GitHub Actions Pipeline Repair

## Scope

- Applies to every GitHub Actions workflow under `.github/workflows/`.
- Diagnose failed jobs, repair the root cause, and keep existing workflow intent, permissions, triggers, and useful checks intact.
- Prefer the smallest change that makes the failing pipeline reliable and maintainable.

## Required workflow

### 1. Inspect before editing

- Read every workflow affected by the failure, including reusable workflows and referenced local actions.
- Inspect the complete failing log and identify the first actionable error rather than masking a later symptom.
- Check repository instructions, action versions, runner images, job containers, shell commands, environment variables, caches, artifacts, and required tools.
- Classify the failure as workflow syntax, action compatibility, runner or container setup, dependency installation, permissions, credentials, network or certificate trust, shell behavior, test failure, or repository code failure.

### 2. Repair the root cause

- Preserve triggers, job names, required checks, dependency ordering, least-privilege permissions, and existing failure semantics unless the failure directly requires a change.
- Pin actions to the repository's established major versions and use a supported runtime for JavaScript actions.
- For job containers, install every tool used by later steps before the first dependent step. Include the CA certificate bundle when Git, package managers, or HTTPS APIs are used.
- Ensure the configured shell exists and is executable. Install `zsh` when Zsh scripts are validated; retain POSIX `sh` for container and action compatibility.
- Prefer minimal images only when their package manager, libc, certificate store, shell, Git behavior, and action compatibility are understood. Do not switch to Alpine merely to reduce image size when it creates avoidable compatibility risk.
- Keep package installation non-interactive, clean package indexes in the same layer or step, and avoid installing unrelated tools.
- Use explicit working directories, quoting, and fail-fast shell behavior where needed. Do not hide errors with `|| true`, disabled checks, or unconditional success commands.
- Keep secrets in GitHub Secrets or variables. Never print, hard-code, or persist secret values.

### 3. Security and change boundaries

- Never disable TLS or certificate verification, use insecure package repositories, weaken permissions, or add privileged containers to make a pipeline pass.
- Never remove tests, security scans, dependency review, CodeQL, SARIF upload, or validation steps merely to avoid a failure.
- Never invent credentials, tokens, service endpoints, image tags, resource limits, or generated output.
- Do not force-push, commit, rewrite history, or change unrelated files unless explicitly requested.
- Treat changes to permissions, external services, publishing, deployment, persistence, or release behavior as high risk and explain them before applying them.

## Validation

- Parse every changed workflow as YAML.
- Run `actionlint` when available and resolve all relevant diagnostics.
- Run repository-local validation and tests for the changed slice, including Makefile dry runs, Python checks, and shell syntax checks when those steps are part of the workflow.
- Inspect `git diff --check` and the final diff for accidental permission, trigger, secret, path, or dependency changes.
- When Docker is available and the change affects a job container, verify the image can install the required tools and certificates. Do not claim a hosted GitHub Actions run was verified locally.
- If a required external check cannot be run, report the exact limitation and the remaining risk.

## Completion criteria

A pipeline repair is complete only when the root cause is addressed, existing intended checks remain enabled, security controls are preserved, changed workflows validate successfully, and any unavailable hosted-run verification is stated explicitly.
