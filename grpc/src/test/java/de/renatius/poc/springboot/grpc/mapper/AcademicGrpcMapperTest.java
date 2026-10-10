package de.renatius.poc.springboot.grpc.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.grpc.v1.Course;
import de.renatius.poc.springboot.grpc.v1.Professor;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesResponse;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsResponse;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsResponse;
import de.renatius.poc.springboot.grpc.v1.Student;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

class AcademicGrpcMapperTest {

  private final AcademicGrpcMapper mapper = new AcademicGrpcMapperImpl();

  @Test
  void mapsStudent() {
    UUID id = UUID.randomUUID();
    Student proto = mapper.toProto(new StudentDto(id, "Ada", "Lovelace"));
    assertThat(proto.getId()).isEqualTo(id.toString());
    assertThat(proto.getFirstName()).isEqualTo("Ada");
    assertThat(proto.getLastName()).isEqualTo("Lovelace");
  }

  @Test
  void rejectsNullSources() {
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toProto((StudentDto) null));
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toProto((ProfessorDto) null));
    assertThatIllegalArgumentException().isThrownBy(() -> mapper.toProto((CourseDto) null));
  }

  @Test
  void mapsProfessorWithAndWithoutTitle() {
    UUID id = UUID.randomUUID();
    Professor withTitle = mapper.toProto(new ProfessorDto(id, "Dr.", "Alan", "Turing"));
    assertThat(withTitle.getId()).isEqualTo(id.toString());
    assertThat(withTitle.getTitle()).isEqualTo("Dr.");
    assertThat(withTitle.getFirstName()).isEqualTo("Alan");
    assertThat(withTitle.getLastName()).isEqualTo("Turing");
    assertThat(mapper.toProto(new ProfessorDto(id, null, "Alan", "Turing")).getTitle()).isEmpty();
  }

  @Test
  void mapsCourseWithAndWithoutOptionalFields() {
    UUID id = UUID.randomUUID();
    UUID professorId = UUID.randomUUID();
    Course full = mapper.toProto(new CourseDto(id, "Math", "A1", professorId));
    assertThat(full.getId()).isEqualTo(id.toString());
    assertThat(full.getName()).isEqualTo("Math");
    assertThat(full.getRoom()).isEqualTo("A1");
    assertThat(full.getProfessorId()).isEqualTo(professorId.toString());

    Course minimal = mapper.toProto(new CourseDto(id, "Math", null, null));
    assertThat(minimal.getRoom()).isEmpty();
    assertThat(minimal.getProfessorId()).isEmpty();
  }

  @Test
  void mapsStudentPage() {
    Page<StudentDto> page =
        new PageImpl<>(
            List.of(new StudentDto(UUID.randomUUID(), "A", "B")), PageRequest.of(1, 1), 3);
    SearchStudentsResponse response = mapper.toStudentSearchResponse(page);
    assertThat(response.getStudentsCount()).isEqualTo(1);
    assertThat(response.getTotalElements()).isEqualTo(3);
    assertThat(response.getTotalPages()).isEqualTo(3);
    assertThat(response.getPage()).isEqualTo(1);
    assertThat(response.getSize()).isEqualTo(1);
  }

  @Test
  void mapsProfessorPage() {
    Page<ProfessorDto> page =
        new PageImpl<>(
            List.of(new ProfessorDto(UUID.randomUUID(), null, "A", "B")), PageRequest.of(0, 2), 1);
    SearchProfessorsResponse response = mapper.toProfessorSearchResponse(page);
    assertThat(response.getProfessorsCount()).isEqualTo(1);
    assertThat(response.getTotalElements()).isEqualTo(1);
    assertThat(response.getSize()).isEqualTo(2);
  }

  @Test
  void mapsEmptyCoursePage() {
    SearchCoursesResponse empty = mapper.toCourseSearchResponse(Page.empty());
    assertThat(empty.getCoursesCount()).isZero();
    Page<CourseDto> page =
        new PageImpl<>(List.of(new CourseDto(UUID.randomUUID(), "N", "R", null)));
    assertThat(mapper.toCourseSearchResponse(page).getCoursesList()).hasSize(1);
  }
}
