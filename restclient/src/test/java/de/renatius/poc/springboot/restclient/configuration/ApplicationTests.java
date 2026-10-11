package de.renatius.poc.springboot.restclient.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.restclient.assertions.ApplicationClassAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    assertThat(applicationContext).isNotNull();
    ApplicationClassAssert.assertThat(Application.class)
        .isSpringBootApplication()
        .hasStaticMainMethod();
  }
}
