---
name: kubernetes-manifest-standards
description: "Use when authoring, reviewing, or validating Kubernetes manifests and Helm values."
applyTo: "**/deployment/manifests/**/*.yaml,**/deployment/manifests/**/*.yml,**/deployment/*-values.yaml,**/deployment/*-values.yml,**/k8s/**/*.yaml,**/k8s/**/*.yml,**/kubernetes/**/*.yaml,**/kubernetes/**/*.yml,**/helm/**/*.yaml,**/helm/**/*.yml,**/charts/**/*.yaml,**/charts/**/*.yml"
---

# Kubernetes Manifest Standards

## Scope

- Apply these rules to Kubernetes resources, Helm values, and chart templates.
- Preserve the repository's deployment intent, target environment, and existing resource relationships.
- Treat Docker Desktop load-testing resources as local test infrastructure, not as production defaults.
- Do not assume a Kubernetes context, cluster version, or Helm release is available.

## Required workflow

### 1. Establish context

- Read related manifests, overlays, Helm values, Dockerfiles, scripts, and deployment documentation before editing.
- Determine the target environment, namespace, Kubernetes version, image source, ports, health endpoints, storage behavior, and required access from repository evidence.
- Ask for missing information when a change depends on an unknown environment or would alter availability, access, persistence, or compatibility.
- Keep edits limited to the requested resources and directly required references.

### 2. Maintain declarative and compatible resources

- Keep desired state in version-controlled manifests or Helm sources; do not make imperative changes the only record of configuration.
- Use explicit, compatible API versions and stable resource names. Preserve namespace and label/selector consistency.
- Pin deployable image versions or digests where the environment supports them. Preserve documented local image tags such as `:local` when they are part of the Docker Desktop workflow.
- Do not add credentials or private keys to manifests, Helm values, examples, logs, or diffs. Use the repository's established secret mechanism; ask before introducing a new one.
- Treat local test credentials as non-production fixtures. Never reuse them in shared environments.

### 3. Apply operational and security settings

- Set CPU and memory requests and limits using measured usage or explicit requirements. If safe values are unknown, identify the gap rather than inventing resource sizing.
- Configure startup, readiness, and liveness probes for long-running containers only when a reliable application check exists. Do not add probes to one-shot Jobs.
- Apply least privilege: avoid privileged containers, host namespaces, host paths, unnecessary capabilities, and unnecessary service-account token mounts.
- Configure storage and cleanup deliberately. Explain data-retention or availability effects before changing volumes, replicas, selectors, or rollout behavior.
- Avoid public exposure unless the deployment contract requires it. Restrict service and network access to the intended clients.
- Keep labels, annotations, resource selectors, and service selectors aligned. Do not change immutable selectors as incidental cleanup.

### 4. Validate without changing live state

- Validate YAML and inspect the complete rendered output after changing Helm values or templates.
- Use available local checks such as `helm lint`, `helm template`, or `kubectl apply --dry-run=client` for applicable manifests. Report unavailable tools, schemas, or cluster-dependent checks.
- Do not apply, upgrade, delete, or otherwise change live cluster resources without explicit user approval for that operation.
- Treat namespace deletion, volume removal, and resource replacement as separately approval-gated when data or availability could be affected.
- Review the final diff for secret exposure, privilege escalation, unintended public access, missing required resources, selector drift, and destructive storage changes.

## Prohibited patterns

- Do not guess cluster versions, image compatibility, ports, health checks, credentials, resource limits, or data-retention requirements.
- Do not use floating image tags for deployable environments unless the existing workflow explicitly requires them.
- Do not weaken TLS, authentication, authorization, or workload isolation to make deployment succeed.
- Do not claim a manifest is cluster-valid based only on YAML parsing or a client-side check.

## Completion criteria

A Kubernetes change is complete when its deployment context and assumptions are clear, related resources remain consistent, applicable validation has run, and no live cluster changes occurred without explicit approval.
