package de.renatius.poc.springboot.grpc.mapper;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.dto.StudentDto;
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
  Student toProto(StudentDto dto);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id")
  @Mapping(target = "title", defaultValue = "")
  @Mapping(target = "firstName")
  @Mapping(target = "lastName")
  Professor toProto(ProfessorDto dto);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id")
  @Mapping(target = "name")
  @Mapping(target = "room", defaultValue = "")
  @Mapping(target = "professorId")
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
