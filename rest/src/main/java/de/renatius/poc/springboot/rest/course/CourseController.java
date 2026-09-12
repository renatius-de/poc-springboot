package de.renatius.poc.springboot.rest.course;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.rest.exception.ResourceNotFoundException;
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

@RestController
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

  @PostMapping
  public ResponseEntity<CourseDto> create(@RequestBody CourseDto request) {
    validate(request);
    Course saved = courseRepository.save(mapper.toEntityForCreate(request));
    return ResponseEntity.created(URI.create("/api/courses/%s".formatted(saved.getId())))
        .body(mapper.toDto(saved));
  }

  @GetMapping("/{id}")
  public CourseDto getById(@PathVariable UUID id) {
    return mapper.toDto(
        courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id)));
  }

  @PutMapping("/{id}")
  public CourseDto update(@PathVariable UUID id, @RequestBody CourseDto request) {
    validate(request);
    if (request.id() != null && !request.id().equals(id)) {
      throw new IllegalArgumentException("Body id must match path id");
    }
    if (!courseRepository.existsById(id)) {
      throw new ResourceNotFoundException("Course", id);
    }
    Course updated = courseRepository.save(mapper.toEntityForUpdate(id, request));
    return mapper.toDto(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    Course existing =
        courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
    courseRepository.delete(existing);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public Page<CourseDto> search(
      @RequestParam(name = "name", required = false) String name,
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
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
    if (request.professorId() != null && !professorRepository.existsById(request.professorId())) {
      throw new IllegalArgumentException("professorId '%s' does not exist".formatted(request.professorId()));
    }
  }
}
