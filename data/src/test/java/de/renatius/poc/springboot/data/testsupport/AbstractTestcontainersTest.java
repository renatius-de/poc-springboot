package de.renatius.poc.springboot.data.testsupport;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestcontainersTest {

  @Container
  @ServiceConnection
  protected static final PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>("postgres:17-alpine");
}
