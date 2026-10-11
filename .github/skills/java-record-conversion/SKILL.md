---
name: java-record-conversion
description: "Safely convert a Java data-oriented class to a record while preserving validation, behavior, API expectations, and relevant builder or wither capabilities."
---

# Convert Java Class to Record

Use this skill when an existing Java class is a data carrier and should be converted to a Java record. The conversion must preserve the class's observable behavior, validation rules, relevant construction patterns, and required framework integration. Do not convert a class automatically when it owns mutable state, identity-based lifecycle behavior, inheritance requirements, or behavior that is incompatible with record semantics.

## Objective

- Convert a suitable class to `public record ClassName(...)`.
- Preserve the public contract, validation behavior, exception behavior, and domain methods unless a change is explicitly requested.
- Remove boilerplate supplied by Lombok or Immutables when the record replaces it.
- Keep construction and usage practical when the original type exposed a builder, withers, serialization contract, or framework-specific integration.
- Verify the complete result after editing.

## Mandatory workflow

### Step 1: Analyze the target class

Read the complete target Java file and inspect nearby tests, usages, and build configuration when they affect the conversion. Before editing, identify:

- Whether Project Lombok is used, including annotations such as `@Data`, `@Value`, `@Getter`, `@Setter`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@NoArgsConstructor`, `@Builder`, `@SuperBuilder`, `@With`, and `@EqualsAndHashCode`.
- Whether the Immutables framework is used, including annotations such as `@Value.Immutable` and related generated immutable types or builders. Consult the official framework documentation or the project's existing usage when the generated API is unclear: https://immutables.github.io/.
- Every instance field, its type, declaration order, visibility, default value, initializer, and whether it is mutable or effectively immutable.
- Existing constructors, constructor chaining, overloads, defensive copies, normalization, and defaulting behavior.
- All methods, including getters, setters, `equals`, `hashCode`, `toString`, builder methods, wither methods, factory methods, validation methods, and domain-specific methods.
- All argument checks, null checks, range checks, cross-field validations, guards, and exceptions thrown by constructors or methods.
- Serialization, persistence, dependency injection, reflection, proxying, ORM, JSON, or other framework requirements that may constrain record conversion.
- Usages of the class so accessor changes and constructor changes can be assessed before the edit.

Write a concise conversion assessment before changing code. If the class cannot satisfy record requirements without a breaking or domain-significant change, stop and ask for clarification instead of guessing.

### Step 2: Determine record compatibility

Convert the class only when its state can be represented by record components and its intended semantics are value-oriented. Confirm that:

- All record components can be declared in the record header in the correct order.
- The class does not require mutable instance fields, instance field reassignment, or a no-argument constructor unless the project has a supported alternative.
- The class does not rely on extending another class. A record may implement interfaces but cannot extend a class.
- The class does not require subclassing or proxy-based behavior that records cannot support.
- Any mutable component is intentionally part of the existing contract; preserve it only when conversion remains behaviorally correct, and consider defensive copying where the original class already provided it.
- Framework support for records is available and configured for the affected serialization, persistence, validation, or dependency injection paths.

Preserve component order and types unless a separate, explicitly approved API change is required.

### Step 3: Apply the transformation

Transform the declaration to the following shape, adapted to the actual components:

```java
public record ClassName(Type firstField, Type secondField) {
    // Compact constructor and domain methods, when required.
}
```

Apply these rules:

- Move all represented instance fields into the record header.
- Remove Lombok and Immutables annotations that the record replaces.
- Remove imports that are no longer used, including imports for removed framework annotations.
- Do not leave generated-code annotations or framework annotations in place unless they are explicitly supported and still required by the project.
- Remove boilerplate getters, setters, generated `equals`, `hashCode`, and `toString` implementations when record-provided behavior is equivalent.
- Preserve domain-specific methods and adapt them to record accessors where necessary.
- Replace accesses such as `getField()` or `field` with `field()` inside the record and update external call sites from bean-style accessors to record-style accessors when the public API change is part of the approved conversion.
- Keep static fields, constants, static factories, nested types, and interface implementations when they remain valid and necessary.
- Preserve method visibility, return types, exception behavior, and side effects unless record semantics require a documented adjustment.

### Step 4: Move validation into a compact constructor

Place existing constructor argument checks and normalization in a compact constructor when they apply to record components:

```java
public record ClassName(String value, int count) {
    public ClassName {
        Objects.requireNonNull(value, "value must not be null");
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
    }
}
```

When moving validation:

- Preserve the original validation order where callers may observe which exception is raised first.
- Preserve exception types and meaningful messages when compatibility requires them.
- Preserve normalization, canonicalization, defensive copying, and defaulting behavior.
- Validate cross-component invariants in the same constructor phase as before.
- Do not add new validation merely because a record is being introduced.
- Do not use explicit field assignments in a compact constructor; record component assignment is implicit.
- Use a canonical constructor only when an explicit constructor body or parameter transformation cannot be expressed clearly in compact form.
- Keep validation helpers private and focused when extracting them improves readability without changing behavior.

### Step 5: Preserve builders and immutable update patterns

Determine whether construction APIs are part of the supported contract before removing them.

- If Lombok `@Builder` is present, inspect all builder usages and generated method expectations. Keep a compatible builder when callers depend on it, either by retaining a supported builder implementation or by replacing it with a small explicit builder that constructs the record through its canonical constructor.
- If `@SuperBuilder` depends on inheritance, do not assume it can be reproduced by a record. Stop and ask for a compatible API design when inheritance-based builder behavior is required.
- If Immutables `@Value.Immutable` is present, inspect usages of the generated immutable type, generated builder, static factory methods, and any framework integration. Remove the annotation only when the record and any explicitly retained construction API provide equivalent required behavior.
- Add a builder only when the original builder is required or the user explicitly requests one. Do not add speculative boilerplate.
- If Lombok `@With` or an equivalent immutable update API is required, add explicit wither methods only for required components, for example `withName(newName)`, returning a new record instance through the canonical constructor.
- Preserve builder validation by routing builder completion through the record constructor rather than duplicating validation.
- Confirm that builder and wither methods do not expose mutable internal state differently from the original implementation.

### Step 6: Update affected usages and tests

Search the workspace for the target type, its constructors, getters, setters, builder calls, withers, generated Immutables names, and serialization or persistence references. Update only the usages required by the approved conversion.

Check specifically for:

- Bean-style getter calls that must use `component()`.
- Direct constructor calls whose argument order or validation behavior must remain unchanged.
- Setter calls that are no longer valid and need an explicit replacement design.
- Lombok builder and Immutables builder calls.
- Reflection, JSON, persistence, dependency injection, and mapping configuration.
- Tests that assert generated `equals`, `hashCode`, `toString`, accessor names, or exact exception behavior.

Do not silently change external contracts. Ask for clarification when callers require both bean-style accessors and record-style accessors, or when a framework cannot consume the record without additional configuration.

### Step 7: Validate the result

After the conversion, read the complete newly created or modified Java file from disk. Then verify:

- The declaration is syntactically valid and uses the intended `public record ClassName(...)` form.
- Every record component has the correct type, name, order, and required annotation.
- Imports are complete and no longer-used imports are removed.
- No obsolete Lombok or Immutables annotations, generated-type references, or unused framework imports remain.
- The compact constructor contains all required original validations, guards, normalization, and defensive-copy behavior.
- No mutable instance fields, setters, illegal instance initializers, or invalid explicit assignments remain.
- Domain-specific methods compile and use record accessors correctly.
- Builder and wither APIs retained by contract still construct valid records and preserve validation.
- Record-provided `equals`, `hashCode`, and `toString` semantics are acceptable for the existing contract.
- Immutability is preserved to the extent promised by the original type; check mutable components and defensive copies explicitly.
- Serialization, persistence, reflection, and validation integrations remain supported.

Run the narrowest available compilation, test, or type-check command for the affected module. Prefer targeted tests covering constructor validation, accessor behavior, equality, builder or wither behavior, serialization, and persistence. If no executable validation is available, report the limitation clearly and perform a careful source-level review.

## Record-specific rules

- A record is shallowly immutable: record components cannot be reassigned, but referenced mutable objects can still be mutated. Do not claim deep immutability without verifying component types and defensive copying.
- Records cannot extend classes, declare additional instance fields, or provide instance setters.
- Records may implement interfaces and may contain static members, nested types, constructors, and domain methods.
- Use the record's canonical constructor as the single validation and construction boundary.
- Prefer the simplest implementation that preserves the original behavior; do not introduce a framework or abstraction solely to make the conversion look uniform.
- Preserve annotations on record components, accessors, constructor parameters, or the record declaration only when their target and framework semantics remain correct after conversion.
- Do not convert an entity, mutable aggregate, framework proxy type, or inheritance-based model without confirming that the target framework supports records and the domain contract permits value semantics.

## Anti-patterns to avoid

- Do not convert a class without reading the entire file and checking representative usages.
- Do not remove builders, withers, factories, or generated APIs without checking whether callers depend on them.
- Do not replace constructor validation with weaker or reordered validation.
- Do not assume a Lombok or Immutables annotation has no external effect.
- Do not retain unused Lombok or Immutables imports or annotations.
- Do not add setters or mutable escape hatches to compensate for record conversion.
- Do not silently change serialization names, persistence mapping, JSON shape, exception types, or accessor contracts.
- Do not claim that a record is deeply immutable when it contains mutable components.
- Do not finish without reading the final Java file and running the narrowest available validation.

## Output format

When applying this skill, report the result in this order:

1. Conversion assessment
   - State why the class is or is not suitable for a record.
   - Summarize Lombok, Immutables, fields, constructors, methods, and validations found.

2. Transformation summary
   - Describe the record components, compact constructor validation, retained domain methods, and any builder or wither compatibility work.

3. Validation result
   - State that the complete Java file was reread.
   - Report source checks and the targeted compile or test command, including any limitations.

4. Remaining risks
   - Call out mutable components, framework compatibility concerns, changed accessor contracts, or unresolved usage assumptions.

## Success criteria

The conversion is successful when:

- The class is a valid Java record with the correct components and public contract.
- Existing validations, guards, normalization, and exception behavior are preserved.
- Unneeded Lombok and Immutables code is removed without breaking required construction APIs.
- Required builders, withers, domain methods, and framework integrations remain available or are explicitly addressed.
- The final source has no syntax, import, or obsolete-annotation issues.
- Record immutability and value semantics are understood and correctly applied.
- The affected code passes the narrowest practical compilation and behavior checks.
