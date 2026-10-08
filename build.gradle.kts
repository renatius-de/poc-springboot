import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.protobuf") version "0.9.5" apply false
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
            mavenBom("com.fasterxml.jackson:jackson-bom:2.21.7")
            mavenBom("tools.jackson:jackson-bom:3.1.7")
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.1")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.3")
            mavenBom("org.springframework.grpc:spring-grpc-dependencies:1.0.3")
        }
        dependencies {
            dependency("org.testcontainers:testcontainers-junit-jupiter:2.0.5")
            dependency("org.testcontainers:testcontainers-postgresql:2.0.5")
            dependency("org.mapstruct:mapstruct:1.6.3")
            dependency("org.mapstruct:mapstruct-processor:1.6.3")
            dependency("org.projectlombok:lombok-mapstruct-binding:0.2.0")
            dependency("com.tngtech.archunit:archunit-junit5:1.5.0")
        }
    }

    dependencies {
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
