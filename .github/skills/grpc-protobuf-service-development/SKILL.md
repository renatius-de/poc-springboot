---
name: grpc-protobuf-service-development
description: "Design, implement, and validate Spring gRPC services and Protobuf contracts while preserving wire compatibility."
---

# Develop Spring gRPC and Protobuf Services

Use this skill when changing Protobuf definitions, generated gRPC APIs, Spring gRPC service implementations, or gRPC error handling in this repository.

## Scope

- Treat files under `grpc/src/main/proto/` as the source of truth for the gRPC contract. Do not edit generated Java sources.
- Preserve the existing Spring gRPC server, shared data module, and service conventions.
- Keep REST and gRPC behavior aligned for shared domain operations when their contracts describe the same behavior. Do not change the REST API unless it is required and in scope.
- Preserve the repository's REST/gRPC benchmark comparability when changing search, filtering, or pagination behavior.
- Do not introduce streaming RPCs, authentication changes, or new public operations without an explicit requirement.

## Required workflow

### 1. Establish the contract and implementation path

Before editing:

- Read the relevant `.proto` service and message definitions, gRPC implementation, mapper, shared data service or repository, exception support, application configuration, and tests.
- Check the `grpc` module's Protobuf and gRPC Gradle plugin configuration and use the repository's configured versions.
- Trace request validation, domain behavior, persistence, response mapping, status mapping, metadata, and client-visible semantics.
- Check corresponding REST operations and load-test scripts when the change affects shared behavior or REST/gRPC comparison.
- Ask for clarification if the intended wire behavior, compatibility requirement, authorization rule, or data effect is unclear.

### 2. Evolve Protobuf contracts safely

- Preserve existing package names, service names, RPC names, field numbers, and field types unless an approved contract change requires otherwise.
- Never reuse a removed field number or name. Mark retired field numbers and names as `reserved`.
- Never renumber fields or change a field to an incompatible wire type as incidental cleanup.
- Preserve enum numeric values. Reserve removed enum numbers and names; do not repurpose the unspecified/default value.
- Use `optional` when clients must distinguish an omitted scalar from its default value. Document and test presence semantics.
- Review generated JSON names and default-value behavior when changing scalar or enum fields.
- Update dependent clients, tests, and copied API definitions only when those files are part of the repository contract.
- Treat changing update semantics, validation behavior, status codes, or pagination defaults as an API behavior change, even if the Protobuf wire shape remains compatible.

### 3. Implement service behavior

- Follow the existing `@GrpcService` and generated `*ServiceImplBase` pattern.
- Keep RPC methods focused on translating requests, invoking shared domain or persistence behavior, and mapping responses.
- Use existing mappers and validation, paging, specification, and error helpers where applicable; avoid duplicating transport-independent business rules.
- Complete each unary `StreamObserver` exactly once through the established execution/error helper. Do not report success after an error or swallow failures.
- Map known validation, not-found, authentication, and authorization failures to appropriate gRPC statuses using the existing exception support.
- Return generic internal-error details to clients for unexpected failures; do not expose stack traces, SQL, credentials, or sensitive payloads.
- Preserve relevant tracing, correlation metadata, and problem-detail trailer behavior. Do not log request contents that can contain sensitive data.
- Keep page bounds, sort-field allowlists, and filter interpretation aligned with existing REST or documented behavior.

### 4. Test contract and behavior

Add or update focused tests for the changed behavior:

- Protobuf generation and descriptors when the contract changes.
- Mapper behavior when message or DTO mappings change.
- Service success, validation, missing-resource, and authorization/error status behavior.
- Optional-field presence, enum values, pagination boundaries, or wire compatibility when affected.
- Database-backed behavior with the repository's Testcontainers integration-test pattern when persistence is involved.
- REST/gRPC parity and load-test equivalence when shared search behavior changes.

Use the existing JUnit and AssertJ conventions. Prefer in-process gRPC tests for isolated transport behavior and Testcontainers integration tests for persistence behavior. Do not make network or load-test calls to an unconfirmed target.

### 5. Validate and review

Run the narrowest applicable checks first, using the Gradle wrapper. Examples:

```bash
./gradlew :grpc:generateProto
./gradlew :grpc:test
./gradlew :grpc:build
```

Run related `:data`, `:rest`, or full-build tasks only when the change affects those modules or their contracts. Report commands that were unavailable or not run; do not claim wire compatibility based only on successful compilation.

Review the final diff for:

- accidental edits to generated code or unrelated modules
- reused or renumbered Protobuf fields and enum values
- changed defaults, presence semantics, or undocumented API behavior
- inconsistent REST/gRPC behavior or benchmark workloads
- incomplete observer termination, incorrect status mapping, or leaked internal details
- secret, personal-data, or request-payload exposure in logs and examples

## Prohibited patterns

- Do not edit generated sources instead of their `.proto` definitions.
- Do not reuse deleted field tags or enum numbers.
- Do not change RPC behavior or wire compatibility without describing the impact and obtaining required approval.
- Do not bypass shared domain behavior with a second implementation of business rules in the transport layer.
- Do not map every failure to success, `INTERNAL`, or a generic status when the existing exception model can distinguish it.
- Do not weaken authorization or expose sensitive exception details to make a test pass.
- Do not claim compatibility, runtime health, or benchmark results without running the relevant checks.

## Expected output

Report:

1. the affected Protobuf service, RPCs, and compatibility considerations
2. implementation and shared REST/domain behavior affected
3. tests and validation commands run, with results
4. unavailable checks, behavior changes, and remaining risks

## Completion criteria

A gRPC change is complete when the Protobuf source and generated contract agree, service behavior and error semantics are tested, applicable module checks pass, and compatibility and unavailable validations are reported accurately.
