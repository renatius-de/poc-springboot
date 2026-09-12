package de.renatius.poc.springboot.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "de.renatius.poc.springboot")
@EntityScan(basePackages = "de.renatius.poc.springboot.data.entity")
@EnableJpaRepositories(basePackages = "de.renatius.poc.springboot.data.repository")
public class RestApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestApplication.class, args);
  }
}
