---
name: jpa-entity-modernization
description: "Analyze and refactor a JPA/Hibernate entity against its actual database table, modernize it with safe Lombok annotations, preserve compatibility, and verify compilation and startup."
---

# JPA Entity Refactoring

Use this skill when a selected `@Entity` class must be compared with its database table, corrected, simplified, or modernized with Project Lombok.

## Preconditions

Before making changes, locate all of the following:

- The selected Java class annotated with `@Entity`.
- Its table mapping from `@Table`, naming conventions, or ORM configuration.
- The authoritative schema source: DDL, Liquibase changelog, Flyway migration, schema dump, or database metadata.
- The build command, Java version, JPA provider, Spring Boot version, and Lombok configuration.
- Representative repositories, services, DTOs, queries, tests, and construction sites.

If the entity or schema cannot be found, stop the refactoring and report the missing input. Do not invent mappings, table constraints, relationships, or migrations.

## Required workflow

### 1. Analyze the entity

Read the complete entity and record:

- `@Entity`, `@Table`, schema, catalog, inheritance, and JPA access strategy.
- Identifier fields, `@Id`, generation strategy, sequence or identity configuration, and version fields.
- Every persistent field, Java type, default value, converter, and validation constraint.
- `@Column` name, length, precision, scale, nullable, unique, insertable, and updatable settings.
- Enum, temporal, UUID, JSON, binary, array, and provider-specific mappings.
- `@ManyToOne`, `@OneToOne`, `@OneToMany`, and `@ManyToMany` relationships.
- Join columns, join tables, ownership, `mappedBy`, cascade, fetch, orphan removal, and ordering.
- Constructors, getters, setters, builders, lifecycle callbacks, `equals`, `hashCode`, and `toString`.

Inspect usages before changing field names, accessor signatures, constructors, mutability, or equality semantics.

### 2. Compare the database table

Compare every entity field with the authoritative table definition. Check:

- Table and column names.
- SQL and Java data types.
- Length, precision, scale, nullable constraints, and defaults.
- Primary keys, generated values, unique constraints, and indexes.
- Foreign keys, referenced columns, cardinality, and join-table structure.
- Delete and update behavior represented by cascade or orphan removal.

Classify differences as:

- **Confirmed defect:** the entity contradicts the schema or established runtime contract.
- **Safe simplification:** redundant code or annotation can be removed without semantic change.
- **Compatibility risk:** a change may affect callers, queries, serialization, generated SQL, or migrations.
- **Unknown:** evidence is incomplete and clarification is required.

Do not alter database migrations or schema structure unless explicitly requested.

### 3. Plan the refactoring

Before editing, state:

- Which mapping corrections are supported by schema evidence.
- Which boilerplate will be replaced with Lombok.
- Which public names, constructors, types, and behaviors remain unchanged.
- Which fetch, cascade, orphan-removal, equality, and lazy-loading decisions are preserved.
- Which compile, test, schema-validation, and startup commands will be executed.

Ask for clarification when the schema source, relationship ownership, equality strategy, migration requirement, or compatibility contract is ambiguous.

### 4. Modernize with Project Lombok

Use the smallest annotation set that preserves the entity contract:

