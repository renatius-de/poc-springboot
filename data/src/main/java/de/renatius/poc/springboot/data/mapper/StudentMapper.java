package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

  default StudentDto toDto(Student student) {
    return toDtoInternal(MapperSupport.requireSource(student, "student"));
  }

  StudentDto toDtoInternal(Student student);

  default Student toEntity(StudentDto studentDto) {
    return toEntityInternal(MapperSupport.requireSource(studentDto, "studentDto"));
  }

  @Mapping(target = "courses", ignore = true)
  Student toEntityInternal(StudentDto studentDto);
}
