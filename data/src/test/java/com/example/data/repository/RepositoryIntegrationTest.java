package com.example.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.data.TestDataApplication;
import com.example.data.dto.CourseDto;
import com.example.data.dto.ProfessorDto;
import com.example.data.dto.StudentDto;
import com.example.data.entity.Course;
import com.example.data.entity.Professor;
import com.example.data.entity.Student;
import com.example.data.mapper.CourseMapper;
import com.example.data.mapper.ProfessorMapper;
import com.example.data.mapper.StudentMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = TestDataApplication.class)
class RepositoryIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:17-alpine");

  @Autowired private ProfessorRepository professorRepository;
  @Autowired private CourseRepository courseRepository;
  @Autowired private StudentRepository studentRepository;

  @Autowired private ProfessorMapper professorMapper;
  @Autowired private CourseMapper courseMapper;
  @Autowired private StudentMapper studentMapper;

  @Test
  void shouldPersistAndReadEntitiesWithRelationshipsAndUuidIds() {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Ada").lastName("Lovelace").build());

    Course course = Course.builder().name("Distributed Systems").room("A-101").build();
    course.setProfessor(professor);
    course = courseRepository.save(course);

    Student student = Student.builder().firstName("Alan").lastName("Turing").build();
    student.getCourses().add(course);
    student = studentRepository.save(student);

    assertThat(professor.getId()).isNotNull();
    assertThat(course.getId()).isNotNull();
    assertThat(student.getId()).isNotNull();

    List<Course> allCourses = courseRepository.findAll();
    assertThat(allCourses).hasSize(1);
    assertThat(allCourses.getFirst().getProfessor().getId()).isEqualTo(professor.getId());

    List<Student> allStudents = studentRepository.findAll();
    assertThat(allStudents).hasSize(1);
    assertThat(allStudents.getFirst().getCourses()).extracting(Course::getId).containsExactly(course.getId());

    ProfessorDto professorDto = professorMapper.toDto(professor);
    CourseDto courseDto = courseMapper.toDto(course);
    StudentDto studentDto = studentMapper.toDto(student);

    assertThat(professorDto.id()).isEqualTo(professor.getId());
    assertThat(courseDto.professorId()).isEqualTo(professor.getId());
    assertThat(studentDto.id()).isEqualTo(student.getId());

    Course mappedCourse = courseMapper.toEntity(courseDto);
    Professor mappedProfessor = professorMapper.toEntity(professorDto);
    Student mappedStudent = studentMapper.toEntity(studentDto);

    assertThat(mappedCourse.getProfessor()).isNotNull();
    assertThat(mappedCourse.getProfessor().getId()).isEqualTo(courseDto.professorId());
    assertThat(mappedProfessor.getId()).isEqualTo(professorDto.id());
    assertThat(mappedProfessor.getFirstName()).isEqualTo(professorDto.firstName());
    assertThat(mappedStudent.getId()).isEqualTo(studentDto.id());
    assertThat(mappedStudent.getLastName()).isEqualTo(studentDto.lastName());
  }
}
