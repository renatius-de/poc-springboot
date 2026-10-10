package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentRestMapper {

  StudentDto toDto(Student student);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Student toEntityForCreate(StudentDto request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "firstName", source = "request.firstName")
  @Mapping(target = "lastName", source = "request.lastName")
  @Mapping(target = "courses", ignore = true)
  Student toEntityForUpdate(UUID id, StudentDto request);
}
