import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.protobuf") version "0.10.0" apply false
}

allprojects {
    group = "de.renatius.poc.springboot"
    version = "999.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    extensions.configure<DependencyManagementExtension> {
        imports {
            mavenBom("com.fasterxml.jackson:jackson-bom:2.22.3")
            mavenBom("tools.jackson:jackson-bom:3.2.3")
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.1")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.3")
            mavenBom("org.springframework.grpc:spring-grpc-dependencies:1.1.1")
        }
        dependencies {
            dependency("org.testcontainers:testcontainers-junit-jupiter:2.0.5")
            dependency("org.testcontainers:testcontainers-postgresql:2.0.5")
            dependency("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.32.0")
            dependency("org.mapstruct:mapstruct:1.6.3")
            dependency("org.mapstruct:mapstruct-processor:1.6.3")
            dependency("org.projectlombok:lombok-mapstruct-binding:0.2.0")
            dependency("com.tngtech.archunit:archunit-junit5:1.5.1")
            dependency("com.google.protobuf:protobuf-java:4.36.2")
            dependency("com.google.protobuf:protobuf-java-util:4.36.2")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
            dependency("org.springdoc:springdoc-openapi-starter-common:3.1.1")
            dependency("io.swagger.core.v3:swagger-annotations-jakarta:2.2.55")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.1")
        }
    }

    dependencies {
        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding:0.2.0")
        add("annotationProcessor", "org.mapstruct:mapstruct-processor:1.6.3")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(25)
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
