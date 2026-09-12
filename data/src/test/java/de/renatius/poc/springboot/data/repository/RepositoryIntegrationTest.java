package de.renatius.poc.springboot.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.TestDataApplication;
import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.mapper.CourseMapper;
import de.renatius.poc.springboot.data.mapper.ProfessorMapper;
import de.renatius.poc.springboot.data.mapper.StudentMapper;
import de.renatius.poc.springboot.data.testsupport.AbstractTestcontainersTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestDataApplication.class)
@Transactional
class RepositoryIntegrationTest extends AbstractTestcontainersTest {

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
    professor.addCourse(course);
    course = courseRepository.save(course);

    Student student = Student.builder().firstName("Alan").lastName("Turing").build();
    student.addCourse(course);
    student = studentRepository.save(student);

    assertThat(professor.getId()).isNotNull();
    assertThat(course.getId()).isNotNull();
    assertThat(student.getId()).isNotNull();
    assertThat(professor.getCourses()).contains(course);
    assertThat(course.getStudents()).contains(student);

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
    Course mappedCourseWithoutProfessor = courseMapper.toEntity(new CourseDto(null, "Algorithms", "B-202", null));

    assertThat(mappedCourse.getProfessor()).isNotNull();
    assertThat(mappedCourse.getProfessor().getId()).isEqualTo(courseDto.professorId());
    assertThat(mappedProfessor.getId()).isEqualTo(professorDto.id());
    assertThat(mappedProfessor.getFirstName()).isEqualTo(professorDto.firstName());
    assertThat(mappedStudent.getId()).isEqualTo(studentDto.id());
    assertThat(mappedStudent.getLastName()).isEqualTo(studentDto.lastName());
    assertThat(mappedCourseWithoutProfessor.getProfessor()).isNull();
  }

  @Test
  void shouldKeepProfessorCourseAssociationConsistentWhenReassigningAndClearing() {
    Professor firstProfessor =
        Professor.builder().title("Prof.").firstName("Grace").lastName("Hopper").build();
    Professor secondProfessor =
        Professor.builder().title("Dr.").firstName("Barbara").lastName("Liskov").build();
    Course course = Course.builder().name("Compilers").room("C-303").build();

    firstProfessor.addCourse(course);
    assertThat(course.getProfessor()).isEqualTo(firstProfessor);
    assertThat(firstProfessor.getCourses()).contains(course);

    secondProfessor.addCourse(course);
    assertThat(course.getProfessor()).isEqualTo(secondProfessor);
    assertThat(firstProfessor.getCourses()).doesNotContain(course);
    assertThat(secondProfessor.getCourses()).contains(course);

    secondProfessor.removeCourse(course);
    assertThat(course.getProfessor()).isNull();
    assertThat(secondProfessor.getCourses()).doesNotContain(course);
  }
}
