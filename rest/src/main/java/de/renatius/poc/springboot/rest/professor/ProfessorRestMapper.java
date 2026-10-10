package de.renatius.poc.springboot.rest.professor;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProfessorRestMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "title")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  ProfessorDto toDto(Professor professor);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "title", source = "title")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForCreate(ProfessorDto request);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "title", source = "request.title")
  @Mapping(target = "firstName", source = "request.firstName")
  @Mapping(target = "lastName", source = "request.lastName")
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForUpdate(UUID id, ProfessorDto request);
}
