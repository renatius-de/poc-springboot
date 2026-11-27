package de.renatius.poc.springboot.graphql.testsupport;

import de.renatius.poc.springboot.graphql.GraphqlApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
    classes = GraphqlApplication.class,
    properties = {
      "spring.autoconfigure.exclude="
          + "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
          + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
          + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration",
      "spring.graphql.schema.locations=classpath*:graphql/"
    })
@ActiveProfiles("postgresql")
@Testcontainers
public abstract class AbstractTestcontainersTest {

  @Container
  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");

  @Container
  @SuppressWarnings("resource")
  protected static final MongoDBContainer MONGODB =
      new MongoDBContainer("mongo:8").withReplicaSet();

  @DynamicPropertySource
  static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL::getPassword);
    registry.add("spring.mongodb.uri", MONGODB::getConnectionString);
    registry.add("spring.mongodb.database", () -> "test");
    registry.add("spring.mongodb.representation.uuid", () -> "standard");
    registry.add("app.security.enabled", () -> "false");
  }
}
