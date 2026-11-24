package de.renatius.poc.springboot.data.testsupport;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
public abstract class AbstractTestcontainersTest {

  @Container
  @ServiceConnection
  protected static final PostgreSQLContainer postgresql =
      new PostgreSQLContainer("postgres:18-alpine");
}
