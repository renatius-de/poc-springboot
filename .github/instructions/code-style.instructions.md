---
applyTo: "**/*"
---

# Formatting and code style

Treat the repository-root `.editorconfig` as authoritative. Every setting in the matching section,
including IntelliJ `ij_*` formatting and generation settings, applies to files you create or edit.
This summary does not replace the complete configuration. Do not extrapolate Java formatting rules
to other languages; consult the matching section whenever a detail is not summarized here.

## Repository-wide formatting

- Use UTF-8 encoding and LF (`\n`) line endings.
- Insert a final newline in every file.
- Use spaces, not tabs. The default indentation and tab width are four spaces; use eight spaces
  for continuation indentation where configured.
- Keep lines at or below the configured 120-character maximum. Wrap long prose and
  expressions deliberately; do not rely on the IDE formatter to wrap Java long lines.
- Keep indentation on blank lines out. Do not add trailing whitespace.
- The EditorConfig sets a 100-character visual guide, disables smart tabs, and disables wrap-on-typing in the IDE.

## Java (`*.java`)

- Indent with four spaces; use end-of-line opening braces for classes, methods, lambdas, and control blocks.
- Put one blank line after the package declaration and after imports, and separate methods and
  classes with one blank line. Do not insert blank lines after class headers or before method
  bodies.
- Use spaces after commas and around binary, assignment, equality, logical, relational, additive,
  and multiplicative operators. Do not put a space before commas or before method/method-call
  parentheses. Use spaces before and after colons where Java syntax uses them.
- Keep simple expressions on separate lines rather than placing multiple expressions on one line.
  Preserve the configured import grouping and ordering.
- Do not manually align consecutive assignments, declarations, or multiline expressions unless
  the corresponding `.editorconfig` option enables it. Follow the configured alignment and
  wrapping rules for annotations, parameters, records, resources, and chained calls.
- Place line comments in the first column when consistent with the configured formatter. Keep
  braces and spacing consistent with existing Java sources.

## Kotlin and Gradle Kotlin DSL (`*.kt`, `*.kts`)

- Use the common four-space indentation, UTF-8, LF, and final-newline rules.
- Use spaces after commas and around binary, assignment, equality, logical, relational, additive,
  and multiplicative operators. Do not put spaces before commas or before type colons; use a
  space after type colons, and spaces before and after supertype colons.
- Do not add trailing commas. Follow the configured import grouping.
- When wrapping parameter or call argument lists, put each item on its own line. Follow the
  configured continuation indentation and wrapping behavior for chained calls, assignments, and
  expressions.

## YAML and protocol buffers (`*.yaml`, `*.yml`, `*.proto`)

- Indent with two spaces (including YAML sequences); never use tabs.
- In YAML, do not put a space before the mapping colon, do not visually align mapping values, and
  preserve configured line breaks.
- In protocol buffers, use spaces after commas and around assignment operators, with no space before commas.

## Markdown (`*.md`, `*.markdown`)

- Put exactly one space after heading markers and list bullets.
- Use one space between words and preserve a quote marker when wrapping blockquote text.
- Wrap long prose and blockquote text where practical, consistent with the 120-character maximum.
- Preserve meaningful code-block formatting and intentional line breaks.

## XML and properties files

- For XML-family files, follow the configured attribute alignment, wrapping, and spacing (no
  spaces around `=` in attributes); preserve meaningful text and whitespace.
- For Java properties files, follow the `.editorconfig` settings and existing file conventions;
  do not align groups of fields or add blank lines unless needed for clarity.

For CSS, JavaScript/TypeScript, shell, JSON, and any other supported file type, follow that file
type's explicit `.editorconfig` section and the nearest existing repository examples.
