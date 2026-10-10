package de.renatius.poc.springboot.rest.professor;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProfessorRestMapper {

  ProfessorDto toDto(Professor professor);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForCreate(ProfessorDto request);

  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "id", source = "id")
  Professor toEntityForUpdate(UUID id, ProfessorDto request);
}
