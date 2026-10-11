---
name: openapi-java-documentation
description: "Create and maintain high-quality OpenAPI 3 documentation for Java controllers, DTOs, and application APIs using framework-compatible tooling."
---

# Document Java APIs with OpenAPI 3

Use this skill when adding, reviewing, or improving OpenAPI 3 documentation for Java REST APIs, especially APIs implemented with Spring MVC, Spring WebFlux, Jakarta REST, or another framework supported by Swagger Core or springdoc-openapi.

## Goals

Produce API documentation that is:

- accurate to the implemented HTTP contract
- useful to API consumers and client generators
- consistent across controllers, DTOs, errors, and security requirements
- compatible with the project's actual OpenAPI tooling and version
- explicit about validation, nullability, examples, authorization, and failure responses
- maintainable without duplicating descriptions or inventing behavior

Treat the generated OpenAPI document as the contract. An annotation is successful only when it produces the intended paths, operations, parameters, schemas, responses, and security metadata.

## Required working method

### 1. Establish the project contract

Before changing annotations or configuration, inspect:

- the Java and framework versions
- the existing OpenAPI dependency and its version
- the generated-document configuration and available endpoint
- controller mappings, request parameters, request bodies, response types, and exception handlers
- DTO validation annotations and serialization behavior
- authentication, authorization, and API versioning conventions
- existing OpenAPI annotations and nearby documentation patterns

Use the project's established integration. Do not upgrade springdoc-openapi, Swagger Core, Jackson, or the web framework as part of a documentation-only change unless explicitly requested.

If the source code and requested documentation disagree, document the behavior that is actually implemented only after resolving whether the implementation or the contract is authoritative. Do not guess missing business rules, security requirements, status codes, or field semantics.

### 2. Choose the annotation boundary

Keep documentation close to the code that owns the behavior:

- Put `@Tag` on a controller or resource class when it groups related operations.
- Put `@Operation` on each public endpoint when its summary, description, operation ID, tags, or security differs from defaults.
- Put `@Parameter` on path, query, header, and cookie parameters when names, constraints, examples, or descriptions are not inferred correctly.
- Put `@RequestBody` on the operation when media types, requiredness, examples, or descriptions need explicit control.
- Put `@ApiResponse` on the operation for every meaningful success and failure response.
- Put `@Schema`, `@ArraySchema`, and field-level schema annotations on DTOs when Java types, validation metadata, serialization, or examples are insufficient for an accurate schema.
- Use reusable components or configuration for cross-cutting descriptions, common responses, security schemes, and shared parameters when the integration supports them.
- Use `@Hidden` only for endpoints or models that must intentionally be absent from the public contract. Never use it to conceal undocumented behavior accidentally.

Prefer annotations that describe observable HTTP behavior over comments about implementation details.

### 3. Document controllers and operations

For every exposed operation:

- Provide a concise, verb-led summary.
- Provide a description when behavior, side effects, authorization, filtering, pagination, idempotency, or constraints need explanation.
- Define a stable, unique `operationId` using the project's naming convention. Do not derive IDs from unstable method names if overloads or refactoring could cause collisions.
- Ensure the documented HTTP method and path match the framework mapping exactly.
- Document path variables, query parameters, headers, and cookies with their location, requiredness, type, format, allowed values, defaults, constraints, and realistic examples.
- Document request and response media types explicitly when an endpoint supports more than the default or uses content negotiation.
- Document whether an operation is idempotent, asynchronous, paginated, or subject to conditional requests when that is part of the HTTP contract.
- Avoid exposing internal service, persistence, or implementation terminology in consumer-facing descriptions.

Do not use an annotation to promise filtering, sorting, retries, asynchronous processing, or status codes that the endpoint does not implement.

### 4. Model request and response schemas

Use dedicated request and response DTOs when they represent different contracts. Do not expose persistence entities merely because they are convenient return types.

For DTO fields:

- Use `@Schema` descriptions that explain meaning, units, accepted values, and important constraints.
- Keep schema names stable and consumer-friendly.
- Mark fields as required, nullable, read-only, or write-only according to runtime serialization and validation behavior.
- Align `@Schema` constraints with Jakarta Bean Validation annotations such as `@NotNull`, `@NotBlank`, `@Size`, `@Pattern`, `@Min`, `@Max`, `@Positive`, and `@Email`.
- Use the correct OpenAPI `format`, such as `date`, `date-time`, `uuid`, `email`, or a supported numeric format.
- Document enums with meaningful descriptions. Preserve the serialized enum values rather than Java constant names when Jackson or another serializer changes them.
- Document arrays with item schemas, bounds, ordering, and uniqueness when relevant.
- Document maps with value schemas and key restrictions when relevant.
- Add realistic examples that satisfy the schema and do not contain credentials, tokens, personal data, or misleading placeholder values.

Use Java and Jackson configuration as the source of truth for property names, inclusion rules, date/time representation, and polymorphic serialization. Annotation metadata must not contradict the serialized JSON.

