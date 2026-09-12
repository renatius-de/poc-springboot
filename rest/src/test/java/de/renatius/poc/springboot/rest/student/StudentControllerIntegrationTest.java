package de.renatius.poc.springboot.rest.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import de.renatius.poc.springboot.rest.testsupport.AbstractTestcontainersTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class StudentControllerIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
  @Autowired private StudentRepository studentRepository;

  @Test
  void shouldCreateAndGetStudent() throws Exception {
    StudentDto request = new StudentDto(null, "Ada", "Lovelace");

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/students/.+")))
            .andExpect(jsonPath("$.firstName").value("Ada"))
            .andExpect(jsonPath("$.lastName").value("Lovelace"))
            .andReturn();

    String location = createResult.getResponse().getHeader("Location");
    assertThat(location).isNotBlank();
    UUID id = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

    mockMvc
        .perform(get("/api/students/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.firstName").value("Ada"))
        .andExpect(jsonPath("$.lastName").value("Lovelace"));
  }

  @Test
  void shouldUpdateStudent() throws Exception {
    Student existing = studentRepository.save(Student.builder().firstName("Alan").lastName("Turing").build());

    StudentDto update = new StudentDto(existing.getId(), "Alan Mathison", "Turing");

    mockMvc
        .perform(
            put("/api/students/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existing.getId().toString()))
        .andExpect(jsonPath("$.firstName").value("Alan Mathison"))
        .andExpect(jsonPath("$.lastName").value("Turing"));
  }

  @Test
  void shouldDeleteStudent() throws Exception {
    Student existing = studentRepository.save(Student.builder().firstName("Grace").lastName("Hopper").build());

    mockMvc.perform(delete("/api/students/{id}", existing.getId())).andExpect(status().isNoContent());

    assertThat(studentRepository.findById(existing.getId())).isEmpty();
  }

  @Test
  void shouldSearchStudentsWithPaging() throws Exception {
    studentRepository.save(Student.builder().firstName("Ada").lastName("Lovelace").build());
    studentRepository.save(Student.builder().firstName("Alan").lastName("Turing").build());

    mockMvc
        .perform(get("/api/students/search").param("first_name", "al").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].firstName").value("Alan"))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.number").value(0));
  }

  @Test
  void shouldReturnProblemDetailForBadRequestAndNotFound() throws Exception {
    StudentDto invalid = new StudentDto(null, "", "Lovelace");

    mockMvc
        .perform(
            post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").exists());

    mockMvc
        .perform(get("/api/students/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.detail").exists());
  }
}
