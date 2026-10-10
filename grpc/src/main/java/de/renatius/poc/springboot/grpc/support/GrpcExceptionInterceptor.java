package de.renatius.poc.springboot.grpc.support;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

/**
 * Global interceptor that turns exceptions escaping a service method into standard gRPC statuses
 * (UNAUTHENTICATED, PERMISSION_DENIED, INVALID_ARGUMENT, INTERNAL, ...).
 */
@Slf4j
@Component
@GlobalServerInterceptor
public class GrpcExceptionInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    ServerCall.Listener<ReqT> delegate;
    try {
      delegate = next.startCall(call, headers);
    } catch (RuntimeException exception) {
      close(call, exception);
      return new ServerCall.Listener<>() {};
    }
    return new SimpleForwardingServerCallListener<>(delegate) {
      @Override
      public void onMessage(ReqT message) {
        guard(() -> super.onMessage(message));
      }

      @Override
      public void onHalfClose() {
        guard(super::onHalfClose);
      }

      private void guard(Runnable action) {
        try {
          action.run();
        } catch (RuntimeException exception) {
          close(call, exception);
        }
      }
    };
  }

  private void close(ServerCall<?, ?> call, RuntimeException exception) {
    Status status = GrpcExceptionMapper.toStatus(exception);
    if (status.getCode() == Status.Code.INTERNAL) {
      log.error("Unexpected error while handling gRPC call", exception);
    } else {
      log.warn("gRPC call failed with status {}: {}", status.getCode(), status.getDescription());
    }
    try {
      call.close(status.withCause(null), GrpcExceptionMapper.toTrailers(exception));
    } catch (IllegalStateException alreadyClosed) {
      log.debug("gRPC call already closed", alreadyClosed);
    }
  }
}
