package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.repository.StudentRepository;
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
@RequestMapping("/api/students")
public class StudentController {

  private final StudentRepository repository;
  private final StudentRestMapper mapper;

  public StudentController(StudentRepository repository, StudentRestMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<StudentDto> create(@RequestBody StudentDto request) {
    validate(request);
    Student saved = repository.save(mapper.toEntityForCreate(request));
    return ResponseEntity.created(URI.create("/api/students/%s".formatted(saved.getId())))
        .body(mapper.toDto(saved));
  }

  @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
  public StudentDto getById(@PathVariable UUID id) {
    return mapper.toDto(
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id)));
  }

  @PutMapping("/{id}")
  public StudentDto update(@PathVariable UUID id, @RequestBody StudentDto request) {
    validate(request);
    if (request.id() != null && !request.id().equals(id)) {
      throw new IllegalArgumentException("Body id must match path id");
    }
    Student existing =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
    existing.setFirstName(request.firstName());
    existing.setLastName(request.lastName());
    Student updated = repository.save(existing);
    return mapper.toDto(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    Student existing =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
    repository.delete(existing);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public Page<StudentDto> search(
      @RequestParam(name = "first_name", required = false) String firstName,
      @RequestParam(name = "last_name", required = false) String lastName,
      @PageableDefault(sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC)
          Pageable pageable) {
    Specification<Student> specification = alwaysTrue();
    if (firstName != null && !firstName.isBlank()) {
      specification = specification.and(likeIgnoreCase("firstName", firstName));
    }
    if (lastName != null && !lastName.isBlank()) {
      specification = specification.and(likeIgnoreCase("lastName", lastName));
    }
    return repository.findAll(specification, pageable).map(mapper::toDto);
  }

  private static Specification<Student> alwaysTrue() {
    return (root, query, cb) -> cb.conjunction();
  }

  private static Specification<Student> likeIgnoreCase(String field, String value) {
    return (root, query, cb) -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
  }

  private static void validate(StudentDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Request body must not be null");
    }
    if (request.firstName() == null || request.firstName().isBlank()) {
      throw new IllegalArgumentException("firstName must not be blank");
    }
    if (request.lastName() == null || request.lastName().isBlank()) {
      throw new IllegalArgumentException("lastName must not be blank");
    }
  }
}
