package de.renatius.poc.springboot.grpc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.grpc.testsupport.AbstractTestcontainersTest;
import de.renatius.poc.springboot.grpc.v1.CreateProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.DeleteProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.GetProfessorRequest;
import de.renatius.poc.springboot.grpc.v1.ProfessorServiceGrpc;
import de.renatius.poc.springboot.grpc.v1.SearchPageRequest;
import de.renatius.poc.springboot.grpc.v1.SearchProfessorsRequest;
import de.renatius.poc.springboot.grpc.v1.UpdateProfessorRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProfessorGrpcServiceIntegrationTest extends AbstractTestcontainersTest {

  @Autowired private ProfessorRepository professorRepository;

  private ProfessorServiceGrpc.ProfessorServiceBlockingStub client;

  @BeforeEach
  void setUpClient() {
    client = ProfessorServiceGrpc.newBlockingStub(channel);
  }

  @Test
  void shouldCreateAndGetProfessor() {
    var created =
        client.createProfessor(
            CreateProfessorRequest.newBuilder()
                .setTitle("Dr.")
                .setFirstName("Ada")
                .setLastName("Lovelace")
                .build());

    assertThat(created.getId()).isNotBlank();
    assertThat(created.getTitle()).isEqualTo("Dr.");

    var loaded = client.getProfessor(GetProfessorRequest.newBuilder().setId(created.getId()).build());
    assertThat(loaded.getId()).isEqualTo(created.getId());
    assertThat(loaded.getLastName()).isEqualTo("Lovelace");
  }

  @Test
  void shouldUpdateProfessor() {
    Professor existing =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Barbara").lastName("Liskov").build());

    var updated =
        client.updateProfessor(
            UpdateProfessorRequest.newBuilder()
                .setId(existing.getId().toString())
                .setTitle("Prof.")
                .setFirstName("Barbara")
                .setLastName("Liskov")
                .build());

    assertThat(updated.getTitle()).isEqualTo("Prof.");
  }

  @Test
  void shouldDeleteProfessor() {
    Professor existing =
        professorRepository.save(
            Professor.builder().title("Dr.").firstName("Grace").lastName("Hopper").build());

    client.deleteProfessor(DeleteProfessorRequest.newBuilder().setId(existing.getId().toString()).build());

    assertThat(professorRepository.findById(existing.getId())).isEmpty();
  }

  @Test
  void shouldSearchProfessorsWithPaging() {
    professorRepository.save(
        Professor.builder().title("Dr.").firstName("Ada").lastName("Lovelace").build());
    professorRepository.save(
        Professor.builder().title("Prof.").firstName("Alan").lastName("Kay").build());

    var response =
        client.searchProfessors(
            SearchProfessorsRequest.newBuilder()
                .setFirstName("al")
                .setPage(SearchPageRequest.newBuilder().setPage(0).setSize(10).build())
                .build());

    assertThat(response.getProfessorsCount()).isEqualTo(1);
    assertThat(response.getProfessors(0).getFirstName()).isEqualTo("Alan");
    assertThat(response.getSize()).isEqualTo(10);
  }

  @Test
  void shouldReturnInvalidArgumentForBlankTitle() {
    assertThatThrownBy(
            () ->
                client.createProfessor(
                    CreateProfessorRequest.newBuilder()
                        .setTitle("")
                        .setFirstName("Ada")
                        .setLastName("Lovelace")
                        .build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.INVALID_ARGUMENT);
  }

  @Test
  void shouldReturnNotFoundForDeleteUnknownProfessor() {
    assertThatThrownBy(
            () ->
                client.deleteProfessor(
                    DeleteProfessorRequest.newBuilder().setId(UUID.randomUUID().toString()).build()))
        .isInstanceOf(StatusRuntimeException.class)
        .extracting(ex -> ((StatusRuntimeException) ex).getStatus().getCode())
        .isEqualTo(Status.Code.NOT_FOUND);
  }
}
