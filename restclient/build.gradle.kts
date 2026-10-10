plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations")
    runtimeOnly("io.opentelemetry:opentelemetry-exporter-otlp")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("com.tngtech.archunit:archunit-junit5")
    testImplementation("io.projectreactor:reactor-test")
}
