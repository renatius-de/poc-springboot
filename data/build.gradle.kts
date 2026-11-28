dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
    api("org.springframework.boot:spring-boot-flyway")
    api("org.flywaydb:flyway-core")
    api("org.flywaydb:flyway-database-postgresql")
    api("org.mapstruct:mapstruct")
    api("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations")
    api("io.opentelemetry:opentelemetry-api")

    compileOnlyApi("io.swagger.core.v3:swagger-annotations-jakarta")
    compileOnlyApi("com.google.code.findbugs:jsr305")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.testcontainers:testcontainers-mongodb")
    testImplementation(project(":test-utils"))
}
