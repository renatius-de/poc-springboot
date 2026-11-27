import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("org.springframework.boot") version "4.2.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.protobuf") version "0.10.1" apply false
    id("info.solidsoft.pitest") version "1.19.0" apply false
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
    apply(plugin = "info.solidsoft.pitest")

    extensions.configure<PitestPluginExtension> {
        pitestVersion.set("1.30.2")
        junit5PluginVersion.set("1.2.3")
        addJUnitPlatformLauncher.set(false)
        threads.set(4)
        targetClasses.set(setOf("${project.group}.*"))
        outputFormats.set(setOf("HTML", "XML"))
    }

    tasks.named("check") {
        dependsOn("pitest")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    extensions.configure<DependencyManagementExtension> {
        imports {
            mavenBom("com.fasterxml.jackson:jackson-bom:2.23.0")
            mavenBom("tools.jackson:jackson-bom:3.3.0")
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.2.0")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2026.0.0")
            mavenBom("org.springframework.grpc:spring-grpc-dependencies:1.1.2")
        }
        dependencies {
            dependency("org.testcontainers:testcontainers-junit-jupiter:2.0.5")
            dependency("org.testcontainers:testcontainers-postgresql:2.0.5")
            dependency("org.testcontainers:testcontainers-mongodb:2.0.5")
            dependency("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.34.0")
            dependency("com.google.code.findbugs:jsr305:3.0.2")
            dependency("org.mapstruct:mapstruct:1.6.3")
            dependency("org.mapstruct:mapstruct-processor:1.6.3")
            dependency("org.projectlombok:lombok-mapstruct-binding:0.2.0")
            dependency("com.tngtech.archunit:archunit-junit5:1.5.1")
            dependency("com.google.protobuf:protobuf-java:4.36.2")
            dependency("com.google.protobuf:protobuf-java-util:4.36.2")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")
            dependency("org.springdoc:springdoc-openapi-starter-common:3.2.0")
            dependency("io.swagger.core.v3:swagger-annotations-jakarta:2.2.56")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-api:3.2.0")
        }
    }

    dependencies {
        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding:0.2.0")
        add("annotationProcessor", "org.mapstruct:mapstruct-processor:1.6.3")
        add("testCompileOnly", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok")
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

    val sourceSets = extensions.getByType<SourceSetContainer>()
    val integrationTest = sourceSets.create("integrationTest")
    integrationTest.compileClasspath += sourceSets["main"].output
    integrationTest.runtimeClasspath += sourceSets["main"].output
    configurations["integrationTestImplementation"].extendsFrom(
        configurations["implementation"],
        configurations["testImplementation"]
    )
    configurations["testCompileOnly"].extendsFrom(configurations["compileOnlyApi"])
    configurations["integrationTestCompileOnly"].extendsFrom(configurations["compileOnlyApi"])
    configurations["integrationTestRuntimeOnly"].extendsFrom(
        configurations["runtimeOnly"],
        configurations["testRuntimeOnly"]
    )

    val integrationTestTask = tasks.register<Test>("integrationTest") {
        description = "Runs integration tests with the *IT naming convention"
        group = "verification"
        testClassesDirs = integrationTest.output.classesDirs
        classpath = integrationTest.runtimeClasspath
        useJUnitPlatform()
        include("**/*IT.class")
    }

    tasks.named("check") {
        dependsOn(integrationTestTask)
    }
}
