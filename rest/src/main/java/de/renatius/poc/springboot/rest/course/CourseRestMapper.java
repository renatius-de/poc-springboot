package de.renatius.poc.springboot.rest.course;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.mapper.CourseMapper;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CourseRestMapper {

  private final CourseMapper delegate;

  public CourseRestMapper(CourseMapper delegate) {
    this.delegate = delegate;
  }

  public CourseDto toDto(Course course) {
    return delegate.toDto(course);
  }

  public Course toEntityForCreate(CourseDto request) {
    return delegate.toEntity(new CourseDto(null, request.name(), request.room(), request.professorId()));
  }

  public Course toEntityForUpdate(UUID id, CourseDto request) {
    return delegate.toEntity(new CourseDto(id, request.name(), request.room(), request.professorId()));
  }
}
