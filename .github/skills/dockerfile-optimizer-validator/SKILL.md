---
name: dockerfile-optimizer-validator
description: "Analyze, optimize, complete, and validate Dockerfiles with explicit build and runtime safety checks."
---

# Dockerfile Optimizer & Validator

## Scope

Use this skill to inspect Dockerfiles, apply justified improvements, complete missing operational metadata, validate the resulting image, and report unresolved assumptions or risks.

## Required workflow

### 1. Find and select the Dockerfile

Search the current project directory recursively for Dockerfiles, including names such as:

- `Dockerfile`
- `Dockerfile.dev`
- `Dockerfile.prod`
- `Dockerfile.test`
- Files matching `Dockerfile.*`

Exclude generated, vendored, dependency, and hidden-directory copies unless the user explicitly requests them. Also check build scripts, Compose files, and documentation when filename discovery alone is insufficient.

Apply these selection rules:

- If no Dockerfile is found, inform the user and ask whether a new `Dockerfile` should be created. Do not create one without confirmation.
- If exactly one Dockerfile is found, read it automatically.
- If multiple Dockerfiles are found, list every candidate and explicitly ask which file should be analyzed and optimized. Do not choose one silently.

Before editing, inspect the selected Dockerfile together with relevant build context files, `.dockerignore`, Compose files, package manifests, lockfiles, and application configuration when needed to understand the build and runtime contract.

### 2. Analyze and optimize

Preserve the application's build and runtime intent. Ask targeted questions when a change depends on unknown runtime ports, health endpoints, startup commands, base-image compatibility, architecture, or required system packages.

Check and improve the selected Dockerfile as appropriate:

- **Multi-stage builds:** separate dependency installation and compilation from the runtime stage when the application supports it, and copy only the artifacts required at runtime.
- **Layer caching and performance:** order stable dependency and manifest copies before frequently changing source files; combine related package-manager operations; remove package-manager caches in the same `RUN` instruction.
- **Base images and supply-chain security:** replace floating `latest` tags with a compatible version or digest when the required version can be established. If compatibility cannot be established, flag the tag and ask for the desired version instead of guessing.
- **Least privilege:** create or use a dedicated non-root user and set `USER` for the runtime stage unless the application or base image explicitly requires root.
- **Build context:** verify that `.dockerignore` excludes unnecessary files, credentials, dependency caches, and build output without excluding required inputs. Do not expose secret values in the Dockerfile or build arguments.
- **Operational metadata:** add `WORKDIR`, `EXPOSE`, and `HEALTHCHECK` when they are absent and their values can be derived safely from the application contract. Ask before guessing ports, health paths, or commands.
- **Runtime behavior:** preserve or improve `ENTRYPOINT` and `CMD`, prefer exec-form instructions, and ensure signal handling and graceful shutdown are not undermined.

Keep changes minimal, explain compatibility-sensitive changes before applying them, and do not refactor unrelated files.

### 3. Build and validate

After optimization, inspect the final Dockerfile and resolve static issues before building. Ask the user for explicit permission before executing Docker commands that build images or run containers. With permission, run:

```bash
docker build . -f <selected-dockerfile> -t <temporary-image-tag>
```

Use the correct build context and pass required, non-secret build arguments only when they are documented by the project. Review the build output for syntax errors, warnings, failed steps, deprecated instructions, unpinned images, inefficient layers, and leaked secrets. Do not claim a successful build if the command was not executed or completed successfully.

If the build succeeds, run a short smoke test using the image tag:

```bash
docker run --rm <temporary-image-tag>
```

Use detached mode with a bounded timeout when the container is a long-running service, and stop it after checking startup behavior. Confirm that the entrypoint or command starts successfully, inspect relevant output, and report whether the container exited, remained healthy, or failed. Do not run destructive cleanup commands or publish images without separate explicit approval.

If Docker is unavailable, report that validation could not be executed and provide the exact commands for the user. If a build or smoke test fails, diagnose the smallest relevant change, apply only a justified correction, and rerun the focused validation.

## Safety and decision rules

- Never choose a Dockerfile silently when multiple candidates exist.
- Never guess base-image versions, runtime ports, health endpoints, users, package requirements, or startup commands when the project does not establish them.
- Never put passwords, tokens, private keys, or other secrets in Dockerfiles, image layers, build arguments, logs, or reports.
- Do not run `docker build` or `docker run` without explicit user approval.
- Do not use `--privileged`, mount sensitive host paths, disable TLS verification, or broaden container permissions to make validation pass.
- Preserve compatibility unless the user approves a breaking change, and describe the impact of changes to exposed ports, startup commands, runtime users, or image families.
- Use English for generated comments, explanations embedded in files, and validation output.

## Expected output

Report:

1. the selected Dockerfile and any alternatives that required user selection
2. analysis findings and applied changes
3. security concerns and unresolved assumptions
4. the exact build command and its result, or the reason it was not executed
5. the smoke-test command and observed startup result, when approved and available

## Completion checklist

- The correct Dockerfile was selected explicitly.
- Build context, `.dockerignore`, dependencies, and runtime requirements were reviewed as needed.
- Multi-stage structure, caching, base-image pinning, least privilege, package cleanup, and operational metadata were evaluated.
- The Dockerfile was built successfully, or the unavailable prerequisite or missing approval was reported.
- The resulting image was smoke-tested when build and run permission was granted.
- Build output and runtime behavior were reviewed for warnings, errors, inefficiencies, and security risks.
- Remaining assumptions and risks were reported clearly.
