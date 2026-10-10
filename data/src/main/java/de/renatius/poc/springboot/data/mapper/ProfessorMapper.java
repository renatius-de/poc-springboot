package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProfessorMapper {

  ProfessorDto toDto(Professor professor);

  @Mapping(target = "courses", ignore = true)
  Professor toEntity(ProfessorDto professorDto);
}
