package de.renatius.poc.springboot.rest.exception;

import de.renatius.poc.springboot.data.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ApiErrorResponse> handleNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    log.warn("Resource not found: {}", exception.getMessage());
    return build(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    ConstraintViolationException.class,
    DataIntegrityViolationException.class
  })
  ResponseEntity<ApiErrorResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
    log.warn("Bad request ({}): {}", exception.getClass().getSimpleName(), exception.getMessage());
    log.debug("Bad request stack trace", exception);
    return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(UnauthorizedException.class)
  ResponseEntity<ApiErrorResponse> handleUnauthorized(
      UnauthorizedException exception, HttpServletRequest request) {
    log.warn("Unauthorized: {}", exception.getMessage());
    return build(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ForbiddenException.class)
  ResponseEntity<ApiErrorResponse> handleForbidden(
      ForbiddenException exception, HttpServletRequest request) {
    log.warn("Forbidden: {}", exception.getMessage());
    return build(HttpStatus.FORBIDDEN, exception.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
    log.error("Unexpected error", exception);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request.getRequestURI());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception exception,
      Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    HttpStatus status = HttpStatus.resolve(statusCode.value());
    String message = body instanceof ProblemDetail problem && problem.getDetail() != null
        ? problem.getDetail()
        : exception.getMessage();
    String path = request instanceof ServletWebRequest web ? web.getRequest().getRequestURI() : null;
    ApiErrorResponse response =
        ApiErrorResponse.of(
            statusCode.value(), status != null ? status.getReasonPhrase() : "Error", message, path);
    return ResponseEntity.status(statusCode).headers(headers).body(response);
  }

  private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, String path) {
    return ResponseEntity.status(status)
        .body(ApiErrorResponse.of(status.value(), status.getReasonPhrase(), message, path));
  }
}
