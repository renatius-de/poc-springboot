---
name: docker-compose-optimizer
description: "Use GitHub Copilot to analyze, optimize, complete, and validate Docker Compose configurations."
applyTo: "**/docker-compose*.yml,**/docker-compose*.yaml,**/compose.yml,**/compose.yaml"
---

# Docker Compose Optimizer & Validator

## Scope

Act as an expert GitHub Copilot instruction set for analyzing, optimizing, completing, and validating Docker Compose configurations. Preserve service intent, keep edits focused, and report assumptions explicitly.

## Required workflow

### 1. Find and select the Compose file

Search the current project directory recursively for Compose files, including `docker-compose.yml`, `docker-compose.yaml`, `docker-compose.override.yml`, override and environment variants, `compose.yml`, and `compose.yaml`.

- If no Compose file is found, inform the user and ask whether a new `docker-compose.yml` should be created. Do not invent services without confirmation.
- If exactly one Compose file is found, read it automatically.
- If multiple Compose files are found, list every candidate and explicitly ask which one should be analyzed. Do not choose silently.
- Exclude generated, vendored, dependency, and hidden-directory copies unless the user requests them.
- After selection, inspect referenced `.env` files, variable definitions, build contexts, Dockerfiles, scripts, and nearby configuration as needed.

### 2. Analyze and optimize

Read the selected Compose file completely and evaluate the following:

- **Syntax and structure:** correct YAML indentation and structure, remove the obsolete top-level `version` attribute, and preserve valid Compose interpolation.
- **Completeness:** add service-appropriate `healthcheck` and `restart: unless-stopped` policies, explicit networks, named volumes, and bounded logging options where justified.
- **Security:** replace `latest` image tags with compatible versions when established by the project; otherwise ask instead of guessing. Move non-secret configuration to `.env` variables and sensitive values to Docker Secrets or an external secret manager. Never print secret values.
- **Resources:** add meaningful `deploy.resources.limits` for CPU and memory only when workload requirements or project constraints support safe values. Ask before guessing limits that could prevent startup.
- **Operations:** review service dependencies, readiness versus startup ordering, persistence, port exposure, bind mounts, least privilege, and network exposure. Do not add privileged mode, broad host mounts, or public ports without a clear requirement.

Explain changes that may affect persistence, ports, credentials, image compatibility, data loss, or service availability before applying them. Preserve unrelated user edits.

### 3. Validate and optionally run

Run syntax validation from the directory containing the selected file, or provide the correct file path explicitly:

```bash
docker compose config
```

Resolve configuration errors before runtime checks. Ask the user for explicit permission before starting services. With permission, run:

```bash
docker compose up -d
docker compose ps
docker compose logs --tail=100
```

Use `docker compose ps` and targeted logs to inspect service state, restarts, exits, and health status. Do not claim a service is healthy solely because its container is running. If no healthcheck exists, report that health could not be directly verified. Treat volume removal, destructive recreation, pruning, and other data-impacting operations as requiring separate approval.

If Docker is unavailable or permission is not granted, report the limitation and provide the exact commands that remain.

## Clarification and safety rules

- Never choose silently among multiple Compose files.
- Never invent services, image versions, credentials, resource limits, ports, or persistence behavior.
- Never expose passwords, tokens, private keys, or other secret values in files, logs, diffs, or reports.
- Preserve compatibility unless the user approves a breaking change.
- Use English for generated repository content and validation output.

## Expected output

Report the selected Compose file, alternatives requiring selection, findings, applied changes, security concerns, unresolved assumptions, the `docker compose config` result, and service state, health, and log observations when runtime execution was approved.

## Completion checklist

- The correct Compose file was selected explicitly.
- YAML and Compose structure, obsolete syntax, images, secrets, limits, persistence, networks, and logging were reviewed.
- Configuration validation completed successfully, or unavailable checks were reported accurately.
- Runtime startup occurred only after explicit approval.
- Service state, healthchecks, and logs were inspected after startup.
- Remaining risks and assumptions were stated clearly.
