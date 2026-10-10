package de.renatius.poc.springboot.rest.professor;

import lombok.extern.slf4j.Slf4j;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
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
@RequestMapping("/api/professors")
public class ProfessorController {

  private final ProfessorRepository repository;
  private final ProfessorRestMapper mapper;

  public ProfessorController(ProfessorRepository repository, ProfessorRestMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @WithSpan
  @PostMapping
  public ResponseEntity<ProfessorDto> create(@RequestBody ProfessorDto request) {
    log.debug("ProfessorController.create invoked");
    validate(request);
    Professor saved = repository.save(mapper.toEntityForCreate(request));
    return ResponseEntity.created(URI.create("/api/professors/%s".formatted(saved.getId())))
        .body(mapper.toDto(saved));
  }

  @WithSpan
  @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
  public ProfessorDto getById(@SpanAttribute("id") @PathVariable UUID id) {
    log.debug("ProfessorController.getById invoked");
    return mapper.toDto(
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id)));
  }

  @WithSpan
  @PutMapping("/{id}")
  public ProfessorDto update(@SpanAttribute("id") @PathVariable UUID id, @RequestBody ProfessorDto request) {
    log.debug("ProfessorController.update invoked");
    validate(request);
    if (request.id() != null && !request.id().equals(id)) {
      throw new IllegalArgumentException("Body id must match path id");
    }
    Professor existing =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    existing.setTitle(request.title());
    existing.setFirstName(request.firstName());
    existing.setLastName(request.lastName());
    Professor updated = repository.save(existing);
    return mapper.toDto(updated);
  }

  @WithSpan
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@SpanAttribute("id") @PathVariable UUID id) {
    log.debug("ProfessorController.delete invoked");
    Professor existing =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    repository.delete(existing);
    return ResponseEntity.noContent().build();
  }

  @WithSpan
  @GetMapping("/search")
  public Page<ProfessorDto> search(
      @RequestParam(name = "first_name", required = false) String firstName,
      @RequestParam(name = "last_name", required = false) String lastName,
      @PageableDefault(sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC)
          Pageable pageable) {
    log.debug("ProfessorController.search invoked");
    Specification<Professor> specification = alwaysTrue();
    if (firstName != null && !firstName.isBlank()) {
      specification = specification.and(likeIgnoreCase("firstName", firstName));
    }
    if (lastName != null && !lastName.isBlank()) {
      specification = specification.and(likeIgnoreCase("lastName", lastName));
    }
    return repository.findAll(specification, pageable).map(mapper::toDto);
  }

  private static Specification<Professor> alwaysTrue() {
    return (root, query, cb) -> cb.conjunction();
  }

  private static Specification<Professor> likeIgnoreCase(String field, String value) {
    return (root, query, cb) -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
  }

  private static void validate(ProfessorDto request) {
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
