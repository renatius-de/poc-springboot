package de.renatius.poc.springboot.rest.professor;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.mapper.ProfessorMapper;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProfessorRestMapper {

  private final ProfessorMapper delegate;

  public ProfessorRestMapper(ProfessorMapper delegate) {
    this.delegate = delegate;
  }

  public ProfessorDto toDto(Professor professor) {
    return delegate.toDto(professor);
  }

  public Professor toEntityForCreate(ProfessorDto request) {
    return delegate.toEntity(
        new ProfessorDto(null, request.title(), request.firstName(), request.lastName()));
  }

  public Professor toEntityForUpdate(UUID id, ProfessorDto request) {
    return delegate.toEntity(new ProfessorDto(id, request.title(), request.firstName(), request.lastName()));
  }
}
