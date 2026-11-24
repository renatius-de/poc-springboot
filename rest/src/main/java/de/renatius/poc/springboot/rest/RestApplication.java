package de.renatius.poc.springboot.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.renatius.poc.springboot")
public class RestApplication {

  public static void main(String[] args) {
    log.info("Starting RestApplication");
    SpringApplication.run(RestApplication.class, args);
  }
}
