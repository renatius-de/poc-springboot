package de.renatius.poc.springboot.graphql.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class GraphqlApplicationIT extends AbstractTestcontainersTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void shouldLoadGraphqlApplicationContext() {
    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.containsBean("academicController")).isTrue();
  }
}
