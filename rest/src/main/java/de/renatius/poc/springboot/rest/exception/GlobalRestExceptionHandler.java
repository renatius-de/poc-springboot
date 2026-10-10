package de.renatius.poc.springboot.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.time.Instant;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String TYPE_BASE = "https://poc.renatius.de/problems/";

  @ExceptionHandler(ResourceNotFoundException.class)
  ProblemDetail handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
    log.warn("Resource not found: {}", exception.getMessage());
    return problem(HttpStatus.NOT_FOUND, "Resource not found", "not-found", exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    ConstraintViolationException.class,
    DataIntegrityViolationException.class
  })
  ProblemDetail handleBadRequest(Exception exception, HttpServletRequest request) {
    log.warn("Bad request ({}): {}", exception.getClass().getSimpleName(), exception.getMessage());
    log.debug("Bad request stack trace", exception);
    return problem(HttpStatus.BAD_REQUEST, "Bad request", "bad-request", exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(UnauthorizedException.class)
  ProblemDetail handleUnauthorized(UnauthorizedException exception, HttpServletRequest request) {
    log.warn("Unauthorized: {}", exception.getMessage());
    return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "unauthorized", exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ForbiddenException.class)
  ProblemDetail handleForbidden(ForbiddenException exception, HttpServletRequest request) {
    log.warn("Forbidden: {}", exception.getMessage());
    return problem(HttpStatus.FORBIDDEN, "Forbidden", "forbidden", exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
    log.error("Unexpected error", exception);
    return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "internal-error", "Unexpected server error", request.getRequestURI());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception exception,
      Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    ProblemDetail problem =
        body instanceof ProblemDetail existing
            ? existing
            : ProblemDetail.forStatusAndDetail(statusCode, exception.getMessage());
    if (request instanceof ServletWebRequest web && problem.getInstance() == null) {
      problem.setInstance(URI.create(web.getRequest().getRequestURI()));
    }
    enrich(problem);
    return ResponseEntity.status(statusCode).headers(headers).body(problem);
  }

  private static ProblemDetail problem(
      HttpStatus status, String title, String typeSuffix, String detail, String path) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setType(URI.create(TYPE_BASE + typeSuffix));
    problem.setInstance(URI.create(path));
    enrich(problem);
    return problem;
  }

  private static void enrich(ProblemDetail problem) {
    problem.setProperty("timestamp", Instant.now().toString());
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      problem.setProperty("traceId", traceId);
    }
  }
}
