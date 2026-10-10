package de.renatius.poc.springboot.rest.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.rest.exception.ResourceNotFoundException;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Courses", description = "Course management")
@RequestMapping("/api/courses")
public class CourseController {

  private final CourseRepository courseRepository;
  private final ProfessorRepository professorRepository;
  private final CourseRestMapper mapper;

  public CourseController(
      CourseRepository courseRepository,
      ProfessorRepository professorRepository,
      CourseRestMapper mapper) {
    this.courseRepository = courseRepository;
    this.professorRepository = professorRepository;
    this.mapper = mapper;
  }

  @Operation(summary = "Create course")
  @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
      })
  @WithSpan
  @PostMapping
  public ResponseEntity<CourseDto> create(@RequestBody CourseDto request) {
    log.debug("CourseController.create invoked");
    validate(request);
    Course saved = courseRepository.save(mapper.toEntityForCreate(request));
    return ResponseEntity.created(URI.create("/api/courses/%s".formatted(saved.getId())))
        .body(mapper.toDto(saved));
  }

  @Operation(summary = "Get course by id")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "404", description = "Not found")
      })
  @WithSpan
  @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
  public CourseDto getById(@SpanAttribute("id") @PathVariable UUID id) {
    log.debug("CourseController.getById invoked");
    return mapper.toDto(
        courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id)));
  }

  @Operation(summary = "Update course")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Not found")
      })
  @WithSpan
  @PutMapping("/{id}")
  public CourseDto update(@SpanAttribute("id") @PathVariable UUID id, @RequestBody CourseDto request) {
    log.debug("CourseController.update invoked");
    validate(request);
    if (request.id() != null && !request.id().equals(id)) {
      throw new IllegalArgumentException("Body id must match path id");
    }
    Course existing =
        courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
    existing.setName(request.name());
    existing.setRoom(request.room());
    existing.setProfessor(professorRepository.getReferenceById(request.professorId()));
    Course updated = courseRepository.save(existing);
    return mapper.toDto(updated);
  }

  @Operation(summary = "Delete course")
  @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Success"),
        @ApiResponse(responseCode = "404", description = "Not found")
      })
  @WithSpan
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@SpanAttribute("id") @PathVariable UUID id) {
    log.debug("CourseController.delete invoked");
    Course existing =
        courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
    courseRepository.delete(existing);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Search courses")
  @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success")
      })
  @WithSpan
  @GetMapping("/search")
  public Page<CourseDto> search(
      @RequestParam(name = "name", required = false) String name,
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    log.debug("CourseController.search invoked");
    Specification<Course> specification = Specification.where(likeIgnoreCase("name", name));
    return courseRepository.findAll(specification, pageable).map(mapper::toDto);
  }

  private Specification<Course> likeIgnoreCase(String field, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return (root, query, cb) -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
  }

  private void validate(CourseDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Request body must not be null");
    }
    if (request.name() == null || request.name().isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    if (request.professorId() == null) {
      throw new IllegalArgumentException("professorId must not be null");
    }
    if (!professorRepository.existsById(request.professorId())) {
      throw new IllegalArgumentException("professorId '%s' does not exist".formatted(request.professorId()));
    }
  }
}
