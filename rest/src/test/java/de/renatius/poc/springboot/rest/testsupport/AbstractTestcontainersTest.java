package de.renatius.poc.springboot.rest.testsupport;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

import de.renatius.poc.springboot.rest.RestApplication;

@SpringBootTest(classes = RestApplication.class)
@Transactional
public abstract class AbstractTestcontainersTest {

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");

  static {
    POSTGRESQL.start();
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL::getPassword);
    registry.add("app.security.enabled", () -> "false");
  }

  @SuppressWarnings("unused")
  @BeforeEach
  void setUpMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
