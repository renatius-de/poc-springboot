package de.renatius.poc.springboot.rest.professor;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProfessorRestMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "title")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  ProfessorDto toDto(Professor professor);

  @InheritInverseConfiguration
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForCreate(ProfessorDto request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "request.title")
  @Mapping(target = "firstName", source = "request.firstName")
  @Mapping(target = "lastName", source = "request.lastName")
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForUpdate(UUID id, ProfessorDto request);
}
