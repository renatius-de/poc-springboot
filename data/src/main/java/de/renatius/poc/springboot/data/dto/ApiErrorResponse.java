package de.renatius.poc.springboot.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.slf4j.MDC;

@Schema(description = "Unified error response returned for all failed REST calls")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    @Schema(description = "Time the error occurred (UTC)", example = "2026-10-10T13:58:30.013Z")
        Instant timestamp,
    @Schema(description = "HTTP status code", example = "404") int status,
    @Schema(description = "HTTP status reason phrase", example = "Not Found") String error,
    @Schema(
            description = "Human readable explanation of the error",
            example = "Student with id '3fa85f64-5717-4562-b3fc-2c963f66afa6' was not found")
        String message,
    @Schema(description = "Request path that caused the error", example = "/api/students/3fa85f64")
        String path,
    @Schema(
            description = "Trace id for correlating the error with logs and traces; absent if tracing is inactive",
            example = "4bf92f3577b34da6a3ce929d0e0e4736",
            nullable = true)
        String traceId) {

  public static ApiErrorResponse of(int status, String error, String message, String path) {
    return new ApiErrorResponse(Instant.now(), status, error, message, path, MDC.get("traceId"));
  }
}
