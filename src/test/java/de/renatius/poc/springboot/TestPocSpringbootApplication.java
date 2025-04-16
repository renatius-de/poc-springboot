package de.renatius.poc.springboot;

import org.springframework.boot.SpringApplication;

public class TestPocSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.from(PocSpringbootApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
