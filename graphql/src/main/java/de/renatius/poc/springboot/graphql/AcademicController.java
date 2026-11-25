package de.renatius.poc.springboot.graphql;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.mapper.CourseMapper;
import de.renatius.poc.springboot.data.mapper.ProfessorMapper;
import de.renatius.poc.springboot.data.mapper.StudentMapper;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import de.renatius.poc.springboot.data.specification.SearchSpecifications;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AcademicController {
  private final StudentRepository studentRepository;
  private final ProfessorRepository professorRepository;
  private final CourseRepository courseRepository;
  private final StudentMapper studentMapper;
  private final ProfessorMapper professorMapper;
  private final CourseMapper courseMapper;

  @WithSpan
  @QueryMapping
  public StudentDto student(@Argument String id) {
    UUID uuid = parseUuid(id);
    return studentMapper.toDto(
        studentRepository.findById(uuid).orElseThrow(() -> notFound("Student", uuid)));
  }

  @WithSpan
  @QueryMapping
  public StudentPage students(
      @Argument String firstName,
      @Argument String lastName,
      @Argument int page,
      @Argument int size) {
    Specification<Student> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    if (firstName != null && !firstName.isBlank()) {
      specification = specification.and(SearchSpecifications.likeIgnoreCase("firstName", firstName));
    }
    if (lastName != null && !lastName.isBlank()) {
      specification = specification.and(SearchSpecifications.likeIgnoreCase("lastName", lastName));
    }
    Page<StudentDto> result =
        studentRepository
            .findAll(specification, PageRequest.of(page, size, Sort.by("lastName", "firstName").ascending()))
            .map(studentMapper::toDto);
    return new StudentPage(
        result.getContent(), result.getTotalElements(), result.getTotalPages(),
        result.getNumber(), result.getSize());
  }

  @WithSpan
  @MutationMapping
  public StudentDto createStudent(@Argument StudentInput input) {
    validateStudent(input);
    Student saved =
        studentRepository.save(studentMapper.toEntity(new StudentDto(null, input.firstName, input.lastName)));
    return studentMapper.toDto(saved);
  }

  @WithSpan
  @MutationMapping
  public StudentDto updateStudent(@Argument String id, @Argument StudentInput input) {
    UUID uuid = parseUuid(id);
    validateStudent(input);
    Student existing =
        studentRepository.findById(uuid).orElseThrow(() -> notFound("Student", uuid));
    existing.setFirstName(input.firstName);
    existing.setLastName(input.lastName);
    return studentMapper.toDto(studentRepository.save(existing));
  }

  @WithSpan
  @MutationMapping
  public boolean deleteStudent(@Argument String id) {
    UUID uuid = parseUuid(id);
    Student existing =
        studentRepository.findById(uuid).orElseThrow(() -> notFound("Student", uuid));
    studentRepository.delete(existing);
    return true;
  }

  @WithSpan
  @QueryMapping
  public ProfessorDto professor(@Argument String id) {
    UUID uuid = parseUuid(id);
    return professorMapper.toDto(
        professorRepository.findById(uuid).orElseThrow(() -> notFound("Professor", uuid)));
  }

  @WithSpan
  @QueryMapping
  public ProfessorPage professors(
      @Argument String firstName,
      @Argument String lastName,
      @Argument int page,
      @Argument int size) {
    Specification<Professor> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    if (firstName != null && !firstName.isBlank()) {
      specification = specification.and(SearchSpecifications.likeIgnoreCase("firstName", firstName));
    }
    if (lastName != null && !lastName.isBlank()) {
      specification = specification.and(SearchSpecifications.likeIgnoreCase("lastName", lastName));
    }
    Page<ProfessorDto> result =
        professorRepository
            .findAll(specification, PageRequest.of(page, size, Sort.by("lastName", "firstName").ascending()))
            .map(professorMapper::toDto);
    return new ProfessorPage(
        result.getContent(), result.getTotalElements(), result.getTotalPages(),
        result.getNumber(), result.getSize());
  }

  @WithSpan
  @MutationMapping
  public ProfessorDto createProfessor(@Argument ProfessorInput input) {
    validateProfessor(input);
    Professor saved =
        professorRepository.save(
            professorMapper.toEntity(new ProfessorDto(null, input.title, input.firstName, input.lastName)));
    return professorMapper.toDto(saved);
  }

  @WithSpan
  @MutationMapping
  public ProfessorDto updateProfessor(@Argument String id, @Argument ProfessorInput input) {
    UUID uuid = parseUuid(id);
    validateProfessor(input);
    Professor existing =
        professorRepository.findById(uuid).orElseThrow(() -> notFound("Professor", uuid));
    existing.setTitle(input.title);
    existing.setFirstName(input.firstName);
    existing.setLastName(input.lastName);
    return professorMapper.toDto(professorRepository.save(existing));
  }

  @WithSpan
  @MutationMapping
  public boolean deleteProfessor(@Argument String id) {
    UUID uuid = parseUuid(id);
    Professor existing =
        professorRepository.findById(uuid).orElseThrow(() -> notFound("Professor", uuid));
    professorRepository.delete(existing);
    return true;
  }

  @WithSpan
  @QueryMapping
  public CourseDto course(@Argument String id) {
    UUID uuid = parseUuid(id);
    return courseMapper.toDto(
        courseRepository.findById(uuid).orElseThrow(() -> notFound("Course", uuid)));
  }

  @WithSpan
  @QueryMapping
  public CoursePage courses(@Argument String name, @Argument int page, @Argument int size) {
    Specification<Course> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    if (name != null && !name.isBlank()) {
      specification = specification.and(SearchSpecifications.likeIgnoreCase("name", name));
    }
    Page<CourseDto> result =
        courseRepository
            .findAll(specification, PageRequest.of(page, size, Sort.by("name").ascending()))
            .map(courseMapper::toDto);
    return new CoursePage(
        result.getContent(), result.getTotalElements(), result.getTotalPages(),
        result.getNumber(), result.getSize());
  }

  @WithSpan
  @MutationMapping
  public CourseDto createCourse(@Argument CourseInput input) {
    validateCourse(input);
    Course course =
        Course.builder()
            .name(input.name)
            .room(input.room)
            .professor(professorRepository.getReferenceById(input.professorId))
            .build();
    return courseMapper.toDto(courseRepository.save(course));
  }

  @WithSpan
  @MutationMapping
  public CourseDto updateCourse(@Argument String id, @Argument CourseInput input) {
    UUID uuid = parseUuid(id);
    validateCourse(input);
    Course existing =
        courseRepository.findById(uuid).orElseThrow(() -> notFound("Course", uuid));
    existing.setName(input.name);
    existing.setRoom(input.room);
    existing.setProfessor(professorRepository.getReferenceById(input.professorId));
    return courseMapper.toDto(courseRepository.save(existing));
  }

  @WithSpan
  @MutationMapping
  public boolean deleteCourse(@Argument String id) {
    UUID uuid = parseUuid(id);
    Course existing =
        courseRepository.findById(uuid).orElseThrow(() -> notFound("Course", uuid));
    courseRepository.delete(existing);
    return true;
  }

  private static UUID parseUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (RuntimeException exception) {
      throw new IllegalArgumentException("id must be a valid UUID", exception);
    }
  }

  private static IllegalArgumentException notFound(String resource, UUID id) {
    return new IllegalArgumentException("%s %s was not found".formatted(resource, id));
  }

  private static void validateStudent(StudentInput input) {
    if (input == null
        || input.firstName == null
        || input.firstName.isBlank()
        || input.lastName == null
        || input.lastName.isBlank()) {
      throw new IllegalArgumentException("firstName and lastName must not be blank");
    }
  }

  private static void validateProfessor(ProfessorInput input) {
    if (input == null
        || input.firstName == null
        || input.firstName.isBlank()
        || input.lastName == null
        || input.lastName.isBlank()) {
      throw new IllegalArgumentException("firstName and lastName must not be blank");
    }
  }

  private void validateCourse(CourseInput input) {
    if (input == null
        || input.name == null
        || input.name.isBlank()
        || input.professorId == null) {
      throw new IllegalArgumentException("name must not be blank and professorId is required");
    }
    if (!professorRepository.existsById(input.professorId)) {
      throw new IllegalArgumentException("professorId '%s' does not exist".formatted(input.professorId));
    }
  }

  public record StudentInput(String firstName, String lastName) {}
  public record ProfessorInput(String title, String firstName, String lastName) {}
  public record CourseInput(String name, String room, UUID professorId) {}
  public record StudentPage(
      java.util.List<StudentDto> content,
      long totalElements,
      int totalPages,
      int page,
      int size) {}
  public record ProfessorPage(
      java.util.List<ProfessorDto> content,
      long totalElements,
      int totalPages,
      int page,
      int size) {}
  public record CoursePage(
      java.util.List<CourseDto> content,
      long totalElements,
      int totalPages,
      int page,
      int size) {}
}
