package de.renatius.poc.springboot.grpc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.grpc.testsupport.AbstractTestcontainersTest;
import de.renatius.poc.springboot.grpc.v1.CourseServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.CreateCourseRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteCourseRequest;
import de.renatius.poc.springboot.grpc.v1.GetCourseRequest;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesRequest;
import de.renatius.poc.springboot.grpc.v1.SearchPageRequest;
import de.renatius.poc.springboot.grpc.v1.UpdateCourseRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CourseGrpcServiceIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private CourseRepository courseRepository;
  @Autowired private ProfessorRepository professorRepository;

  private CourseServiceGrpc.CourseServiceBlockingStub client;

  @BeforeEach
  void setUpClient() {
    client = CourseServiceGrpc.newBlockingStub(channel);
  }

  @Test
  void shouldCreateAndGetCourse() {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Ada").lastName("Lovelace").build());

    var created =
        client.createCourse(
            CreateCourseRequest.newBuilder()
                .setName("Distributed Systems")
                .setRoom("A-101")
                .setProfessorId(professor.getId().toString())
                .build());

    assertThat(created.getId()).isNotBlank();
    assertThat(created.getProfessorId()).isEqualTo(professor.getId().toString());

    var loaded = client.getCourse(GetCourseRequest.newBuilder().setId(created.getId()).build());
    assertThat(loaded.getName()).isEqualTo("Distributed Systems");
  }

  @Test
  void shouldUpdateCourse() {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Grace").lastName("Hopper").build());
    Course course =
        courseRepository.save(
            Course.builder().name("Compilers").room("C-303").professor(professor).build());

    var updated =
        client.updateCourse(
            UpdateCourseRequest.newBuilder()
                .setId(course.getId().toString())
                .setName("Advanced Compilers")
                .setRoom("C-304")
                .setProfessorId(professor.getId().toString())
                .build());

    assertThat(updated.getName()).isEqualTo("Advanced Compilers");
    assertThat(updated.getRoom()).isEqualTo("C-304");
  }

  @Test
  void shouldDeleteCourse() {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Barbara").lastName("Liskov").build());
    Course course =
        courseRepository.save(
            Course.builder().name("Algorithms").room("B-202").professor(professor).build());

    client.deleteCourse(DeleteCourseRequest.newBuilder().setId(course.getId().toString()).build());

    assertThat(courseRepository.findById(course.getId())).isEmpty();
  }

  @Test
  void shouldSearchCoursesWithPaging() {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Donald").lastName("Knuth").build());
    courseRepository.save(Course.builder().name("Algorithms").room("B-202").professor(professor).build());
    courseRepository.save(
        Course.builder().name("Distributed Systems").room("A-101").professor(professor).build());

    var response =
        client.searchCourses(
            SearchCoursesRequest.newBuilder()
                .setName("alg")
                .setPage(SearchPageRequest.newBuilder().setPage(0).setSize(10).build())
                .build());

    assertThat(response.getCoursesCount()).isEqualTo(1);
    assertThat(response.getCourses(0).getName()).isEqualTo("Algorithms");
    assertThat(response.getPage()).isEqualTo(0);
  }

  @Test
  void shouldReturnInvalidArgumentForUnknownProfessorFormat() {
    assertThatThrownBy(
            () ->
                client.createCourse(
                    CreateCourseRequest.newBuilder()
                        .setName("Distributed Systems")
                        .setRoom("A-101")
                        .setProfessorId("invalid-uuid")
                        .build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.INVALID_ARGUMENT);
  }

  @Test
  void shouldReturnNotFoundForDeleteUnknownCourse() {
    assertThatThrownBy(
            () ->
                client.deleteCourse(
                    DeleteCourseRequest.newBuilder().setId(UUID.randomUUID().toString()).build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.NOT_FOUND);
  }
}
