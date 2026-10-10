package de.renatius.poc.springboot.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.renatius.poc.springboot")
@EntityScan(basePackages = "de.renatius.poc.springboot.data.entity")
@EnableJpaRepositories(basePackages = "de.renatius.poc.springboot.data.repository")
public class GrpcApplication {

  public static void main(String[] args) {
    log.info("Starting GrpcApplication");
    SpringApplication.run(GrpcApplication.class, args);
  }
}
