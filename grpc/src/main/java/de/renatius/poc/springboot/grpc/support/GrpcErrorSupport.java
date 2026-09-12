package de.renatius.poc.springboot.grpc.support;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import java.util.function.Supplier;

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
    } catch (StatusRuntimeException exception) {
      observer.onError(exception);
    } catch (Exception exception) {
      observer.onError(
          Status.INTERNAL.withDescription("Unexpected server error").withCause(exception).asRuntimeException());
    }
  }
}
