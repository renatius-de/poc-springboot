package de.renatius.poc.springboot.rest.student;

import de.renatius.poc.springboot.data.dto.StudentDto;
import de.renatius.poc.springboot.data.entity.Student;
import de.renatius.poc.springboot.data.mapper.StudentMapper;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class StudentRestMapper {

  private final StudentMapper delegate;

  public StudentRestMapper(StudentMapper delegate) {
    this.delegate = delegate;
  }

  public StudentDto toDto(Student student) {
    return delegate.toDto(student);
  }

  public Student toEntityForCreate(StudentDto request) {
    return delegate.toEntity(new StudentDto(null, request.firstName(), request.lastName()));
  }

  public Student toEntityForUpdate(UUID id, StudentDto request) {
    return delegate.toEntity(new StudentDto(id, request.firstName(), request.lastName()));
  }
}
