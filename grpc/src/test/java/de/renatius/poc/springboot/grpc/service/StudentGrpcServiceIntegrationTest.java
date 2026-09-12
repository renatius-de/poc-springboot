package de.renatius.poc.springboot.grpc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import de.renatius.poc.springboot.grpc.testsupport.AbstractTestcontainersTest;
import de.renatius.poc.springboot.grpc.v1.CreateStudentRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteStudentRequest;
import de.renatius.poc.springboot.grpc.v1.GetStudentRequest;
import de.renatius.poc.springboot.grpc.v1.SearchPageRequest;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsRequest;
import de.renatius.poc.springboot.grpc.v1.StudentServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.UpdateStudentRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StudentGrpcServiceIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private StudentRepository studentRepository;

  private StudentServiceGrpc.StudentServiceBlockingStub client;

  @BeforeEach
  void setUpClient() {
    client = StudentServiceGrpc.newBlockingStub(channel);
  }

  @Test
  void shouldCreateAndGetStudent() {
    var created =
        client.createStudent(
            CreateStudentRequest.newBuilder().setFirstName("Ada").setLastName("Lovelace").build());

    assertThat(created.getId()).isNotBlank();
    assertThat(created.getFirstName()).isEqualTo("Ada");
    assertThat(created.getLastName()).isEqualTo("Lovelace");

    var loaded = client.getStudent(GetStudentRequest.newBuilder().setId(created.getId()).build());

    assertThat(loaded.getId()).isEqualTo(created.getId());
    assertThat(loaded.getFirstName()).isEqualTo("Ada");
  }

  @Test
  void shouldUpdateStudent() {
    Student existing = studentRepository.save(Student.builder().firstName("Alan").lastName("Turing").build());

    var updated =
        client.updateStudent(
            UpdateStudentRequest.newBuilder()
                .setId(existing.getId().toString())
                .setFirstName("Alan Mathison")
                .setLastName("Turing")
                .build());

    assertThat(updated.getId()).isEqualTo(existing.getId().toString());
    assertThat(updated.getFirstName()).isEqualTo("Alan Mathison");
  }

  @Test
  void shouldDeleteStudent() {
    Student existing = studentRepository.save(Student.builder().firstName("Grace").lastName("Hopper").build());

    client.deleteStudent(DeleteStudentRequest.newBuilder().setId(existing.getId().toString()).build());

    assertThat(studentRepository.findById(existing.getId())).isEmpty();
  }

  @Test
  void shouldSearchStudentsWithPaging() {
    studentRepository.save(Student.builder().firstName("Ada").lastName("Lovelace").build());
    studentRepository.save(Student.builder().firstName("Alan").lastName("Turing").build());

    var response =
        client.searchStudents(
            SearchStudentsRequest.newBuilder()
                .setFirstName("al")
                .setPage(SearchPageRequest.newBuilder().setPage(0).setSize(10).build())
                .build());

    assertThat(response.getStudentsCount()).isEqualTo(1);
    assertThat(response.getStudents(0).getFirstName()).isEqualTo("Alan");
    assertThat(response.getPage()).isEqualTo(0);
    assertThat(response.getSize()).isEqualTo(10);
  }

  @Test
  void shouldReturnInvalidArgumentForBlankFirstName() {
    assertThatThrownBy(
            () ->
                client.createStudent(
                    CreateStudentRequest.newBuilder().setFirstName("").setLastName("Lovelace").build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.INVALID_ARGUMENT);
  }

  @Test
  void shouldReturnNotFoundForDeleteUnknownStudent() {
    assertThatThrownBy(
            () ->
                client.deleteStudent(
                    DeleteStudentRequest.newBuilder().setId(UUID.randomUUID().toString()).build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.NOT_FOUND);
  }
}
