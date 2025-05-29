package de.renatius.poc.springboot.restclient.configuration;

import org.springframework.boot.SpringApplication;

public class TestMainApplication {

  public static void main(String[] args) {
    SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
  }
}
