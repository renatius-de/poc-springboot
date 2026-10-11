---
name: java-25-ecosystem-standards
description: "Use when writing, reviewing, refactoring, testing, or configuring Java 25+ applications, Spring Boot, Quarkus, Maven, Gradle, Mockito, Testcontainers, and JUnit 6+."
applyTo: "**/*.java,**/pom.xml,**/build.gradle,**/build.gradle.kts,**/application*.yml,**/application*.yaml,**/*.kt"
---

# Java 25+ Ecosystem Standards

## Scope and priorities

- Apply these rules to Java source, tests, build files, and Spring Boot or Quarkus configuration.
- Preserve the existing public contract, repository conventions, and supported runtime unless the task explicitly changes them.
- Prefer the simplest design that is correct, observable, secure, and easy to maintain.
- Verify the project's actual Java, framework, JUnit, Mockito, and Testcontainers versions before using version-specific APIs.
- Resolve conflicts in this order: explicit user requirements, repository conventions, these standards, then general style preferences.
- This rule covers Java implementation and testing only. It does not define OpenAPI authoring, README approval, or documentation language rules.

## Architecture & coding standards

- Use records consistently for immutable DTOs and value objects. Use a class only when identity, mutability, inheritance, or framework requirements require it.
- Use pattern matching for `instanceof` and `switch`, as well as switch expressions, when this removes casts, flag fields, or nested conditions.
- Use sealed interfaces or classes for closed domain hierarchies and ensure switch expressions cover every permitted variant.
- Use virtual threads by default for I/O-bound workloads and blocking tasks. Define timeouts, cancellation, and failure semantics; do not use them as a substitute for CPU scaling and avoid pinning through long `synchronized` blocks or native calls.
- Use structured concurrency when dependent tasks must be started, completed, or cancelled as a unit and the project version supports it.
- Keep methods small, use guard clauses, and separate validation, orchestration, persistence, and presentation.
- Use constructor injection exclusively and stateless components by default. Make state, lifecycle, and thread-safety requirements explicit when state is necessary.
- Prefer standard Java and framework facilities. Use Lombok or MapStruct only when established by repository convention or when they clearly reduce repetitive code.
- Do not introduce speculative abstractions, global mutable state, unnecessary reflection, or blocking calls into reactive flows.

## Spring Boot 4+ patterns

- Use current Spring Boot 4+ or Quarkus conventions supported by the repository; do not upgrade frameworks as part of an unrelated change.
- Use constructor injection exclusively. Avoid field-level `@Autowired` and setter injection.
- Define configuration with `@ConfigurationProperties` and validated immutable records instead of mutable beans or scattered `@Value` fields.
- Keep web, application, domain, and infrastructure boundaries visible. Use ports or interfaces at real external boundaries, not for every class.
- Use Spring Data repositories for persistence and Flyway or Liquibase for versioned database migrations. Do not change schemas implicitly through automatic DDL generation in production.
- Disable Open-Session-in-View and load required data within explicit transaction boundaries. Avoid lazy loading in the REST response path.
- Use declarative HTTP clients through `@HttpExchange` for external REST communication. Define timeouts, error mapping, idempotency, and retry policies explicitly.
- Handle REST errors centrally with `@RestControllerAdvice` and `ProblemDetail` following RFC 7807. Do not leak internal stack traces, credentials, or sensitive payloads.
- Validate OAuth2/OIDC tokens with issuer, signature, audience, expiry, and clock-skew checks. Map roles and claims explicitly and authorize according to least privilege.
- Instrument relevant business and infrastructure boundaries with Micrometer Observability. Use consistent metric names, tracing context, and structured logs without secrets or personal data.
- Account for AOT and GraalVM Native Image compatibility: prefer static configuration and register required reflection, proxy, or resource hints explicitly.
- Deny by default for protected endpoints. Do not disable TLS verification, CSRF protection, token validation, or authorization to simplify tests or local development.

## Testing standards: AssertJ, Mockito, JUnit 6+, and Testcontainers

### Assertion style and test philosophy

