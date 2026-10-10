package de.renatius.poc.springboot.data.mapper;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

  default CourseDto toDto(Course course) {
    return toDtoInternal(MapperSupport.requireSource(course, "course"));
  }

  @Mapping(target = "professorId", source = "professor.id")
  CourseDto toDtoInternal(Course course);

  default Course toEntity(CourseDto courseDto) {
    return toEntityInternal(MapperSupport.requireSource(courseDto, "courseDto"));
  }

  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "professorId")
  Course toEntityInternal(CourseDto courseDto);

  default Professor mapProfessor(UUID professorId) {
    if (professorId == null) {
      return null;
    }
    Professor professor = new Professor();
    professor.setId(professorId);
    return professor;
  }
}
