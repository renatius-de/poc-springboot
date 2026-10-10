package de.renatius.poc.springboot.grpc.support;

public class UnauthenticatedException extends RuntimeException {

  public UnauthenticatedException(String message) {
    super(message);
  }
}
