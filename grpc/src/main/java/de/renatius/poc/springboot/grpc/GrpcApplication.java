package de.renatius.poc.springboot.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.renatius.poc.springboot")
public class GrpcApplication {

  public static void main(String[] args) {
    log.info("Starting GrpcApplication");
    SpringApplication.run(GrpcApplication.class, args);
  }
}
