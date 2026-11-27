package de.renatius.poc.springboot.rest.testsupport;

import de.renatius.poc.springboot.rest.RestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
    classes = RestApplication.class,
    properties =
        "spring.autoconfigure.exclude="
            + "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
            + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
            + "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration")
@Transactional
@ActiveProfiles("postgresql")
@Testcontainers
public abstract class AbstractTestcontainersTest {

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Container
  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");
  @Container
  @SuppressWarnings ("resource")
  protected static final MongoDBContainer MONGODB =
      new MongoDBContainer("mongo:8").withReplicaSet();

  @SuppressWarnings("unused")
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

  @SuppressWarnings("unused")
  @BeforeEach
  void setUpMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
