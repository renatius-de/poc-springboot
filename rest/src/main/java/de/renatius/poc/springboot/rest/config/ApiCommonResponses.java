package de.renatius.poc.springboot.rest.config;

import de.renatius.poc.springboot.data.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Documents the standard 401, 403 and 500 error responses on a controller or operation. */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
  @ApiResponse(
      responseCode = "401",
      description = "Authentication is required or the credentials are invalid",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorResponse.class))),
  @ApiResponse(
      responseCode = "403",
      description = "The caller is authenticated but not allowed to perform this operation",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorResponse.class))),
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected server error",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorResponse.class)))
})
public @interface ApiCommonResponses {}
