# poc-springboot

A Java 25, Spring Boot multi-module project with REST, gRPC, data, and reactive REST client applications.

## Prerequisites

- Java 25 (the Gradle Wrapper downloads the configured Gradle distribution)
- Docker, for Testcontainers-based integration tests

## Build and test

```bash
./gradlew clean build
```

Run the tests for an individual module:

```bash
./gradlew :rest:test
./gradlew :grpc:test
```

## Run an application

```bash
./gradlew :rest:bootRun
./gradlew :grpc:bootRun
./gradlew :restclient:bootRun
```

The modules are:

- `restclient`: reactive REST client application
- `data`: shared persistence and database migration code
- `rest`: REST API
- `grpc`: gRPC API and protobuf code generation

See [deployment/README.md](deployment/README.md) for Kubernetes load testing and benchmarking.
