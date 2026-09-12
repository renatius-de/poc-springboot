package de.renatius.poc.springboot.rest.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.rest.testsupport.AbstractTestcontainersTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class CourseControllerIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CourseRepository courseRepository;
  @Autowired private ProfessorRepository professorRepository;

  @Test
  void shouldCreateAndGetCourse() throws Exception {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Ada").lastName("Lovelace").build());
    CourseDto request = new CourseDto(null, "Distributed Systems", "A-101", professor.getId());

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/courses/.+")))
            .andExpect(jsonPath("$.name").value("Distributed Systems"))
            .andExpect(jsonPath("$.room").value("A-101"))
            .andExpect(jsonPath("$.professorId").value(professor.getId().toString()))
            .andReturn();

    String location = createResult.getResponse().getHeader("Location");
    assertThat(location).isNotBlank();
    UUID id = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

    mockMvc
        .perform(get("/api/courses/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Distributed Systems"))
        .andExpect(jsonPath("$.room").value("A-101"))
        .andExpect(jsonPath("$.professorId").value(professor.getId().toString()));
  }

  @Test
  void shouldUpdateCourse() throws Exception {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Alan").lastName("Turing").build());
    Course existing = courseRepository.save(Course.builder().name("Algorithms").room("B-202").build());
    existing.setProfessor(professor);
    existing = courseRepository.save(existing);

    CourseDto update = new CourseDto(existing.getId(), "Advanced Algorithms", "C-303", professor.getId());

    mockMvc
        .perform(
            put("/api/courses/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existing.getId().toString()))
        .andExpect(jsonPath("$.name").value("Advanced Algorithms"))
        .andExpect(jsonPath("$.room").value("C-303"))
        .andExpect(jsonPath("$.professorId").value(professor.getId().toString()));
  }

  @Test
  void shouldReturnBadRequestWhenUpdateBodyIdMismatchesPathId() throws Exception {
    Professor professor =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Alan").lastName("Turing").build());
    Course existing = courseRepository.save(Course.builder().name("Algorithms").room("B-202").build());
    CourseDto update = new CourseDto(UUID.randomUUID(), "Algorithms", "B-202", professor.getId());

    mockMvc
        .perform(
            put("/api/courses/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").exists());
  }

  @Test
  void shouldDeleteCourse() throws Exception {
    Course existing = courseRepository.save(Course.builder().name("Compilers").room("D-404").build());

    mockMvc.perform(delete("/api/courses/{id}", existing.getId())).andExpect(status().isNoContent());

    assertThat(courseRepository.findById(existing.getId())).isEmpty();
  }

  @Test
  void shouldSearchCoursesWithPaging() throws Exception {
    courseRepository.save(Course.builder().name("Distributed Systems").room("A-101").build());
    courseRepository.save(Course.builder().name("Operating Systems").room("B-202").build());

    mockMvc
        .perform(get("/api/courses/search").param("name", "systems").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.number").value(0));
  }

  @Test
  void shouldReturnProblemDetailForBadRequestAndNotFound() throws Exception {
    CourseDto invalid = new CourseDto(null, "", "A-101", UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").exists());

    mockMvc
        .perform(get("/api/courses/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.detail").exists());
  }

  @Test
  void shouldReturnBadRequestForUnknownProfessorId() throws Exception {
    CourseDto request = new CourseDto(null, "Networks", "N-101", UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").exists());
  }
}
