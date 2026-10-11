---
applyTo: "**/src/test/**/*.java"
---

# Unit and integration testing conventions

## Assertions

- Use AssertJ for every test assertion and import it with `import static org.assertj.core.api.Assertions.*;`.
- Do not use JUnit assertion methods or imports, including `assertEquals`, `assertTrue`,
  `assertFalse`, `assertNull`, `assertNotNull`, and `assertThrows`. JUnit annotations and
  lifecycle APIs are allowed.
- Prefer fluent assertions that state the behavior clearly:
  - Equality: `assertThat(actual).isEqualTo(expected);`
  - Boolean: `assertThat(condition).isTrue();`
  - Nullability: `assertThat(value).isNotNull();`
  - Collections: `assertThat(values).hasSize(expectedSize).contains(expectedValue);`
- For exceptions, use `assertThatThrownBy(...)` or `assertThatCode(...)` and assert the relevant
  type, message, or other contract.
- When refactoring tests, preserve their conditions, edge cases, setup, and behavioral coverage.

## Test quality and execution

- Test relevant success, failure, and boundary cases at the appropriate unit or integration level.
- Use existing libraries, fixtures, and module conventions. Keep tests independent of execution
  order and external state unless the test explicitly covers integration with that state.
- Do not remove or weaken unrelated tests.
- Run the narrowest adequate existing Gradle task, such as `./gradlew :data:test`. For
  cross-module or repository-wide changes, use `./gradlew --no-daemon clean build`.
