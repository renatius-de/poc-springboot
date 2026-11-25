package de.renatius.poc.springboot.graphql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.renatius.poc.springboot")
public class GraphqlApplication {
  public static void main(String[] args) {
    log.info("Starting GraphqlApplication");
    SpringApplication.run(GraphqlApplication.class, args);
  }
}
