package de.renatius.poc.springboot.grpc.config;

import de.renatius.poc.springboot.grpc.v1.AcademicProto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documents the gRPC services (served on the gRPC port) in OpenAPI form, derived from the protobuf
 * service descriptors. The documented paths follow the gRPC HTTP/2 convention
 * {@code /<package>.<Service>/<Method>}; they are for exploration only.
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI grpcOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Academic gRPC API")
                .version("1.0")
                .description(
                    "gRPC services for students, professors and courses, listed from the protobuf"
                        + " definitions. Call them with a gRPC client on the configured gRPC port."))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public OpenApiCustomizer grpcServicesCustomizer() {
    return openApi ->
        AcademicProto.getDescriptor()
            .getServices()
            .forEach(
                service -> {
                  openApi.addTagsItem(
                      new Tag().name(service.getName()).description("gRPC service " + service.getFullName()));
                  service
                      .getMethods()
                      .forEach(
                          method -> {
                            Operation operation =
                                new Operation()
                                    .addTagsItem(service.getName())
                                    .operationId(service.getName() + "_" + method.getName())
                                    .summary(method.getName())
                                    .description(
                                        "gRPC call %s(%s) returns %s"
                                            .formatted(
                                                method.getFullName(),
                                                method.getInputType().getName(),
                                                method.getOutputType().getName()))
                                    .responses(
                                        new ApiResponses()
                                            .addApiResponse("200", response("OK"))
                                            .addApiResponse("3", response("INVALID_ARGUMENT"))
                                            .addApiResponse("5", response("NOT_FOUND")));
                            openApi.path(
                                "/" + service.getFullName() + "/" + method.getName(),
                                new PathItem().post(operation));
                          });
                });
  }

  private static ApiResponse response(String description) {
    return new ApiResponse()
        .description(description)
        .content(new Content().addMediaType("application/grpc", new MediaType().schema(new ObjectSchema())));
  }
}
