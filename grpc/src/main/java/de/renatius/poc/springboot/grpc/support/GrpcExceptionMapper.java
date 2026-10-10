package de.renatius.poc.springboot.grpc.support;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.slf4j.MDC;

/** Maps arbitrary exceptions to standard gRPC statuses with a correlation trace id trailer. */
public final class GrpcExceptionMapper {

  public static final Metadata.Key<String> TRACE_ID_KEY =
      Metadata.Key.of("trace-id", Metadata.ASCII_STRING_MARSHALLER);

  /** Trailer carrying an RFC 7807 problem detail JSON document (UTF-8) for the failed call. */
  public static final Metadata.Key<byte[]> PROBLEM_DETAIL_KEY =
      Metadata.Key.of("problem-detail-bin", Metadata.BINARY_BYTE_MARSHALLER);

  private static final String TYPE_BASE = "https://poc.renatius.de/problems/grpc/";

  private GrpcExceptionMapper() {}

  public static Status toStatus(Throwable throwable) {
    if (throwable instanceof StatusRuntimeException exception) {
      return exception.getStatus();
    }
    if (throwable instanceof UnauthenticatedException) {
      return Status.UNAUTHENTICATED.withDescription(throwable.getMessage());
    }
    if (throwable instanceof PermissionDeniedException || throwable instanceof SecurityException) {
      return Status.PERMISSION_DENIED.withDescription(throwable.getMessage());
    }
    if (throwable instanceof IllegalArgumentException) {
      return Status.INVALID_ARGUMENT.withDescription(throwable.getMessage());
    }
    return Status.INTERNAL.withDescription("Unexpected server error").withCause(throwable);
  }

  public static Metadata toTrailers(Throwable throwable) {
    Metadata trailers =
        throwable instanceof StatusRuntimeException exception && exception.getTrailers() != null
            ? exception.getTrailers()
            : new Metadata();
    String traceId = MDC.get("traceId");
    if (traceId != null && !trailers.containsKey(TRACE_ID_KEY)) {
      trailers.put(TRACE_ID_KEY, traceId);
    }
    if (!trailers.containsKey(PROBLEM_DETAIL_KEY)) {
      trailers.put(
          PROBLEM_DETAIL_KEY,
          toProblemDetailJson(toStatus(throwable), traceId).getBytes(StandardCharsets.UTF_8));
    }
    return trailers;
  }

  /** Builds an RFC 7807 problem detail document equivalent to the given gRPC status. */
  public static String toProblemDetailJson(Status status, String traceId) {
    Status.Code code = status.getCode();
    StringBuilder json = new StringBuilder("{");
    field(json, "type", TYPE_BASE + code.name().toLowerCase().replace('_', '-')).append(',');
    field(json, "title", code.name()).append(',');
    json.append("\"status\":").append(httpStatus(code)).append(',');
    field(json, "detail", status.getDescription() == null ? code.name() : status.getDescription())
        .append(',');
    field(json, "grpcStatus", code.name()).append(',');
    field(json, "timestamp", Instant.now().toString());
    if (traceId != null) {
      json.append(',');
      field(json, "traceId", traceId);
    }
    return json.append('}').toString();
  }

  public static int httpStatus(Status.Code code) {
    return switch (code) {
      case OK -> 200;
      case INVALID_ARGUMENT, FAILED_PRECONDITION, OUT_OF_RANGE -> 400;
      case UNAUTHENTICATED -> 401;
      case PERMISSION_DENIED -> 403;
      case NOT_FOUND -> 404;
      case ALREADY_EXISTS, ABORTED -> 409;
      case RESOURCE_EXHAUSTED -> 429;
      case CANCELLED -> 499;
      case UNIMPLEMENTED -> 501;
      case UNAVAILABLE -> 503;
      case DEADLINE_EXCEEDED -> 504;
      default -> 500;
    };
  }

  private static StringBuilder field(StringBuilder json, String name, String value) {
    json.append('"').append(name).append("\":\"");
    for (char c : value.toCharArray()) {
      switch (c) {
        case '"' -> json.append("\\\"");
        case '\\' -> json.append("\\\\");
        case '\n' -> json.append("\\n");
        case '\r' -> json.append("\\r");
        case '\t' -> json.append("\\t");
        default -> {
          if (c < 0x20) {
            json.append(String.format("\\u%04x", (int) c));
          } else {
            json.append(c);
          }
        }
      }
    }
    return json.append('"');
  }

  public static StatusRuntimeException toException(Throwable throwable) {
    return toStatus(throwable).asRuntimeException(toTrailers(throwable));
  }
}
