package de.renatius.poc.springboot.rest.testsupport;

import de.renatius.poc.springboot.rest.RestApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = RestApplication.class)
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractTestcontainersTest {

  @Container
  @ServiceConnection
  protected static final PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>("postgres:17-alpine");
}
