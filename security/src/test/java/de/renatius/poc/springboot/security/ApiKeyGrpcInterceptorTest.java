package de.renatius.poc.springboot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiKeyGrpcInterceptorTest {

  private static final Metadata.Key<String> API_KEY =
      Metadata.Key.of("X-API-Key", Metadata.ASCII_STRING_MARSHALLER);

  @Mock private ServerCall<String, String> call;
  @Mock private ServerCallHandler<String, String> next;

  @Test
  void shouldContinueWhenApiKeyMatches() {
    var headers = new Metadata();
    headers.put(API_KEY, "expected-key");
    var listener = new ServerCall.Listener<String>() {};
    when(next.startCall(call, headers)).thenReturn(listener);
    var interceptor = new ApiKeyGrpcInterceptor(new ApiKeyProperties("expected-key", List.of()));

    ServerCall.Listener<String> result = interceptor.interceptCall(call, headers, next);

    assertThat(result).isSameAs(listener);
    verify(next).startCall(call, headers);
    verifyNoInteractions(call);
  }

  @Test
  void shouldCloseCallAsUnauthenticatedWhenApiKeyIsMissing() {
    var headers = new Metadata();
    var interceptor = new ApiKeyGrpcInterceptor(new ApiKeyProperties("expected-key", List.of()));

    ServerCall.Listener<String> result = interceptor.interceptCall(call, headers, next);

    assertThat(result).isNotNull();
    ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
    verify(call).close(statusCaptor.capture(), any(Metadata.class));
    assertThat(statusCaptor.getValue().getCode()).isEqualTo(Status.Code.UNAUTHENTICATED);
    assertThat(statusCaptor.getValue().getDescription())
        .isEqualTo("A valid X-API-Key is required");
    verifyNoInteractions(next);
  }
}
