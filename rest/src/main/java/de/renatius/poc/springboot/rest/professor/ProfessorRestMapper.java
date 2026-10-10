package de.renatius.poc.springboot.rest.professor;

import de.renatius.poc.springboot.data.dto.ProfessorDto;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.mapper.MapperSupport;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfessorRestMapper {

  default ProfessorDto toDto(Professor professor) {
    return toDtoInternal(MapperSupport.requireSource(professor, "professor"));
  }

  ProfessorDto toDtoInternal(Professor professor);

  default Professor toEntityForCreate(ProfessorDto request) {
    return toEntityForCreateInternal(MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Professor toEntityForCreateInternal(ProfessorDto request);

  default Professor toEntityForUpdate(UUID id, ProfessorDto request) {
    return toEntityForUpdateInternal(
        MapperSupport.requireSource(id, "id"), MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "id", source = "id")
  Professor toEntityForUpdateInternal(UUID id, ProfessorDto request);
}
