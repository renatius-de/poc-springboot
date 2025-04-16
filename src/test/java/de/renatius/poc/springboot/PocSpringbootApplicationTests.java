package de.renatius.poc.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PocSpringbootApplicationTests {

    @Test
    void contextLoads() {
        // no assertions needed here
    }
}
