package de.renatius.poc.springboot.rest.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalRestExceptionHandlerTest {

  @Test
  void doesNotExposeDatabaseDetailsInBadRequestProblemDetail() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/courses");
    DataIntegrityViolationException exception =
        new DataIntegrityViolationException("constraint fk_internal_schema violated");

    var problem = new GlobalRestExceptionHandler().handleBadRequest(exception, request);

    assertThat(problem.getDetail()).isEqualTo("Request violates a data constraint");
    assertThat(problem.getDetail()).doesNotContain("fk_internal_schema");
  }

  @Test
  void providesFallbackDetailWhenBadRequestExceptionHasNoMessage() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/courses");
    ConstraintViolationException exception = new ConstraintViolationException(null, Set.of());

    var problem = new GlobalRestExceptionHandler().handleBadRequest(exception, request);

    assertThat(problem.getDetail()).isEqualTo("The request is invalid");
  }
}
