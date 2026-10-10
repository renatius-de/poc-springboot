package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProfessorMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "title")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  ProfessorDto toDto(Professor professor);

  @InheritInverseConfiguration
  @Mapping(target = "courses", ignore = true)
  Professor toEntity(ProfessorDto professorDto);
}
