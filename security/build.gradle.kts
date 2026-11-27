plugins {
    id("java-library")
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core")
    compileOnly("io.grpc:grpc-api")
    compileOnly("org.springframework.grpc:spring-grpc-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework:spring-webmvc")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
    testImplementation("io.grpc:grpc-api")
}
