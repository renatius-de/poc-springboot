package de.renatius.poc.springboot.graphql;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

import de.renatius.poc.springboot.data.mapper.CourseMapper;
import de.renatius.poc.springboot.data.mapper.ProfessorMapper;
import de.renatius.poc.springboot.data.mapper.StudentMapper;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class AcademicControllerTest {

  @Mock private StudentRepository studentRepository;
  @Mock private ProfessorRepository professorRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private StudentMapper studentMapper;
  @Mock private ProfessorMapper professorMapper;
  @Mock private CourseMapper courseMapper;

  private AcademicController controller;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUpController() {
    controller =
        new AcademicController(
            studentRepository,
            professorRepository,
            courseRepository,
            studentMapper,
            professorMapper,
            courseMapper);
  }

  @Test
  void shouldRejectBlankStudentNameBeforeSaving() {
    assertThatThrownBy(() -> controller.createStudent(new AcademicController.StudentInput("", "Lovelace")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be blank");

    verifyNoInteractions(studentRepository, studentMapper);
  }

  @Test
  void shouldRejectMalformedStudentIdBeforeLookingUpStudent() {
    assertThatThrownBy(() -> controller.student("not-a-uuid"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("id must be a valid UUID");

    verifyNoInteractions(studentRepository, studentMapper);
  }
}
