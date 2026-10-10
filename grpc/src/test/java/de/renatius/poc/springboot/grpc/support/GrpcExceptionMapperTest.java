package de.renatius.poc.springboot.grpc.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.grpc.Status;
import org.junit.jupiter.api.Test;

class GrpcExceptionMapperTest {

  @Test
  void shouldMapExceptionsToStandardStatusCodes() {
    assertThat(GrpcExceptionMapper.toStatus(new UnauthenticatedException("x")).getCode())
        .isEqualTo(Status.Code.UNAUTHENTICATED);
    assertThat(GrpcExceptionMapper.toStatus(new PermissionDeniedException("x")).getCode())
        .isEqualTo(Status.Code.PERMISSION_DENIED);
    assertThat(GrpcExceptionMapper.toStatus(new SecurityException("x")).getCode())
        .isEqualTo(Status.Code.PERMISSION_DENIED);
    assertThat(GrpcExceptionMapper.toStatus(new IllegalArgumentException("x")).getCode())
        .isEqualTo(Status.Code.INVALID_ARGUMENT);
    Status internal = GrpcExceptionMapper.toStatus(new IllegalStateException("secret"));
    assertThat(internal.getCode()).isEqualTo(Status.Code.INTERNAL);
    assertThat(internal.getDescription()).isEqualTo("Unexpected server error");
  }
}