Use `@Schema(oneOf = ...)`, `@Schema(anyOf = ...)`, or the integration's supported polymorphism mechanism only when the wire format is genuinely polymorphic. Define a discriminator and stable mapping when clients need to select a concrete subtype. Avoid undocumented inheritance that produces ambiguous generated schemas.

### 5. Describe responses and errors

Document at least the successful response and every client-visible failure that can occur through the endpoint's exception handling. Common responses include:

- `200` for a successful read or update with a response body
- `201` for creation, including the representation and `Location` semantics when applicable
- `202` for accepted asynchronous work, including how completion is tracked
- `204` for success with no response body
- `400` for malformed or invalid input
- `401` for missing or invalid authentication
- `403` for authenticated callers without permission
- `404` for an unknown resource
- `409` for a documented state or uniqueness conflict
- `422` only when the API deliberately distinguishes semantically invalid input from `400`
- `429` when rate limiting is part of the contract
- `500` or other server errors when the public API documents their response shape

For each response, specify:

- the status code and a clear description
- the response media type
- the response schema or an explicit empty body
- representative examples where they improve consumer understanding
- headers such as `Location`, `ETag`, `Retry-After`, or pagination links when returned

Use one consistent error representation, preferably RFC 7807 `ProblemDetail` or the project's established equivalent. Document stable error fields, validation details, correlation identifiers, and error codes without exposing stack traces, SQL, internal class names, or secrets. Centralize reusable error responses when the toolchain supports component references.

### 6. Document security and authorization

Define security schemes through the integration's supported configuration or annotations, then apply requirements at the API or operation level:

- Use HTTP bearer authentication for bearer tokens and document the token format when relevant.
- Use OAuth2 flows and scopes that match the identity provider and authorization checks.
- Use API keys only in the actual header, query, or cookie location used by the application.
- Use `@SecurityRequirement` or the equivalent to override global security for public endpoints or to document operation-specific scopes.
- Describe authorization requirements in operation documentation when roles, ownership, or tenant boundaries affect access.

Do not document authentication that is not enforced, claim that an endpoint is public when middleware protects it, or include real credentials in examples. Keep security metadata synchronized with the application's actual security configuration.

### 7. Configure the generated document

Keep global metadata in the project's OpenAPI configuration rather than repeating it on controllers:

- title, description, version, and contact
- server URLs and environment-specific concerns
- reusable tags, security schemes, common parameters, and common responses
- API grouping or package scanning rules
- endpoint exposure and access control for the documentation UI and JSON/YAML document

Use the OpenAPI version supported by the installed integration and downstream tooling. Do not claim OpenAPI 3.1 features when the generator or client tooling emits or consumes OpenAPI 3.0. Confirm that customizers, filters, and model converters preserve the intended contract.

Never put secrets, internal hostnames, production tokens, or environment-specific credentials in committed OpenAPI configuration or examples.

### 8. Verify the result

After changing Java annotations or OpenAPI configuration:

1. Compile the affected module with the repository's normal build command.
2. Run focused controller, serialization, or documentation tests when available.
3. Generate or fetch the OpenAPI JSON or YAML document from the application or build plugin.
4. Validate the document with the project's OpenAPI validator.
5. Inspect the changed operations and schemas for accurate paths, methods, parameters, required fields, media types, examples, response codes, and security requirements.
6. Confirm every `$ref` resolves and every `operationId` is unique.
7. Check that generated client or documentation tooling still processes the document successfully when those checks exist.
8. Review the diff for accidental exposure of internal models, endpoints, fields, or secrets.

When the repository has no Java application, do not invent compile or runtime evidence. Verify the skill or documentation artifact itself and report application-level checks that were unavailable.

## Quality rules

- Prefer explicit, consumer-oriented documentation over annotation density.
- Keep descriptions concise but complete; explain constraints and behavior that a client must know.
- Reuse schemas and responses instead of copy-pasting nearly identical definitions.
- Preserve backward compatibility unless a breaking API change is explicitly approved.
- Treat changing a property name, requiredness, enum value, format, status code, security requirement, or response shape as a contract change.
- Keep generated documentation deterministic across environments.
- Use ASCII in source documentation unless the API contract requires another character set.
- Keep all skill instructions and generated guidance in English.

## Decision rules

Proceed directly when the endpoint behavior, tooling version, and desired contract are clear.

Ask for clarification before documenting when any of these are unknown and materially affect the contract:

- whether the API emits OpenAPI 3.0 or 3.1
- which library owns annotation processing
- the serialized shape or requiredness of a field
- the supported authentication scheme or scopes
- the intended error format or status code
- whether a proposed change is backward-compatible

## Expected output

A completed OpenAPI documentation change should include:

- accurate Java annotations or configuration aligned with the existing integration
- complete operation, parameter, schema, response, and security metadata
- realistic examples and reusable error documentation
- validation evidence for the generated OpenAPI document
- a concise report of unavailable checks, assumptions, and any contract changes

## Final instruction

Document the implemented Java HTTP API with the smallest complete set of OpenAPI 3 annotations and configuration needed for a reliable consumer contract. Keep the generated specification accurate, reusable, secure, compatible with the project's tooling, and validated before considering the work complete.
