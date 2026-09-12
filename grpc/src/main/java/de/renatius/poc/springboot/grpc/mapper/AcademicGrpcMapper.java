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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AcademicGrpcMapper {

  public Student toProto(StudentDto dto) {
    return Student.newBuilder()
        .setId(dto.id().toString())
        .setFirstName(dto.firstName())
        .setLastName(dto.lastName())
        .build();
  }

  public Professor toProto(ProfessorDto dto) {
    return Professor.newBuilder()
        .setId(dto.id().toString())
        .setTitle(dto.title() == null ? "" : dto.title())
        .setFirstName(dto.firstName())
        .setLastName(dto.lastName())
        .build();
  }

  public Course toProto(CourseDto dto) {
    Course.Builder builder =
        Course.newBuilder().setId(dto.id().toString()).setName(dto.name()).setRoom(dto.room() == null ? "" : dto.room());
    if (dto.professorId() != null) {
      builder.setProfessorId(dto.professorId().toString());
    }
    return builder.build();
  }

  public SearchStudentsResponse toStudentSearchResponse(Page<StudentDto> page) {
    SearchStudentsResponse.Builder builder =
        SearchStudentsResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.map(this::toProto).forEach(builder::addStudents);
    return builder.build();
  }

  public SearchProfessorsResponse toProfessorSearchResponse(Page<ProfessorDto> page) {
    SearchProfessorsResponse.Builder builder =
        SearchProfessorsResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.map(this::toProto).forEach(builder::addProfessors);
    return builder.build();
  }

  public SearchCoursesResponse toCourseSearchResponse(Page<CourseDto> page) {
    SearchCoursesResponse.Builder builder =
        SearchCoursesResponse.newBuilder()
            .setTotalElements(page.getTotalElements())
            .setTotalPages(page.getTotalPages())
            .setPage(page.getNumber())
            .setSize(page.getSize());
    page.map(this::toProto).forEach(builder::addCourses);
    return builder.build();
  }
}
