package de.renatius.poc.springboot.graphql.testsupport;

import de.renatius.poc.springboot.graphql.GraphqlApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
    classes = GraphqlApplication.class,
    properties =
        "spring.autoconfigure.exclude="
            + "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
            + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
            + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration")
@ActiveProfiles("postgresql")
public abstract class AbstractTestcontainersTest {

  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");

  protected static final MongoDBContainer MONGODB =
      new MongoDBContainer("mongo:8").withReplicaSet();

  static {
    POSTGRESQL.start();
    MONGODB.start();
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL::getPassword);
    registry.add("spring.mongodb.uri", MONGODB::getConnectionString);
    registry.add("spring.data.mongodb.uri", MONGODB::getConnectionString);
    registry.add("spring.data.mongodb.database", () -> "test");
    registry.add("app.security.enabled", () -> "false");
  }
}
