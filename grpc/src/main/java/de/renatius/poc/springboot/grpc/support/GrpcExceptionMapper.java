package de.renatius.poc.springboot.grpc.support;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.MDC;

/** Maps arbitrary exceptions to standard gRPC statuses with a correlation trace id trailer. */
public final class GrpcExceptionMapper {

  public static final Metadata.Key<String> TRACE_ID_KEY =
      Metadata.Key.of("trace-id", Metadata.ASCII_STRING_MARSHALLER);

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
    return trailers;
  }

  public static StatusRuntimeException toException(Throwable throwable) {
    return toStatus(throwable).asRuntimeException(toTrailers(throwable));
  }
}
