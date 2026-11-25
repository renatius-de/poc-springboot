package de.renatius.poc.springboot.security;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.grpc.server.GlobalServerInterceptor;

@GlobalServerInterceptor
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiKeyGrpcInterceptor implements ServerInterceptor {
  private static final Metadata.Key<String> API_KEY =
      Metadata.Key.of("X-API-Key", Metadata.ASCII_STRING_MARSHALLER);

  private final ApiKeyProperties properties;

  public ApiKeyGrpcInterceptor(ApiKeyProperties properties) {
    this.properties = properties;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      Metadata headers,
      ServerCallHandler<ReqT, RespT> next) {
    String configuredApiKey = properties.apiKey();
    String requestApiKey = headers.get(API_KEY);
    if (configuredApiKey == null
        || configuredApiKey.isBlank()
        || !configuredApiKey.equals(requestApiKey)) {
      call.close(Status.UNAUTHENTICATED.withDescription("A valid X-API-Key is required"), new Metadata());
      return new ServerCall.Listener<>() {};
    }
    return next.startCall(call, headers);
  }
}
