package de.renatius.poc.springboot.rest.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.rest.testsupport.AbstractTestcontainersTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class ProfessorControllerIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProfessorRepository professorRepository;

  @Test
  void shouldCreateAndGetProfessor() throws Exception {
    ProfessorDto request = new ProfessorDto(null, "Dr.", "Ada", "Lovelace");

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/professors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(
                header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/professors/.+")))
            .andExpect(jsonPath("$.firstName").value("Ada"))
            .andExpect(jsonPath("$.lastName").value("Lovelace"))
            .andReturn();

    String location = createResult.getResponse().getHeader("Location");
    assertThat(location).isNotBlank();
    UUID id = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

    mockMvc
        .perform(get("/api/professors/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.title").value("Dr."))
        .andExpect(jsonPath("$.firstName").value("Ada"))
        .andExpect(jsonPath("$.lastName").value("Lovelace"));
  }

  @Test
  void shouldUpdateProfessor() throws Exception {
    Professor existing =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Alan").lastName("Turing").build());

    ProfessorDto update = new ProfessorDto(existing.getId(), "Prof.", "Alan Mathison", "Turing");

    mockMvc
        .perform(
            put("/api/professors/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existing.getId().toString()))
        .andExpect(jsonPath("$.title").value("Prof."))
        .andExpect(jsonPath("$.firstName").value("Alan Mathison"))
        .andExpect(jsonPath("$.lastName").value("Turing"));
  }

  @Test
  void shouldDeleteProfessor() throws Exception {
    Professor existing =
        professorRepository.save(
            Professor.builder().title("Prof.").firstName("Grace").lastName("Hopper").build());

    mockMvc.perform(delete("/api/professors/{id}", existing.getId())).andExpect(status().isNoContent());

    assertThat(professorRepository.findById(existing.getId())).isEmpty();
  }

  @Test
  void shouldSearchProfessorsWithPaging() throws Exception {
    professorRepository.save(Professor.builder().title("Dr.").firstName("Ada").lastName("Lovelace").build());
    professorRepository.save(Professor.builder().title("Dr.").firstName("Alan").lastName("Turing").build());

    mockMvc
        .perform(get("/api/professors/search").param("last_name", "tur").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].firstName").value("Alan"))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.number").value(0));
  }

  @Test
  void shouldReturnProblemDetailForBadRequestAndNotFound() throws Exception {
    ProfessorDto invalid = new ProfessorDto(null, "Dr.", "", "Lovelace");

    mockMvc
        .perform(
            post("/api/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").exists());

    mockMvc
        .perform(get("/api/professors/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.detail").exists());
  }
}
