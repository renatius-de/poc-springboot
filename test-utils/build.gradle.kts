plugins {
    id("java-library")
}

sourceSets {
    main {
        java.srcDir("src/test/java")
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-testcontainers")
    api("org.testcontainers:testcontainers-junit-jupiter")
    api("org.testcontainers:testcontainers-postgresql")
    api("org.testcontainers:testcontainers-mongodb")
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}
