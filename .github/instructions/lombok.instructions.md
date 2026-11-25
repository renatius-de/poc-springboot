---
name: lombok-conventions
description: "Use when writing or refactoring Java classes that may use Project Lombok."
applyTo: "**/*.java,**/*.gradle.kts,**/build.gradle.kts"
---

# Lombok Conventions

## Scope

- Apply these rules to every Java source set in all Gradle subprojects.
- Lombok dependencies and annotation processors are centralized in the root
  `build.gradle.kts`. Keep the same configuration for every subproject unless a
  module-specific processor or source-set requirement is documented.
- Preserve Lombok versions managed by the existing Spring and dependency
  management BOMs. Do not introduce a separate Lombok version or duplicate
  module-level processor declarations without a verified compatibility need.

## Safe annotations

- Prefer Java records for immutable DTOs and value objects. Do not replace
  records with Lombok classes.
- Use `@Getter` and `@Setter` only for mutable classes where generated accessors
  preserve the existing public API.
- Use `@Builder` or `@SuperBuilder` only when a builder is already part of the
  contract or when immutable construction clearly benefits the class.
- Use `@NoArgsConstructor`, `@RequiredArgsConstructor`,
  `@AllArgsConstructor`, and `@Slf4j` only when framework or code requirements
  justify them.
- Use explicit constructors or factory methods for validation, invariants,
  generated identifiers, or custom initialization. Do not let Lombok obscure
  lifecycle or constructor behavior.
- Use `@Value` only for genuinely immutable classes. Do not apply it as a
  blanket replacement for mutable POJOs.
- Prefer narrow annotations over `@Data`, especially for persistence entities,
  mutable domain objects, serialization types, and framework-managed classes.

## Prohibited or review-required behavior

- Do not add Lombok-generated `equals`, `hashCode`, or `toString` behavior to
  JPA entities automatically. Preserve custom identity semantics and exclude
  associations and lazy fields.
- Use `@ToString(onlyExplicitlyIncluded = true)` and include only scalar,
  non-sensitive fields when Lombok is appropriate for an entity or document.
- Do not use `@EqualsAndHashCode(callSuper = true)` or `@Data` on JPA entities
  unless the explicit class contract has been verified.
- Do not annotate classes with custom `equals`, `hashCode`, `toString`,
  builders, constructors, serialization, validation, or bean lifecycle behavior
  unless Lombok is proven to generate exactly the existing contract.
- Do not hide existing public constructors or builder methods behind Lombok
  changes. Preserve binary and source APIs, overloads, field visibility, method
  return types, and Jackson, JPA, or MongoDB behavior.
- Do not add Lombok annotations to generated sources or generated gRPC classes.
- Do not add Lombok to test assertions merely to shorten test code.

## Refactoring workflow

- Replace only repetitive boilerplate whose semantics are identical and
  independently verifiable.
- Before refactoring a class, inspect its constructors, framework annotations,
  inheritance, custom methods, serialization configuration, and tests.
- Leave custom equals/hashCode implementations, JPA entities with associations,
  generated classes, and framework lifecycle classes unchanged unless an
  explicit, isolated requirement calls for a change.
- Run `./gradlew test` and the relevant module build after Lombok changes.
- Keep annotation processor configuration compatible with Java releases and
  MapStruct; run annotation-processing-dependent tests after every Lombok
  dependency or processor change.
