package de.renatius.poc.springboot.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ApiKeyServletFilterTest {

  @Test
  void shouldContinueWhenApiKeyMatches() throws Exception {
    var request = new MockHttpServletRequest("GET", "/api/students");
    request.addHeader("X-API-Key", "expected-key");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();
    var filter =
        new ApiKeySecurityConfiguration.ApiKeyServletFilter(
            new ApiKeyProperties("expected-key", List.of()));

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isSameAs(request);
  }

  @Test
  void shouldRejectMissingApiKeyWithUnauthorizedProblemResponse() throws Exception {
    var request = new MockHttpServletRequest("GET", "/api/students");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();
    var filter =
        new ApiKeySecurityConfiguration.ApiKeyServletFilter(
            new ApiKeyProperties("expected-key", List.of()));

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentType()).startsWith("application/problem+json");
    assertThat(response.getContentAsString())
        .contains("\"detail\":\"A valid X-API-Key is required\"");
    assertThat(chain.getRequest()).isNull();
  }

  @Test
  void shouldReturnServiceUnavailableWhenApiKeyIsNotConfigured() throws Exception {
    var request = new MockHttpServletRequest("GET", "/api/students");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();
    var filter =
        new ApiKeySecurityConfiguration.ApiKeyServletFilter(
            new ApiKeyProperties(" ", List.of()));

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(503);
    assertThat(response.getErrorMessage()).isEqualTo("API key configuration is missing");
    assertThat(chain.getRequest()).isNull();
  }

  @Test
  void shouldBypassApiKeyForOptionsAndActuatorRequests() throws Exception {
    var filter =
        new ApiKeySecurityConfiguration.ApiKeyServletFilter(new ApiKeyProperties(null, List.of()));
    var optionsRequest = new MockHttpServletRequest("OPTIONS", "/api/students");
    var optionsResponse = new MockHttpServletResponse();
    var optionsChain = new MockFilterChain();
    var actuatorRequest = new MockHttpServletRequest("GET", "/actuator/health");
    var actuatorResponse = new MockHttpServletResponse();
    var actuatorChain = new MockFilterChain();

    filter.doFilter(optionsRequest, optionsResponse, optionsChain);
    filter.doFilter(actuatorRequest, actuatorResponse, actuatorChain);

    assertThat(optionsResponse.getStatus()).isEqualTo(200);
    assertThat(optionsChain.getRequest()).isSameAs(optionsRequest);
    assertThat(actuatorResponse.getStatus()).isEqualTo(200);
    assertThat(actuatorChain.getRequest()).isSameAs(actuatorRequest);
  }
}
