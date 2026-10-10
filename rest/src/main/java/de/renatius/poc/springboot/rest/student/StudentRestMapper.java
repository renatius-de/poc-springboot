package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.mapper.MapperSupport;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentRestMapper {

  default StudentDto toDto(Student student) {
    return toDtoInternal(MapperSupport.requireSource(student, "student"));
  }

  StudentDto toDtoInternal(Student student);

  default Student toEntityForCreate(StudentDto request) {
    return toEntityForCreateInternal(MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Student toEntityForCreateInternal(StudentDto request);

  default Student toEntityForUpdate(UUID id, StudentDto request) {
    return toEntityForUpdateInternal(
        MapperSupport.requireSource(id, "id"), MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "id", source = "id")
  Student toEntityForUpdateInternal(UUID id, StudentDto request);
}
