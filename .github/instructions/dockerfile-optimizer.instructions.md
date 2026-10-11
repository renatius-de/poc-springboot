---
name: dockerfile-optimizer
description: "Use GitHub Copilot to analyze, optimize, complete, and validate Dockerfiles."
applyTo: "**/Dockerfile,**/Dockerfile.*,**/dockerfile,**/dockerfile.*"
---

# Dockerfile Optimizer & Validator

## Scope

Act as an expert GitHub Copilot instruction set for analyzing, optimizing, completing, and validating Dockerfiles. Preserve the application's build and runtime contract, keep edits focused, and report assumptions explicitly.

## Required workflow

### 1. Find and select the Dockerfile

Search the current project directory recursively for Dockerfiles, including `Dockerfile`, `Dockerfile.dev`, `Dockerfile.prod`, and other `Dockerfile.*` variants.

- If no Dockerfile is found, inform the user and ask whether a new `Dockerfile` should be created. Do not create one without confirmation.
- If exactly one Dockerfile is found, read it automatically.
- If multiple Dockerfiles are found, list every candidate and explicitly ask which one should be analyzed. Do not choose silently.
- Exclude generated, vendored, dependency, and hidden-directory copies unless the user requests them.
- After selection, inspect relevant `.dockerignore`, Compose files, build scripts, package manifests, lockfiles, and application configuration as needed.

### 2. Analyze and optimize

Read the selected Dockerfile completely and evaluate the following:

- **Multi-stage builds:** separate build and runtime stages when supported, and copy only runtime artifacts into the final image.
- **Layer caching:** copy stable dependency manifests before source files and order `COPY` and `RUN` instructions to preserve useful cache layers.
- **Base images:** replace `latest` or other floating tags with a compatible version or digest when the project establishes one. Ask instead of guessing when compatibility is unknown.
- **Security:** use a dedicated non-root user with `USER`, avoid secrets in Dockerfiles and build arguments, and clean package-manager caches in the same `RUN` step, including `rm -rf /var/lib/apt/lists/*` where applicable.
- **Completeness:** add `WORKDIR`, `EXPOSE`, and `HEALTHCHECK` only when their values can be derived from the application contract. Ask before guessing ports, health endpoints, or commands.
- **Runtime behavior:** preserve or improve exec-form `ENTRYPOINT` and `CMD`, signal handling, and graceful shutdown behavior.

Keep changes minimal, preserve unrelated user edits, and explain compatibility-sensitive changes before applying them.

### 3. Build and validate

Ask the user for explicit permission before running Docker commands that build or run an image. With permission, use the correct build context and run:

```bash
docker build . -f <selected-dockerfile> -t <temporary-image-tag>
```

Review build logs for syntax errors, failed steps, warnings, deprecated instructions, unpinned images, inefficient layers, and secret leakage. If the build succeeds, perform a short smoke test:

```bash
docker run --rm <temporary-image-tag>
```

Use detached mode with a bounded check for long-running services, inspect startup output, and stop the test container afterward. Do not publish images, use privileged mode, mount sensitive host paths, or perform destructive cleanup without separate approval.

If Docker is unavailable or permission is not granted, report that runtime validation was not performed and provide the exact commands that remain.

## Clarification and safety rules

- Never choose silently among multiple Dockerfiles.
- Never invent image versions, ports, health endpoints, users, package requirements, or startup commands.
- Never expose passwords, tokens, private keys, or other secret values in files, logs, diffs, or reports.
- Preserve compatibility unless the user approves a breaking change.
- Use English for generated repository content and validation output.

## Expected output

Report the selected Dockerfile, alternatives requiring selection, findings, applied changes, security concerns, unresolved assumptions, exact validation commands and results, and smoke-test observations when available.

## Completion checklist

- The correct Dockerfile was selected explicitly.
- Multi-stage builds, caching, pinned images, least privilege, cache cleanup, and operational metadata were evaluated.
- Build and smoke-test permission was requested before Docker execution.
- Build logs and runtime behavior were reviewed, or unavailable checks were reported accurately.
- Remaining risks and assumptions were stated clearly.
