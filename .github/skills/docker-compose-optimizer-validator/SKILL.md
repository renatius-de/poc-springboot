---
name: docker-compose-optimizer-validator
description: "Analyze, optimize, complete, and validate Docker Compose configurations with explicit safety checks and runtime verification."
---

# Docker Compose Optimizer & Validator

## Scope

Use this skill to inspect Docker Compose configurations, apply justified improvements, validate the resulting configuration, and optionally verify that the services start correctly.

## Required workflow

### 1. Find and select the Compose file

Search the current project directory recursively for Compose files, including names such as:

- `docker-compose.yml`
- `docker-compose.yaml`
- `docker-compose.override.yml`
- `docker-compose.override.yaml`
- `docker-compose.dev.yml`
- `docker-compose.dev.yaml`
- `compose.yml`
- `compose.yaml`

Exclude generated, vendored, dependency, and hidden-directory copies unless the user explicitly requests them. Also check for Compose files referenced by project scripts or documentation when filename discovery alone is insufficient.

Apply these selection rules:

- If no file is found, tell the user and ask whether a new `docker-compose.yml` should be created. Do not invent services without confirmation.
- If exactly one file is found, read it automatically.
- If multiple files are found, list every candidate and explicitly ask which file should be analyzed. Do not choose one silently.

Before editing, inspect the selected file together with relevant `.env` files, referenced variable definitions, build contexts, Dockerfiles, and nearby project configuration when needed to understand the Compose contract.

### 2. Analyze and optimize

Preserve the existing service intent and ask targeted questions when a change depends on unknown application requirements, image compatibility, resource sizing, persistence, or networking.

Check and improve the selected configuration as appropriate:

- **Syntax and structure:** validate YAML indentation and structure, remove the obsolete top-level `version` attribute, preserve valid Compose specification features, and keep interpolation behavior intact.
- **Completeness:** add `healthcheck`, suitable `restart` policies such as `unless-stopped`, explicit networks and named volumes, and bounded log rotation where these choices are appropriate for the service.
- **Security:** replace `latest` image tags with specific compatible versions when the required version can be established; otherwise flag the tag and ask for the desired version. Identify hardcoded passwords, tokens, API keys, and certificates. Recommend `.env` variables for non-secret configuration and Docker secrets or an external secret manager for sensitive values. Never print or expose secret values.
- **Resources and performance:** add meaningful `deploy.resources.limits` for CPU and memory only when the workload or project constraints justify a safe value. Ask before guessing limits that could prevent a service from operating correctly.
- **Operational behavior:** review service dependencies, readiness versus startup ordering, port exposure, bind mounts, data persistence, least-privilege settings, and network exposure. Do not add privileged mode, broad host mounts, or public ports without a clear requirement.

Explain proposed changes before applying changes that could alter persistence, externally visible ports, credentials, data loss risk, image compatibility, or service availability. Keep edits minimal and do not refactor unrelated project files.

### 3. Validate and optionally run

After optimization, run the following from the directory containing the selected Compose file, or pass the correct `-f` path explicitly:

```bash
docker compose config
```

Resolve all configuration errors before proceeding. If Docker is unavailable, report that validation could not be executed and provide the exact command for the user.

Ask the user for explicit permission before starting containers. With permission, run:

```bash
docker compose up -d
docker compose ps
docker compose logs --tail=100
```

Use `docker compose ps` to confirm service state and inspect health status. Use targeted logs for services that are starting, restarting, unhealthy, or exited. Do not claim that a service is healthy solely because its container is running. If a healthcheck is missing, report that health could not be verified for that service and use logs and service state as secondary evidence.

If startup fails, diagnose the relevant configuration or runtime issue, make only justified corrections, rerun `docker compose config`, and ask again before repeating `docker compose up -d` when the change could recreate or affect containers.

## Safety and decision rules

- Never guess which Compose file to modify when multiple candidates exist.
- Never create services, credentials, image versions, resource limits, or persistent-volume behavior from unsupported assumptions.
- Never echo secret values in output, logs, diffs, or examples.
- Do not automatically pull unpinned images or start containers without user approval.
- Treat destructive operations such as volume removal, container recreation with data impact, or pruning as requiring separate explicit approval.
- Preserve compatibility unless the user approves a breaking change and clearly describe the impact.
- Use English for generated comments, explanations embedded in files, and validation output.

## Expected output

Report:

1. the selected Compose file and any alternatives that required user selection
2. the analysis findings and applied changes
3. security concerns and any unresolved assumptions
4. the exact validation command and its result
5. when approved, service states, health status, and relevant log findings

## Completion checklist

- The correct Compose file was selected explicitly.
- YAML and Compose structure were validated.
- Obsolete syntax and justified best-practice gaps were addressed.
- Image tags, secrets, resource limits, persistence, networking, and logs were reviewed.
- `docker compose config` succeeded, or the unavailable prerequisite was reported.
- Container startup was performed only after explicit approval.
- Service state, healthchecks, and logs were inspected after startup.
- The final report names remaining risks and assumptions.
