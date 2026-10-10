package de.renatius.poc.springboot.rest.student;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StudentRestMapperTest {

  private final StudentRestMapper mapper = new StudentRestMapperImpl();

  @Test
  void toDto() {
    UUID id = UUID.randomUUID();
    Student entity = Student.builder().id(id).firstName("Ada").lastName("L").build();
    assertThat(mapper.toDto(entity)).isEqualTo(new StudentDto(id, "Ada", "L"));
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  void toEntityForCreateIgnoresId() {
    Student entity = mapper.toEntityForCreate(new StudentDto(UUID.randomUUID(), "Ada", "L"));
    assertThat(entity.getId()).isNull();
    assertThat(entity.getFirstName()).isEqualTo("Ada");
    assertThat(entity.getLastName()).isEqualTo("L");
    assertThat(entity.getCourses()).isEmpty();
    assertThat(mapper.toEntityForCreate(null)).isNull();
  }

  @Test
  void toEntityForUpdateUsesPathId() {
    UUID id = UUID.randomUUID();
    Student entity = mapper.toEntityForUpdate(id, new StudentDto(UUID.randomUUID(), "Ada", "L"));
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getFirstName()).isEqualTo("Ada");
    assertThat(entity.getLastName()).isEqualTo("L");
    assertThat(mapper.toEntityForUpdate(null, null)).isNull();
  }
}
