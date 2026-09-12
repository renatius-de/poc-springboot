package de.renatius.poc.springboot.grpc.service;

import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.execute;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.notFound;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.parseUuid;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.requireText;
import static de.renatius.poc.springboot.grpc.support.GrpcPagingSupport.toPageable;
import static de.renatius.poc.springboot.grpc.support.GrpcSpecificationSupport.likeIgnoreCase;

import com.google.protobuf.Empty;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.mapper.StudentMapper;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import de.renatius.poc.springboot.grpc.mapper.AcademicGrpcMapper;
import de.renatius.poc.springboot.grpc.v1.CreateStudentRequest;
import de.renatius.poc.springboot.grpc.v1.GetStudentRequest;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsRequest;
import de.renatius.poc.springboot.grpc.v1.SearchStudentsResponse;
import de.renatius.poc.springboot.grpc.v1.StudentServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.UpdateStudentRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteStudentRequest;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class StudentGrpcService extends StudentServiceGrpc.StudentServiceImplBase {

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final AcademicGrpcMapper grpcMapper;

  public StudentGrpcService(
      StudentRepository studentRepository, StudentMapper studentMapper, AcademicGrpcMapper grpcMapper) {
    this.studentRepository = studentRepository;
    this.studentMapper = studentMapper;
    this.grpcMapper = grpcMapper;
  }

  @Override
  public void createStudent(CreateStudentRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Student> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String firstName = requireText(request.getFirstName(), "first_name");
          String lastName = requireText(request.getLastName(), "last_name");
          Student toCreate = studentMapper.toEntity(new StudentDto(null, firstName, lastName));
          Student saved = studentRepository.save(toCreate);
          return grpcMapper.toProto(studentMapper.toDto(saved));
        });
  }

  @Override
  public void getStudent(GetStudentRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Student> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Student student =
              studentRepository.findById(id).orElseThrow(() -> notFound("Student", id));
          return grpcMapper.toProto(studentMapper.toDto(student));
        });
  }

  @Override
  public void updateStudent(UpdateStudentRequest request, StreamObserver<de.renatius.poc.springboot.grpc.v1.Student> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          String firstName = requireText(request.getFirstName(), "first_name");
          String lastName = requireText(request.getLastName(), "last_name");
          Student student =
              studentRepository.findById(id).orElseThrow(() -> notFound("Student", id));
          student.setFirstName(firstName);
          student.setLastName(lastName);
          Student updated = studentRepository.save(student);
          return grpcMapper.toProto(studentMapper.toDto(updated));
        });
  }

  @Override
  public void deleteStudent(DeleteStudentRequest request, StreamObserver<Empty> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Student student =
              studentRepository.findById(id).orElseThrow(() -> notFound("Student", id));
          studentRepository.delete(student);
          return Empty.getDefaultInstance();
        });
  }

  @Override
  public void searchStudents(SearchStudentsRequest request, StreamObserver<SearchStudentsResponse> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String firstName = request.hasFirstName() ? request.getFirstName() : null;
          String lastName = request.hasLastName() ? request.getLastName() : null;
          Specification<Student> specification =
              Specification.<Student>where(likeIgnoreCase("firstName", firstName))
                  .and(likeIgnoreCase("lastName", lastName));
          var pageable = toPageable(request.hasPage() ? request.getPage() : null, "lastName", "firstName");
          var page = studentRepository.findAll(specification, pageable).map(studentMapper::toDto);
          return grpcMapper.toStudentSearchResponse(page);
        });
  }
}