- `@Getter` for generated read access.
- `@Setter` only for intentionally mutable fields.
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` when a protected JPA constructor is appropriate.
- `@AllArgsConstructor` only when its constructor contract is required; retain the JPA no-argument constructor.
- `@Builder` only after checking all construction sites and ensuring required state cannot be bypassed.
- `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` with a deliberate stable identifier or business key.
- `@ToString` only when associations and sensitive fields are explicitly excluded.
- `@Builder.Default` only when the default must apply to builder-created instances.

Preserve domain methods, validation, lifecycle callbacks, and invariant-preserving mutation methods unless generated Lombok methods are demonstrably equivalent.

### 5. Avoid JPA and Lombok anti-patterns

Do not apply `@Data` to an entity by default. Its generated setters, equality, hash code, and string output commonly create persistence defects.

In particular:

- Exclude bidirectional and lazy associations from `toString` to prevent recursion, stack overflows, and unintended lazy loading.
- Exclude associations and mutable fields from `equals` and `hashCode` unless their inclusion is explicitly safe.
- Prefer a stable business key or a carefully designed identifier strategy for equality.
- Ensure equality is compatible with Hibernate proxy subclasses; avoid unsafe exact-class comparisons.
- Do not generate setters that allow invalid aggregate state.
- Do not use an all-arguments constructor as the only constructor for a JPA entity.
- Do not let a builder bypass required fields, validation, or invariants.

### 6. Correct mappings and optimize conservatively

Apply only evidence-based changes:

- Align `@Table`, `@Column`, `@JoinColumn`, and foreign-key metadata with the schema.
- Correct Java types, nullability, lengths, precision, scale, enum strategy, temporal mapping, converters, and generated values.
- Correct relationship ownership and `mappedBy` values.
- Keep collections initialized according to project conventions.
- Prefer `FetchType.LAZY` for relationships unless an explicit use case justifies eager loading.
- Preserve intended cascade and orphan-removal behavior; explain any change and its runtime impact.
- Remove obsolete annotations, redundant mapping methods, and duplicated conversion logic.

Do not change fetch mode, cascade, orphan removal, access type, or equality merely for style. These settings affect runtime behavior and must be justified.

### 7. Check consumers after editing

Search for:

- Field, getter, setter, constructor, and builder references.
- JPQL, Criteria API, Spring Data derived queries, native queries, specifications, and projections.
- DTO mappers, JSON serialization, validation, reflection, auditing, and event handlers.
- Equality and hash-based collection usage.
- Lazy associations accessed outside a transaction or from generated methods.

Confirm that repositories, services, DTOs, serializers, and tests still compile against the entity's field names, types, constructors, accessors, and identity semantics.

### 8. Verify integrity and runtime behavior

Run the narrowest available checks first:

1. Compile with `./mvnw compile`, `mvn compile`, `./gradlew compileJava`, or `gradle compileJava`.
2. Run focused entity, repository, mapping, and persistence tests.
3. Run the relevant project test suite.
4. Enable Hibernate schema validation with `hibernate.ddl-auto=validate` in a safe validation profile and verify persistence-context initialization.
5. Start the Spring Boot application and confirm that entity mappings, proxies, beans, and schema validation initialize without errors.
6. Inspect generated SQL or database metadata when a mapping issue remains uncertain.

Report every command and result. If a database, service, credential, or environment is unavailable, report that limitation instead of claiming success.

## Output format

Return results in this order:

1. **Analysis summary:** entity, table, schema source, framework context, and relevant consumers.
2. **Schema comparison:** matches, confirmed defects, risks, and unresolved questions.
3. **Refactoring plan:** Lombok annotations, mapping corrections, optimization decisions, and preserved contracts.
4. **Implementation summary:** files changed and why.
5. **Verification:** compile, focused tests, full tests, schema validation, and startup results.
6. **Remaining risks:** migration needs, unavailable checks, lazy-loading concerns, and assumptions.

## Self-check

Before completing the task, confirm that:

- A real `@Entity` and an authoritative schema source were identified.
- Every field was compared with its table column, including type, length, nullability, key, and index details.
- Foreign keys, relationship ownership, fetch strategy, cascade, and orphan removal were reviewed.
- No unsafe `@Data`, recursive `toString`, or proxy-hostile equality was introduced.
- The JPA no-argument constructor and required public APIs remain available.
- Lombok annotation processing and generated accessors match existing usages.
- Mapping changes are evidence-based and do not silently require a migration.
- Compilation, tests, `hibernate.ddl-auto=validate`, and application startup were attempted where available.
- Verified facts, unavailable checks, and unresolved risks are clearly separated.

## Anti-patterns

- Do not refactor an entity without reading its schema or explicitly reporting that the schema is unavailable.
- Do not use `@Data` as a shortcut for entity design.
- Do not include bidirectional or lazy associations in generated `toString`, `equals`, or `hashCode`.
- Do not remove the JPA no-argument constructor.
- Do not convert entities to records or immutable DTOs as part of this skill.
- Do not alter migration history or silently change the database contract.
- Do not treat a successful source edit as proof that Hibernate can start.

## Success criteria

The task is complete only when the entity mappings agree with the authoritative schema or every mismatch is documented, Lombok reduces boilerplate without persistence hazards, consumers remain compatible, and all available compile, test, schema-validation, and startup results are reported accurately.