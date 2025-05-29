package de.renatius.poc.springboot.restclient.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {
    // This test will pass if the application context loads successfully.
    // No additional assertions are needed here.
  }
}
