package de.renatius.poc.springboot;

import de.renatius.poc.springboot.configuration.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
