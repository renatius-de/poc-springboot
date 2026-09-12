package de.renatius.poc.springboot.rest.testsupport;

import de.renatius.poc.springboot.rest.RestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(classes = RestApplication.class)
@Transactional
public abstract class AbstractTestcontainersTest {

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  protected static final PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>("postgres:17-alpine");

  static {
    postgresql.start();
  }

  @DynamicPropertySource
  static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresql::getJdbcUrl);
    registry.add("spring.datasource.username", postgresql::getUsername);
    registry.add("spring.datasource.password", postgresql::getPassword);
  }

  @BeforeEach
  void setUpMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
