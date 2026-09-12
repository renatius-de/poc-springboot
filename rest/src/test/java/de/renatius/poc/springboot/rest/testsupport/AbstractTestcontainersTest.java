package de.renatius.poc.springboot.rest.testsupport;

import de.renatius.poc.springboot.rest.RestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = RestApplication.class)
@Transactional
public abstract class AbstractTestcontainersTest {

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Container
  @ServiceConnection
  protected static final PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>("postgres:17-alpine");

  @BeforeEach
  void setUpMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
