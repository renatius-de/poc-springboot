package de.renatius.poc.springboot.graphql.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.graphql.ExecutionGraphQlService;

import java.io.IOException;

class GraphqlApplicationIT extends AbstractTestcontainersTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void shouldLoadGraphqlApplicationContext() throws IOException {
    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.containsBean("academicController")).isTrue();
    assertThat(
            new PathMatchingResourcePatternResolver(applicationContext.getClassLoader())
                .getResources("classpath*:graphql/*.graphqls"))
        .hasSize(1);
    assertThat(applicationContext.getBeansOfType(ExecutionGraphQlService.class))
        .as(
            "GraphQL schema locations: %s",
            applicationContext.getEnvironment().getProperty("spring.graphql.schema.locations"))
        .hasSize(1);
  }
}
