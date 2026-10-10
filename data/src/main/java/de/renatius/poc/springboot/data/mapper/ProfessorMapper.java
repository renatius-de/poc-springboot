package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

  default ProfessorDto toDto(Professor professor) {
    return toDtoInternal(MapperSupport.requireSource(professor, "professor"));
  }

  ProfessorDto toDtoInternal(Professor professor);

  default Professor toEntity(ProfessorDto professorDto) {
    return toEntityInternal(MapperSupport.requireSource(professorDto, "professorDto"));
  }

  @Mapping(target = "courses", ignore = true)
  Professor toEntityInternal(ProfessorDto professorDto);
}
