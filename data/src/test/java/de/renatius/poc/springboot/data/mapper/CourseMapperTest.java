package de.renatius.poc.springboot.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CourseMapperTest {

  private final CourseMapper mapper = new CourseMapperImpl();

  @Test
  void toDtoMapsAllFields() {
    UUID id = UUID.randomUUID();
    UUID professorId = UUID.randomUUID();
    Course entity =
        Course.builder().id(id).name("Algorithms").room("B-204").professor(Professor.builder().id(professorId).build()).build();

    assertThat(mapper.toDto(entity)).isEqualTo(new CourseDto(id, "Algorithms", "B-204", professorId));
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  void toDtoHandlesMissingProfessor() {
    Course entity = Course.builder().id(UUID.randomUUID()).name("Math").room("A1").build();

    assertThat(mapper.toDto(entity).professorId()).isNull();
  }

  @Test
  void toEntityMapsAllFieldsAndIgnoresStudents() {
    UUID id = UUID.randomUUID();
    UUID professorId = UUID.randomUUID();
    Course entity = mapper.toEntity(new CourseDto(id, "History", "C-3", professorId));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getName()).isEqualTo("History");
    assertThat(entity.getRoom()).isEqualTo("C-3");
    assertThat(entity.getProfessor()).isNotNull();
    assertThat(entity.getProfessor().getId()).isEqualTo(professorId);
    assertThat(entity.getStudents()).isEmpty();
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void toEntityHandlesNullValues() {
    Course entity = mapper.toEntity(new CourseDto(null, null, null, null));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNull();
    assertThat(entity.getName()).isNull();
    assertThat(entity.getRoom()).isNull();
    assertThat(entity.getProfessor()).isNull();
    assertThat(entity.getStudents()).isEmpty();
  }

  @Test
  void mapProfessorHandlesNullAndNonNull() {
    assertThat(mapper.mapProfessor(null)).isNull();
    UUID professorId = UUID.randomUUID();
    Professor professor = mapper.mapProfessor(professorId);
    assertThat(professor).isNotNull();
    assertThat(professor.getId()).isEqualTo(professorId);
  }
}
