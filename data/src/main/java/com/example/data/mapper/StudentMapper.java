package com.example.data.mapper;

import com.example.data.dto.StudentDto;
import com.example.data.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

  StudentDto toDto(Student student);

  @Mapping(target = "courses", ignore = true)
  Student toEntity(StudentDto studentDto);
}
