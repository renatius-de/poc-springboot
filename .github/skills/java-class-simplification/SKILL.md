---
name: java-class-simplification
description: "Refactor complex Java classes to reduce cyclomatic complexity, simplify nested control flow, and modernize legacy code with Java 25 idioms."
---

# Simplify Java Class

Use this skill when a Java class has high cyclomatic complexity, deep nesting, duplicated logic, long methods, or a responsibility boundary that is difficult to reason about. The goal is to modernize and simplify the code while preserving the original business behavior, public API, and runtime semantics.

## Objective

- Reduce cyclomatic complexity and deep nesting.
- Improve readability, maintainability, and testability.
- Use modern Java 25 idioms where they clearly improve clarity and reduce boilerplate.
- Keep the public contract intact unless the user explicitly approves a breaking change.
- Preserve business rules, validation behavior, exception handling, and runtime semantics.

## Mandatory operating rules

1. Analyze the target class before modifying it.
   - Measure or identify high complexity signals: many branches, repeated condition checks, deep nested `if`/`else` blocks, large methods, and repeated validation logic.
   - Look for SRP violations, hidden state transitions, redundant conversions, and coupling that can be extracted into cleaner units.
   - Confirm whether the task is a pure refactor or a functional change.

2. Ask concise clarifying questions before code changes when it is needed.
   - Ask if the business rules are ambiguous or undocumented.
   - Ask if framework constraints, transaction boundaries, external API contracts, or expected exceptions are unclear.
   - Ask if test coverage is missing or whether a behavior-preserving refactor is required.
   - Do not guess when domain intent, expected behavior, or edge-case handling is uncertain.
   - If all business rules and expected behaviors are documented and understood, proceed directly with the refactor.

3. Prefer the simplest correct refactoring.
   - Reduce nested logic with guard clauses and early returns.
   - Extract repeated logic into private methods or helper classes only when it improves clarity.
   - Keep one responsibility per method and make names describe the purpose precisely.
   - Do not introduce broad abstractions for a limited or local problem.

4. Preserve behavior and public API contract.
   - Preserve public method signatures, return types, and parameter contracts as defined in the API documentation.
   - Preserve validation, exception behavior, null handling, serialization compatibility, and cross-method side effects.
   - Do not change runtime semantics to make the code look cleaner.

5. Modernize with Java 25 only when it adds clarity.
   - Use enhanced pattern matching, record patterns, or deconstruction where they reduce branching and improve readability.
   - Prefer `switch` expressions and pattern-based dispatch over long `if`/`else` chains when the code becomes easier to understand.
   - Replace repetitive DTOs with records when they are data carriers with no business logic.
   - Use unnamed variables and unnamed patterns where they improve clarity and remove boilerplate.
   - Use sequenced collections and sequence-aware APIs when they simplify ordering logic.
   - Consider virtual threads for blocking I/O-heavy or high-concurrency tasks when the design matches the runtime model.
   - Use preview or incubating Java 25 features only if the project explicitly permits them and they are appropriate for the codebase.

## Required workflow

### Step 1: Analyze the class

Review the class for:

- Deep nesting and large conditional trees.
- Repeat logic in validation, parsing, branching, and mapping.
- Long methods that mix responsibilities.
- Complex loops that can be simplified.
- Overuse of mutable intermediate states.
- DTOs or data carriers that can become records.
- Concurrency patterns that could benefit from virtual threads or structured concurrency.

Produce a brief complexity summary before editing.

### Step 2: Clarify ambiguity before changes

If any of the following are unclear, request a concise clarification before changing the code:

- business intent behind a rule or decision,
- edge-case behavior,
- expected validation outcome,
- required exception semantics,
- framework constraints,
- persistence or transaction boundaries,
- or test expectations.

If the requirements are clear, skip this step and proceed.

### Step 3: Propose a refactoring strategy

Choose the most appropriate simplification pattern based on the actual code.

Use one or more of the following:

- Replace nested conditionals with pattern matching or `switch` expressions.
- Use a record for data-only models that are currently defined as regular classes or mutable DTOs.
- Extract validation and transformation rules into small private methods with descriptive names.
- Collapse multiple boolean checks into a clearer decision pipeline with guard clauses.
- Replace imperative iteration with stream-based or sequence-based logic when readability improves.
- Convert repeated state checks into a small decision object or enum-based dispatch when it reduces complexity.
- Separate parsing logic from business rules when the class handles both.
- Use virtual threads for a concurrency-heavy task only when the threading model truly matches the workload and the application framework supports it.

### Step 4: Produce the updated code

Return the modernized Java class in a fenced code block.

The class should:

- keep the same public API unless the user explicitly requests a change,
- reduce complexity without changing behavior,
- use Java 25 features where they meaningfully simplify the code,
- remain easy to read and maintain,
- and include only the necessary refactor, not speculative redesign.

### Step 5: Add test recommendations

Provide a concise list of tests to validate the refactor.

Recommended tests include:

- happy path behavior,
- edge-case validation,
- null/empty input handling,
- exception behavior,
- boundary conditions,
- concurrency safety when virtual threads or shared state are involved,
- parser or mapping correctness.

## Java 25-specific guidance

Apply these Java 25 patterns where appropriate:

- Enhanced pattern matching in `instanceof` and `switch`.
- Record patterns for compact deconstruction of complex values.
- Unnamed variables and unnamed patterns to reduce local clutter in short-lived scopes.
- Pattern matching with `switch` expressions for state-based decisions.
- Sequenced collections and sequence-aware iteration for ordered-state logic.
- Flexible constructor bodies and modern constructor patterns where they reduce ceremony.
- Virtual threads for blocking I/O or task-oriented concurrency.
- Structured concurrency and modern executor patterns when they reduce complexity in parallel workflows.
- Preview features only when the project is configured for Java 25 preview support and the code remains stable.

## Refactoring rules

- Prefer readability over cleverness.
- Prefer early returns and guard clauses over deeply nested branching.
- Prefer small, single-purpose methods over giant imperative blocks.
- Prefer modern Java patterns when they reduce boilerplate and do not obscure intent.
- Preserve observable behavior exactly unless explicitly requested otherwise.
- Eliminate duplication only when the extracted logic stays easy to understand.
- Do not over-abstract local logic into a framework-like architecture.

## Anti-patterns to avoid

- Do not refactor without understanding the original behavior.
- Do not change business rules to simplify the code.
- Do not hide failures by swallowing exceptions.
- Do not replace a clear algorithm with a more obscure one.
- Do not overuse Java 25 features where classic Java would be clearer.
- Do not add concurrency or virtual-thread usage without understanding the task and thread model.
- Do not use preview features casually when project constraints or compatibility requirements forbid them.

## Output format

The final response must follow this order:

1. Complexity summary
   - Briefly explain the complexity issues found in the class.

2. Refactoring strategy
   - Summarize the approach, including Java 25 idioms used, if applicable.

3. Refactored Java code
   - Provide the full modernized Java class in a fenced code block.

4. Test recommendations
   - Provide a concise list of relevant unit tests.

5. Complexity reduction summary
   - Add a short bulleted summary of what improved.

## Success criteria

The refactor is successful when:

- the class is easier to read and understand,
- the branching is significantly simpler,
- the public behavior is preserved,
- boilerplate is reduced using Java 25 idioms where appropriate,
- responsibilities are clearer,
- and the code is easier to test and maintain.

## Example triggers

Use this skill for:

- validation classes,
- workflow processors,
- request/response mappers,
- parser or normalization logic,
- rule engines,
- service methods with many conditions,
- or any Java class with excessive branching and low maintainability.
