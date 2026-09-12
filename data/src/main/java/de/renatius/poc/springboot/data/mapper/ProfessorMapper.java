package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

  ProfessorDto toDto(Professor professor);

  Professor toEntity(ProfessorDto professorDto);
}
