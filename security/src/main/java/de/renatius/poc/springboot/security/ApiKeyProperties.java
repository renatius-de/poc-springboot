package de.renatius.poc.springboot.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record ApiKeyProperties(String apiKey, List<String> corsAllowedOrigins) {
  public ApiKeyProperties {
    corsAllowedOrigins =
        corsAllowedOrigins == null ? List.of() : List.copyOf(corsAllowedOrigins);
  }
}
