package de.renatius.poc.springboot.grpc.service;

import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.execute;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.notFound;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.parseUuid;
import static de.renatius.poc.springboot.grpc.support.GrpcErrorSupport.requireText;
import static de.renatius.poc.springboot.grpc.support.GrpcPagingSupport.toPageable;
import static de.renatius.poc.springboot.grpc.support.GrpcSpecificationSupport.likeIgnoreCase;

import com.google.protobuf.Empty;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.mapper.ProfessorMapper;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.grpc.mapper.AcademicGrpcMapper;
import de.renatius.poc.springboot.grpc.v1.CreateProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.GetProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.ProfessorServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsRequest;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsResponse;
import de.renatius.poc.springboot.grpc.v1.UpdateProfessorRequest;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class ProfessorGrpcService extends ProfessorServiceGrpc.ProfessorServiceImplBase {

  private final ProfessorRepository professorRepository;
  private final ProfessorMapper professorMapper;
  private final AcademicGrpcMapper grpcMapper;

  public ProfessorGrpcService(
      ProfessorRepository professorRepository,
      ProfessorMapper professorMapper,
      AcademicGrpcMapper grpcMapper) {
    this.professorRepository = professorRepository;
    this.professorMapper = professorMapper;
    this.grpcMapper = grpcMapper;
  }

  @Override
  public void createProfessor(
      CreateProfessorRequest request,
      StreamObserver<de.renatius.poc.springboot.grpc.v1.Professor> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String title = requireText(request.getTitle(), "title");
          String firstName = requireText(request.getFirstName(), "first_name");
          String lastName = requireText(request.getLastName(), "last_name");
          Professor toCreate = professorMapper.toEntity(new ProfessorDto(null, title, firstName, lastName));
          Professor saved = professorRepository.save(toCreate);
          return grpcMapper.toProto(professorMapper.toDto(saved));
        });
  }

  @Override
  public void getProfessor(
      GetProfessorRequest request,
      StreamObserver<de.renatius.poc.springboot.grpc.v1.Professor> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Professor professor =
              professorRepository.findById(id).orElseThrow(() -> notFound("Professor", id));
          return grpcMapper.toProto(professorMapper.toDto(professor));
        });
  }

  @Override
  public void updateProfessor(
      UpdateProfessorRequest request,
      StreamObserver<de.renatius.poc.springboot.grpc.v1.Professor> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          String title = requireText(request.getTitle(), "title");
          String firstName = requireText(request.getFirstName(), "first_name");
          String lastName = requireText(request.getLastName(), "last_name");
          Professor professor =
              professorRepository.findById(id).orElseThrow(() -> notFound("Professor", id));
          professor.setTitle(title);
          professor.setFirstName(firstName);
          professor.setLastName(lastName);
          Professor updated = professorRepository.save(professor);
          return grpcMapper.toProto(professorMapper.toDto(updated));
        });
  }

  @Override
  public void deleteProfessor(DeleteProfessorRequest request, StreamObserver<Empty> responseObserver) {
    execute(
        responseObserver,
        () -> {
          UUID id = parseUuid(request.getId(), "id");
          Professor professor =
              professorRepository.findById(id).orElseThrow(() -> notFound("Professor", id));
          professorRepository.delete(professor);
          return Empty.getDefaultInstance();
        });
  }

  @Override
  public void searchProfessors(
      SearchProfessorsRequest request, StreamObserver<SearchProfessorsResponse> responseObserver) {
    execute(
        responseObserver,
        () -> {
          String firstName = request.hasFirstName() ? request.getFirstName() : null;
          String lastName = request.hasLastName() ? request.getLastName() : null;
          Specification<Professor> specification = (root, query, cb) -> cb.conjunction();
          if (firstName != null && !firstName.isBlank()) {
            specification = specification.and(likeIgnoreCase("firstName", firstName));
          }
          if (lastName != null && !lastName.isBlank()) {
            specification = specification.and(likeIgnoreCase("lastName", lastName));
          }
          var pageable = toPageable(request.hasPage() ? request.getPage() : null, "lastName", "firstName");
          var page = professorRepository.findAll(specification, pageable).map(professorMapper::toDto);
          return grpcMapper.toProfessorSearchResponse(page);
        });
  }
}
