package de.renatius.poc.springboot.data.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public final class PersistenceEnvironmentPostProcessor
    implements EnvironmentPostProcessor, Ordered {

  private static final String PROPERTY_NAME = "spring.autoconfigure.exclude";
  private static final List<String> POSTGRESQL_AUTOCONFIGURATION =
      List.of(
          "org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration",
          "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration",
          "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration");
  private static final List<String> MONGODB_AUTOCONFIGURATION =
      List.of(
          "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
          "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
          "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
          "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration");

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    Set<String> profiles = Set.of(environment.getActiveProfiles());
    boolean postgresql = profiles.contains("postgresql");
    boolean mongodb = profiles.contains("mongodb");

    if (!postgresql && !mongodb) {
      postgresql = true;
    }

    List<String> exclusions = new ArrayList<>();
    if (postgresql && !mongodb) {
      exclusions.addAll(POSTGRESQL_AUTOCONFIGURATION);
    }
    if (mongodb && !postgresql) {
      exclusions.addAll(MONGODB_AUTOCONFIGURATION);
    }

    if (exclusions.isEmpty()) {
      return;
    }

    String configured = environment.getProperty(PROPERTY_NAME);
    if (configured != null && !configured.isBlank()) {
      Arrays.stream(configured.split(","))
          .map(String::trim)
          .filter(value -> !value.isBlank())
          .forEach(exclusions::add);
    }

    environment
        .getPropertySources()
        .addFirst(
            new MapPropertySource(
                "poc-springboot-persistence-exclusions",
                java.util.Map.of(PROPERTY_NAME, String.join(",", exclusions))));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 20;
  }
}
