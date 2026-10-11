package de.renatius.poc.springboot.data.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class EntityEqualityTest {

  @Test
  void keepsDistinctTransientEntitiesInHashBasedRelationships() {
    Student student = new Student();
    Course firstCourse = new Course();
    Course secondCourse = new Course();
    Course course = new Course();
    Student firstStudent = new Student();
    Student secondStudent = new Student();

    student.addCourse(firstCourse);
    student.addCourse(secondCourse);
    course.addStudent(firstStudent);
    course.addStudent(secondStudent);

    assertThat(firstCourse).isNotEqualTo(secondCourse);
    assertThat(student.getCourses()).containsExactlyInAnyOrder(firstCourse, secondCourse);
    assertThat(course.getStudents()).containsExactlyInAnyOrder(firstStudent, secondStudent);

    firstCourse.setId(UUID.randomUUID());
    firstStudent.setId(UUID.randomUUID());

    assertThat(student.getCourses()).contains(firstCourse);
    assertThat(course.getStudents()).contains(firstStudent);
  }

  @Test
  void comparesPersistedEntitiesByIdentifier() {
    UUID id = UUID.randomUUID();

    assertThat(Student.builder().id(id).build()).isEqualTo(Student.builder().id(id).build());
    assertThat(Course.builder().id(id).build()).isEqualTo(Course.builder().id(id).build());
    assertThat(Professor.builder().id(id).build()).isEqualTo(Professor.builder().id(id).build());
  }
}
