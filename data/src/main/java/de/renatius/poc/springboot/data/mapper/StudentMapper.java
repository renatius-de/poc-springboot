package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

  StudentDto toDto(Student student);

  @Mapping(target = "courses", ignore = true)
  Student toEntity(StudentDto studentDto);
}
