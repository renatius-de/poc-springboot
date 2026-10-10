package de.renatius.poc.springboot.rest.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }
}
