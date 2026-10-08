# Repository skills

## Gradle build and test

When working on this repository:

- use `./gradlew` instead of a system Gradle installation
- prefer targeted verification first, then repository-wide verification when the change affects shared configuration
- keep Java and Spring versions aligned with the root build

## Dependency updates

- update managed versions in the root build when a dependency is centrally managed
- do not duplicate managed versions in module build files
- keep test-only libraries in test scope

## Spring Boot application changes

- keep application bootstrap code simple and conventional
- preserve package boundaries enforced by ArchUnit tests
- prefer existing Spring Boot starters and project libraries over adding new dependencies

## GitHub configuration changes

- keep workflows focused on a single responsibility where practical
- use least-privilege workflow permissions
- keep contributor-facing documentation concrete and repository-specific
- tailor issue templates to backend and build-related use cases instead of generic browser-only forms
