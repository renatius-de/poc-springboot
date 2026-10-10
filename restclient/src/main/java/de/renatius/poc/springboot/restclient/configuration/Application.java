package de.renatius.poc.springboot.restclient.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    log.info("Starting Application");
    SpringApplication.run(Application.class, args);
  }
}
