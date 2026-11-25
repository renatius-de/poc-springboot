package de.renatius.poc.springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApiKeyProperties.class)
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiKeySecurityConfiguration {

  @Bean
  FilterRegistrationBean<ApiKeyServletFilter> apiKeyServletFilter(ApiKeyProperties properties) {
    var registration = new FilterRegistrationBean<>(new ApiKeyServletFilter(properties));
    registration.setName("apiKeyServletFilter");
    registration.setOrder(-50);
    registration.addUrlPatterns("/*");
    return registration;
  }

  @Bean
  CorsFilter corsFilter(ApiKeyProperties properties) {
    var cors = new CorsConfiguration();
    cors.setAllowedOrigins(properties.corsAllowedOrigins());
    cors.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(java.util.List.of("X-API-Key", "Content-Type", "Authorization"));
    cors.setAllowCredentials(true);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return new CorsFilter(source);
  }

  public static final class ApiKeyServletFilter extends OncePerRequestFilter {
    private final ApiKeyProperties properties;

    ApiKeyServletFilter(ApiKeyProperties properties) {
      this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      if ("OPTIONS".equalsIgnoreCase(request.getMethod())
          || request.getRequestURI().startsWith("/actuator")) {
        filterChain.doFilter(request, response);
        return;
      }
      String configuredApiKey = properties.apiKey();
      String requestApiKey = request.getHeader("X-API-Key");
      if (configuredApiKey == null || configuredApiKey.isBlank()) {
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
            "API key configuration is missing");
        return;
      }
      if (!java.util.Objects.equals(configuredApiKey, requestApiKey)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            """
            {"type":"https://poc.renatius.de/problems/unauthorized","title":"Unauthorized","status":401,"detail":"A valid X-API-Key is required","instance":"%s","timestamp":"%s"}
            """.formatted(request.getRequestURI(), Instant.now()));
        return;
      }
      filterChain.doFilter(request, response);
    }
  }
}
