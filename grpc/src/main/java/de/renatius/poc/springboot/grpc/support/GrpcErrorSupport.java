package de.renatius.poc.springboot.grpc.support;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class GrpcErrorSupport {

  private GrpcErrorSupport() {}

  public static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw invalidArgument(field + " must not be blank");
    }
    return value;
  }

  public static UUID parseUuid(String value, String field) {
    String source = requireText(value, field);
    try {
      return UUID.fromString(source);
    } catch (IllegalArgumentException exception) {
      throw invalidArgument(field + " must be a valid UUID");
    }
  }

  public static StatusRuntimeException invalidArgument(String detail) {
    return Status.INVALID_ARGUMENT.withDescription(detail).asRuntimeException();
  }

  public static StatusRuntimeException notFound(String resource, UUID id) {
    return Status.NOT_FOUND
        .withDescription("%s '%s' not found".formatted(resource, id))
        .asRuntimeException();
  }

  public static <T> void execute(StreamObserver<T> observer, Supplier<T> supplier) {
    try {
      T response = supplier.get();
      observer.onNext(response);
      observer.onCompleted();
    } catch (Exception exception) {
      Status status = GrpcExceptionMapper.toStatus(exception);
      if (status.getCode() == Status.Code.INTERNAL) {
        log.error("Unexpected error while handling gRPC call", exception);
      } else {
        log.warn("gRPC call failed with status {}: {}", status.getCode(), status.getDescription());
      }
      observer.onError(GrpcExceptionMapper.toException(exception));
    }
  }
}
