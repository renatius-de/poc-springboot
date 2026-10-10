package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StrictProfessorMapper {

  default Professor mapToEntity(ProfessorDto source) {
    if (source == null) {
      throw new IllegalArgumentException("source must not be null");
    }
    return mapInternal(source);
  }

  @Mapping(target = "courses", ignore = true)
  Professor mapInternal(ProfessorDto source);
}
