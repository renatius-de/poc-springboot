package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface StudentRestMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  StudentDto toDto(Student student);

  @InheritInverseConfiguration
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Student toEntityForCreate(StudentDto request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "firstName", source = "request.firstName")
  @Mapping(target = "lastName", source = "request.lastName")
  @Mapping(target = "courses", ignore = true)
  Student toEntityForUpdate(UUID id, StudentDto request);
}
