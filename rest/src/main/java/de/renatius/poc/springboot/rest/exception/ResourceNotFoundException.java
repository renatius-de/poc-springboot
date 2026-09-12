package de.renatius.poc.springboot.rest.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String resourceName, UUID id) {
    super("%s with id '%s' was not found".formatted(resourceName, id));
  }
}
