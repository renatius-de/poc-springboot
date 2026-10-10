package de.renatius.poc.springboot.grpc.mapper;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.mapper.MapperSupport;
import de.renatius.poc.springboot.grpc.v1.Course;
import de.renatius.poc.springboot.grpc.v1.Professor;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesResponse;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsResponse;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsResponse;
import de.renatius.poc.springboot.grpc.v1.Student;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AcademicGrpcMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id")
  @Mapping(target = "firstName")
  @Mapping(target = "lastName")
  Student toProtoInternal(StudentDto dto);

  default Student toProto(StudentDto dto) {
    return toProtoInternal(MapperSupport.requireSource(dto, "dto"));
  }

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id")
  @Mapping(target = "title", defaultValue = "")
  @Mapping(target = "firstName")
  @Mapping(target = "lastName")
  Professor toProtoInternal(ProfessorDto dto);

  default Professor toProto(ProfessorDto dto) {
    return toProtoInternal(MapperSupport.requireSource(dto, "dto"));
  }

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id")
  @Mapping(target = "name")
  @Mapping(target = "room", defaultValue = "")
  @Mapping(target = "professorId")
  Course toProtoInternal(CourseDto dto);

  default Course toProto(CourseDto dto) {
    return toProtoInternal(MapperSupport.requireSource(dto, "dto"));
  }

  default SearchStudentsResponse toStudentSearchResponse(Page<StudentDto> page) {
    MapperSupport.requireSource(page, "page");
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
    MapperSupport.requireSource(page, "page");
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
    MapperSupport.requireSource(page, "page");
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
