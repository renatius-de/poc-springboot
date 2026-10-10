package de.renatius.poc.springboot.rest.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfessorRestMapperTest {

  private final ProfessorRestMapper mapper = new ProfessorRestMapperImpl();

  @Test
  void toDto() {
    UUID id = UUID.randomUUID();
    Professor entity =
        Professor.builder().id(id).title("Dr.").firstName("Alan").lastName("T").build();
    assertThat(mapper.toDto(entity)).isEqualTo(new ProfessorDto(id, "Dr.", "Alan", "T"));
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toDto(null));
  }

  @Test
  void toEntityForCreateIgnoresId() {
    Professor entity =
        mapper.toEntityForCreate(new ProfessorDto(UUID.randomUUID(), null, "Alan", "T"));
    assertThat(entity.getId()).isNull();
    assertThat(entity.getTitle()).isNull();
    assertThat(entity.getFirstName()).isEqualTo("Alan");
    assertThat(entity.getLastName()).isEqualTo("T");
    assertThat(entity.getCourses()).isEmpty();
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toEntityForCreate(null));
  }

  @Test
  void toEntityForUpdateUsesPathId() {
    UUID id = UUID.randomUUID();
    Professor entity =
        mapper.toEntityForUpdate(id, new ProfessorDto(UUID.randomUUID(), "Dr.", "Alan", "T"));
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTitle()).isEqualTo("Dr.");
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toEntityForUpdate(null, null));
  }
}
