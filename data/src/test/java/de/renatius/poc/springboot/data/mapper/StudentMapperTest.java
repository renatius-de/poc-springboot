package de.renatius.poc.springboot.data.mapper;

import static org.assertj.core.api.Assertions.*;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StudentMapperTest {

  private final StudentMapper mapper = new StudentMapperImpl();

  @Test
  void toDtoMapsAllFields() {
    UUID id = UUID.randomUUID();
    Student entity = Student.builder().id(id).firstName("Ada").lastName("Lovelace").build();

    assertThat(mapper.toDto(entity)).isEqualTo(new StudentDto(id, "Ada", "Lovelace"));
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  void toEntityMapsAllFieldsAndIgnoresCourses() {
    UUID id = UUID.randomUUID();
    Student entity = mapper.toEntity(new StudentDto(id, "Alan", "Turing"));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getFirstName()).isEqualTo("Alan");
    assertThat(entity.getLastName()).isEqualTo("Turing");
    assertThat(entity.getCourses()).isEmpty();
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void inverseRoundTripIsStable() {
    UUID id = UUID.randomUUID();
    Student source = Student.builder().id(id).firstName("Grace").lastName("Hopper").build();

    StudentDto dto = mapper.toDto(source);
    Student roundTrip = mapper.toEntity(dto);

    assertThat(roundTrip.getId()).isEqualTo(id);
    assertThat(roundTrip.getFirstName()).isEqualTo("Grace");
    assertThat(roundTrip.getLastName()).isEqualTo("Hopper");
    assertThat(roundTrip.getCourses()).isEmpty();
  }

  @Test
  void toEntityHandlesNullValues() {
    Student entity = mapper.toEntity(new StudentDto(null, null, null));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNull();
    assertThat(entity.getFirstName()).isNull();
    assertThat(entity.getLastName()).isNull();
    assertThat(entity.getCourses()).isEmpty();
  }
}
