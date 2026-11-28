package de.renatius.poc.springboot.testsupport;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

public abstract class AbstractPostgresMongoTestcontainersTest {

  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");

  @SuppressWarnings("resource")
  protected static final MongoDBContainer MONGODB =
      new MongoDBContainer("mongo:8").withReplicaSet();

  static {
    POSTGRESQL.start();
    MONGODB.start();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  MONGODB.close();
                  POSTGRESQL.close();
                }));
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL::getPassword);
    registry.add("spring.mongodb.uri", MONGODB::getConnectionString);
    registry.add("spring.data.mongodb.uri", MONGODB::getConnectionString);
    registry.add("spring.mongodb.database", () -> "test");
    registry.add("spring.mongodb.representation.uuid", () -> "standard");
    registry.add("app.security.enabled", () -> "false");
  }
}
