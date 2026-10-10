package de.renatius.poc.springboot.rest.course;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.mapper.MapperSupport;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseRestMapper {

  default CourseDto toDto(Course course) {
    return toDtoInternal(MapperSupport.requireSource(course, "course"));
  }

  @Mapping(target = "professorId", source = "professor.id")
  CourseDto toDtoInternal(Course course);

  default Course toEntityForCreate(CourseDto request) {
    return toEntityForCreateInternal(MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "professorId")
  Course toEntityForCreateInternal(CourseDto request);

  default Course toEntityForUpdate(UUID id, CourseDto request) {
    return toEntityForUpdateInternal(
        MapperSupport.requireSource(id, "id"), MapperSupport.requireSource(request, "request"));
  }

  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "request.professorId")
  @Mapping(target = "id", source = "id")
  Course toEntityForUpdateInternal(UUID id, CourseDto request);

  default Professor mapProfessor(UUID professorId) {
    if (professorId == null) {
      return null;
    }
    Professor professor = new Professor();
    professor.setId(professorId);
    return professor;
  }
}
