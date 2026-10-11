package de.renatius.poc.springboot.rest.course;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CourseRestMapperTest {

  private final CourseRestMapper mapper = new CourseRestMapperImpl();

  @Test
  void toDtoWithAndWithoutProfessor() {
    UUID id = UUID.randomUUID();
    UUID professorId = UUID.randomUUID();
    Professor professor = Professor.builder().id(professorId).build();
    Course course = Course.builder().id(id).name("Math").room("A1").professor(professor).build();
    assertThat(mapper.toDto(course)).isEqualTo(new CourseDto(id, "Math", "A1", professorId));

    Course orphan = Course.builder().id(id).name("Math").build();
    assertThat(mapper.toDto(orphan).professorId()).isNull();
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  void toEntityForCreateIgnoresIdAndMapsProfessorReference() {
    UUID professorId = UUID.randomUUID();
    Course course =
        mapper.toEntityForCreate(new CourseDto(UUID.randomUUID(), "Math", "A1", professorId));
    assertThat(course.getId()).isNull();
    assertThat(course.getName()).isEqualTo("Math");
    assertThat(course.getRoom()).isEqualTo("A1");
    assertThat(course.getProfessor().getId()).isEqualTo(professorId);
    assertThat(course.getStudents()).isEmpty();

    assertThat(mapper.toEntityForCreate(new CourseDto(null, "Math", null, null)).getProfessor())
        .isNull();
    assertThat(mapper.toEntityForCreate(null)).isNull();
  }

  @Test
  void toEntityForUpdateUsesPathId() {
    UUID id = UUID.randomUUID();
    UUID professorId = UUID.randomUUID();
    Course course =
        mapper.toEntityForUpdate(id, new CourseDto(UUID.randomUUID(), "Math", "A1", professorId));
    assertThat(course.getId()).isEqualTo(id);
    assertThat(course.getProfessor().getId()).isEqualTo(professorId);
    assertThat(mapper.toEntityForUpdate(null, null)).isNull();
  }
}