- Use AssertJ as the mandatory assertion library for Java tests. Do not use classic JUnit assertions such as `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, or `assertThrows`.
- Prefer fluent AssertJ chaining that reads as one behavioral statement, for example `assertThat(result).isNotNull().extracting(Result::items).asList().containsExactly(expectedItem)`.
- Use type-specific assertions instead of asserting implementation details: write `assertThat(items).isEmpty()` instead of `assertThat(items.size()).isEqualTo(0)`, and prefer `isTrue()`, `isFalse()`, `isNull()`, `isNotNull()`, `containsExactly(...)`, or `hasSize(...)` where they express the subject directly.
- Use descriptive assertion context with `as(...)` or `describedAs(...)` before the terminal assertion when the failure message benefits from domain meaning.
- Use `assertThatThrownBy(...)` for expected exceptions and chain type, message, cause, and field assertions. Use `assertThatCode(...)` when verifying that an operation completes without an exception or when the thrown type is not the primary subject.
- Use `assertSoftly(...)` for multiple independent attributes of one result so all relevant failures are reported together; use regular fluent assertions when one failure makes subsequent checks meaningless.
- Keep tests deterministic, isolated, and focused on observable behavior. Prefer behavior-oriented checks over structural assertions or mocking deep internal interactions.

### JUnit 6+ modern architecture

- Use JUnit 6+ as the default test framework for lifecycle, parameterization, execution, and extension features.
- Prefer modern extension-based test architecture over legacy JUnit 4 patterns. Use `@ExtendWith(...)` and dedicated extension classes for infrastructure concerns such as time control, mocking, or system integration only when they provide real value.
- Keep the test lifecycle explicit and readable: use `@BeforeEach`, `@AfterEach`, `@BeforeAll`, and `@AfterAll` only for genuinely lifecycle-bound setup or cleanup. Do not rely on hidden global state or implicit ordering.
- Prefer `@TestInstance(Lifecycle.PER_METHOD)` by default. Use `PER_CLASS` only when a test class is intentionally stateful and the lifecycle is clearly documented and thread-safe.
- Avoid obsolete JUnit 4 patterns such as `@RunWith`, `@Before`, `@After`, `@Ignore`, `@Category`, and legacy rule-based tests.
- Use native JUnit support for Java Virtual Threads when running I/O-heavy or blocking test workloads. Keep test executors virtual-thread-friendly, avoid long blocking code paths in tests, and ensure deterministic cleanup for resources and external systems.
- Give each test a single responsibility and keep fixtures minimal. A test should explain one scenario, one expected outcome, and one failure mode.
- Prefer `@Nested` classes to express behavior and state transitions within a single domain area, for example `whenOrderIsValid`, `whenCustomerHasNoBalance`, or `whenInputIsMalformed`.
- Use `@Tag` or project-specific naming conventions for test classification when they help separate unit, integration, smoke, or slow tests without creating implicit execution dependencies.
- Use explicit ordering only when required by the domain; prefer fully independent tests and deterministic execution. Do not rely on test order for correctness.
- Configure parallel execution only when the suite is designed for it: use isolated resources, avoid shared mutable state, and document any required synchronization or resource locking.
- Use `@ResourceLock` or similar synchronization mechanisms when parallel tests must share non-thread-safe infrastructure such as file systems, ports, or broker state.

### Parameterized tests and data-driven design

- Use parameterized tests as the default for data-driven validation, especially for boundary values, validation rules, equivalence classes, and cross-product behavior.
- Prefer type-safe argument providers over ad hoc arrays or loosely typed maps. Model inputs as records when the test case contains multiple dimensions or domain-values.
- Use `@MethodSource` for complex or derived test data, especially when the dataset is computed from Java logic or when a record-based payload improves readability and type safety.
- Use `@ParameterizedTest` together with a dedicated record such as `record ValidationCase(String input, String expectedOutput, boolean shouldFail) {}` and return `Stream<Arguments>` or `Stream<ValidationCase>` as appropriate.
- Keep parameter sets small, explicit, and readable. Prefer naming conventions that communicate the scenario, e.g. `validEmail`, `missingDomain`, or `tooLongUsername`.
- When a test case is a single value, prefer `@ValueSource`, `@EnumSource`, or a focused `@MethodSource` over broad combinatorial data sets that hide the real intent.
- Avoid parameterization that hides the domain scenario behind opaque positional arguments. Use record names, `Arguments.of(...)`, and helper factories to keep failure messages meaningful.
- For assertions involving parameterized cases, include the test case data in the assertion context via `describedAs(...)` or `as(...)` so failed inputs remain obvious.

### Nested tests and domain structure

- Use `@Nested` classes to group related scenarios by behavior, state, or boundary condition rather than by framework lifecycle alone.
- Structure tests so a parent class describes the subject under test and inner classes describe the relevant contexts, for example `CustomerServiceTests` with nested classes for `whenCustomerExists`, `whenCustomerDoesNotExist`, and `whenBalanceIsInsufficient`.
- Keep nested classes focused on one dimension of behavior. Avoid deeply nested test hierarchies that obscure the actual scenario.
- Prefer naming nested classes and methods with domain language, not with implementation details such as helper names or internal method signatures.
- Use nested tests to model business states, validation flows, and edge-case partitions, while keeping shared setup in outer classes and scenario-specific setup in inner classes or lifecycle methods.

### Mockito and external collaborators

- Initialize Mockito unit tests with `@ExtendWith(MockitoExtension.class)` and configure `@MockitoSettings(strictness = Strictness.STRICT_STUBS)`; remove unused stubs.
- Mock only slow, nondeterministic, external, or independently owned collaborators. Use real records, value objects, collections, and pure domain logic.
- Use `when(...).thenReturn(...)` for stable stubs, `thenThrow(...)` for failure paths, and `verify(...)` only for meaningful external side effects.
- Keep Mockito matchers type-safe and consistent within each invocation. Avoid broad `any()`, deep stubs, `reset`, private/static mocking, and `verifyNoMoreInteractions` without a justified legacy exception.
- Use `ArgumentCaptor` only when the captured value is part of the behavior under test. Mock records or sealed types only at real external boundaries; otherwise use concrete variants.
- Keep each test free of incidental mocking. If a collaborator is not part of the observable behavior, remove it from the test.

### Testcontainers and integration tests

- Use Testcontainers for real database, broker, cache, or service integration instead of semantically different in-memory substitutes.
- Prefer `@ServiceConnection`; alternatively use `@DynamicPropertySource` with `DynamicPropertyRegistry` to register container configuration before application context initialization.
- Control container lifecycles through Testcontainers/JUnit. Use class-level lifecycle for immutable shared fixtures and method-level lifecycle for stateful or isolation-critical tests.
- Ensure container isolation through dedicated databases, schemas, topics, or cleanup strategies. Pin images and use meaningful readiness checks instead of `latest` or arbitrary sleeps.
- Use reusable containers only for explicitly enabled local feedback loops. Never rely on reuse in CI, and keep tests correct when reuse is disabled.
- Start container tests only for integration behavior; test business logic with fast unit tests and test framework wiring, serialization, transactions, and security with appropriate slice or integration tests.

## Code style & guardrails

- Preserve public contracts, repository conventions, and supported runtimes unless the task explicitly changes them.
- Manage dependency versions through existing BOMs, version catalogs, or parent builds. Do not suppress compiler, static analysis, or test failures without a documented reason and clear scope.
- Run the narrowest relevant test first, then the module suite, and finally the usual verification task when practical.
- Test affected success, validation, authorization, timeout, cancellation, persistence, and external-service failure paths.
- Avoid obsolete Java, Spring, Quarkus, JUnit, or Mockito patterns, broad security scopes, mutable shared fixtures, flaky timing assumptions, and arbitrary sleeps.
- Prefer simple, explicit solutions with guard clauses and at most two nesting levels. Do not add unnecessary abstractions or duplicate production logic in tests.
