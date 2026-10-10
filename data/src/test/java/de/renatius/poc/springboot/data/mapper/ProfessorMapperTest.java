package de.renatius.poc.springboot.data.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfessorMapperTest {

  private final ProfessorMapper mapper = new ProfessorMapperImpl();

  @Test
  void toDtoMapsAllFields() {
    UUID id = UUID.randomUUID();
    Professor entity =
        Professor.builder().id(id).title("Dr.").firstName("Alan").lastName("Turing").build();

    assertThat(mapper.toDto(entity)).isEqualTo(new ProfessorDto(id, "Dr.", "Alan", "Turing"));
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  void toEntityMapsAllFieldsAndIgnoresCourses() {
    UUID id = UUID.randomUUID();
    Professor entity = mapper.toEntity(new ProfessorDto(id, "Prof.", "Ada", "Lovelace"));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTitle()).isEqualTo("Prof.");
    assertThat(entity.getFirstName()).isEqualTo("Ada");
    assertThat(entity.getLastName()).isEqualTo("Lovelace");
    assertThat(entity.getCourses()).isEmpty();
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  void inverseRoundTripIsStable() {
    UUID id = UUID.randomUUID();
    Professor source =
        Professor.builder().id(id).title("Dr.").firstName("Margaret").lastName("Hamilton").build();

    ProfessorDto dto = mapper.toDto(source);
    Professor roundTrip = mapper.toEntity(dto);

    assertThat(roundTrip.getId()).isEqualTo(id);
    assertThat(roundTrip.getTitle()).isEqualTo("Dr.");
    assertThat(roundTrip.getFirstName()).isEqualTo("Margaret");
    assertThat(roundTrip.getLastName()).isEqualTo("Hamilton");
    assertThat(roundTrip.getCourses()).isEmpty();
  }

  @Test
  void toEntityHandlesNullValues() {
    Professor entity = mapper.toEntity(new ProfessorDto(null, null, null, null));

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNull();
    assertThat(entity.getTitle()).isNull();
    assertThat(entity.getFirstName()).isNull();
    assertThat(entity.getLastName()).isNull();
    assertThat(entity.getCourses()).isEmpty();
  }
}
