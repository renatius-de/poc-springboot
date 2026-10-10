package de.renatius.poc.springboot.grpc.mapper;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.grpc.v1.Course;
import de.renatius.poc.springboot.grpc.v1.Professor;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesResponse;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsResponse;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsResponse;
import de.renatius.poc.springboot.grpc.v1.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(config = CentralMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicGrpcMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  Student toProto(StudentDto dto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "title", defaultValue = "")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  Professor toProto(ProfessorDto dto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "room", source = "room", defaultValue = "")
  @Mapping(target = "professorId", source = "professorId")
  Course toProto(CourseDto dto);

  default SearchStudentsResponse toStudentSearchResponse(Page<StudentDto> page) {
    SearchStudentsResponse.Builder builder =
        SearchStudentsResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.forEach(dto -> builder.addStudents(toProto(dto)));
    return builder.build();
  }

  default SearchProfessorsResponse toProfessorSearchResponse(Page<ProfessorDto> page) {
    SearchProfessorsResponse.Builder builder =
        SearchProfessorsResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.forEach(dto -> builder.addProfessors(toProto(dto)));
    return builder.build();
  }

  default SearchCoursesResponse toCourseSearchResponse(Page<CourseDto> page) {
    SearchCoursesResponse.Builder builder =
        SearchCoursesResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.forEach(dto -> builder.addCourses(toProto(dto)));
    return builder.build();
  }
}
