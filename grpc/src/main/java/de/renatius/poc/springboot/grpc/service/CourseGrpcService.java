package de.renatius.poc.springboot.grpc.service;

import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.execute;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.notFound;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.parseUuid;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.requireText;
import static de.renatius.poc.springboot.grpc.support.GrpcPagingSupport.toPageable;
import static de.renatius.poc.springboot.grpc.support.GrpcSpecificationSupport.likeIgnoreCase;

import com.google.protobuf.Empty;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.mapper.CourseMapper;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.grpc.mapper.AcademicGrpcMapper;
import de.renatius.poc.springboot.grpc.v1.CourseServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.CreateCourseRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteCourseRequest;
import de.renatius.poc.springboot.grpc.v1.GetCourseRequest;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesRequest;
import de.renatius.poc.springboot.grpc.v1.SearchCoursesResponse;
import de.renatius.poc.springboot.grpc.v1.UpdateCourseRequest;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class CourseGrpcService extends CourseServiceGrpc.CourseServiceImplBase {

  private final CourseRepository courseRepository;
  private final ProfessorRepository professorRepository;
  private final CourseMapper courseMapper;
  private final AcademicGrpcMapper grpcMapper;

  public CourseGrpcService(
      CourseRepository courseRepository,
      ProfessorRepository professorRepository,
      CourseMapper courseMapper,
      AcademicGrpcMapper grpcMapper) {
    this.courseRepository = courseRepository;
    this.professorRepository = professorRepository;
    this.courseMapper = courseMapper;
    this.grpcMapper = grpcMapper;
  }

  @Override
  public void createCourse(
      CreateCourseRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Course> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String name = requireText(request.getName(), "name");
          String room = requireText(request.getRoom(), "room");
          UUID professorId = parseUuid(request.getProfessorId(), "professor_id");
          Professor professor =
              professorRepository.findById(professorId).orElseThrow(() -> notFound("Professor", professorId));

          Course course = new Course();
          course.setName(name);
          course.setRoom(room);
          course.setProfessor(professor);

          Course saved = courseRepository.save(course);
          return grpcMapper.toProto(courseMapper.toDto(saved));
        });
  }

  @Override
  public void getCourse(
      GetCourseRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Course> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Course", id));
          return grpcMapper.toProto(courseMapper.toDto(course));
        });
  }

  @Override
  public void updateCourse(
      UpdateCourseRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Course> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          String name = requireText(request.getName(), "name");
          String room = requireText(request.getRoom(), "room");
          UUID professorId = parseUuid(request.getProfessorId(), "professor_id");

          Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Course", id));
          Professor professor =
              professorRepository.findById(professorId).orElseThrow(() -> notFound("Professor", professorId));

          course.setName(name);
          course.setRoom(room);
          course.setProfessor(professor);

          Course updated = courseRepository.save(course);
          return grpcMapper.toProto(courseMapper.toDto(updated));
        });
  }

  @Override
  public void deleteCourse(DeleteCourseRequest request, StreamObserver<Empty> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Course", id));
          courseRepository.delete(course);
          return Empty.getDefaultInstance();
        });
  }

  @Override
  public void searchCourses(
      SearchCoursesRequest request, StreamObserver<SearchCoursesResponse> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String name = request.hasName() ? request.getName() : null;
          Specification<Course> specification = Specification.where(likeIgnoreCase("name", name));
          var pageable = toPageable(request.hasPage() ? request.getPage() : null, "name");
          var page = courseRepository.findAll(specification, pageable).map(courseMapper::toDto);
          return grpcMapper.toCourseSearchResponse(page);
        });
  }
}
