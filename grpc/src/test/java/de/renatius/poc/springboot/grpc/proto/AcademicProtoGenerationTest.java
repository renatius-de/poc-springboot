package de.renatius.poc.springboot.grpc.proto;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.grpc.v1.Student;
import org.junit.jupiter.api.Test;

class AcademicProtoGenerationTest {

  @Test
  void shouldBuildGeneratedStudentMessage() {
    Student student =
        Student.newBuilder()
            .setId("7f6f90e0-a66c-4af3-a987-f67e6a948f59")
            .setFirstName("Ada")
            .setLastName("Lovelace")
            .build();

    assertThat(student.getFirstName()).isEqualTo("Ada");
    assertThat(student.getLastName()).isEqualTo("Lovelace");
  }
}
