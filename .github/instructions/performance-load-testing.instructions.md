---
name: performance-load-testing
description: "Use when changing or running k6, Testkube, or benchmark workflows for REST and gRPC."
applyTo: "deployment/k6/**/*.js,deployment/k6/**/*.proto,deployment/manifests/configmaps/**/*.yaml,deployment/manifests/configmaps/**/*.yml,deployment/manifests/testkube/**/*.yaml,deployment/manifests/testkube/**/*.yml,deployment/run-*.sh"
---

# Performance and Load Testing

## Scope

- Apply these rules to the repository's k6 scripts, Testkube workflows, load-test ConfigMaps, and deployment test runners.
- Preserve the purpose of comparing the REST and gRPC student-search paths under a common workload.
- Treat results from a local Docker Desktop cluster as indicative, not as production capacity claims.

## Required workflow

### 1. Establish the benchmark contract

- Read the k6 scripts, shared workload helpers, protobuf definition, ConfigMaps, Testkube workflows, runner scripts, and relevant deployment documentation before changing the test.
- Confirm target URLs, namespace, service readiness, data setup, test duration, load shape, and the metrics used to judge success.
- Keep the REST and gRPC workloads comparable: use the same arrival-rate profile, seed-data assumptions, search filter, and page size unless the user requests a deliberate difference.
- When changing the API contract or protobuf service/method/message definitions, update dependent load-test clients and copied definitions consistently.

### 2. Keep workloads safe and results meaningful

- Run tests only against an explicitly selected, isolated test environment. Never direct a benchmark at production or a shared environment without explicit approval.
- Account for test setup mutations and generated records. Use disposable test data or an isolated database, and explain cleanup or persistence behavior before changing it.
- Do not increase traffic, duration, virtual users, or thresholds without a stated testing objective and an environment capable of handling the load.
- Keep thresholds explicit and explain changes to them; a threshold failure is a test result, not a reason to suppress checks or report success.
- Capture latency percentiles, throughput, error rate, dropped iterations, and relevant resource measurements. Identify unavailable or best-effort measurements instead of treating missing data as zero.
- Compare runs only when workload, environment, application version, data, and measurement method are sufficiently consistent. Report those conditions and distinguish measured results from conclusions.
- Preserve useful raw output and summarize threshold outcomes, environment, workload profile, and known limitations.

### 3. Validate safely

- Check script syntax and run the narrowest available static or test validation first. Use shell syntax checks for changed runner scripts.
- Ask for explicit permission before building images, applying or deleting Kubernetes resources, invoking Testkube, running a load test, or sending requests to a target service.
- Before an approved run, confirm the Kubernetes context, namespace, target endpoint, expected load, data impact, and cleanup plan.
- Do not delete namespaces, persistent data, or unrelated workloads as benchmark cleanup.
- Review changed scripts for reliable exit status, clear reporting of threshold versus execution failures, and cleanup of processes they start.

## Prohibited patterns

- Do not silently change the REST/gRPC workload equivalence or invent a target endpoint.
- Do not use local benchmark output as a production capacity guarantee.
- Do not hide failed checks, threshold breaches, missing metrics, or runtime errors behind success-shaped summaries.
- Do not run load against an unconfirmed target or make live cluster changes without explicit approval.

## Completion criteria

A load-testing change is complete when its target and impact are clear, compared workloads remain intentional, applicable checks pass, and results or unavailable checks are reported with their limitations.
