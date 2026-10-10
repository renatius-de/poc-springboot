package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface StudentRestMapper {

  StudentDto toDto(Student student);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "courses", ignore = true)
  Student toEntityForCreate(StudentDto request);

  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "id", source = "id")
  Student toEntityForUpdate(UUID id, StudentDto request);
}
