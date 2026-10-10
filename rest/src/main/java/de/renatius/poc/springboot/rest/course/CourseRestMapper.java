package de.renatius.poc.springboot.rest.course;

import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseRestMapper {

  @Mapping(target = "professorId", source = "professor.id")
  CourseDto toDto(Course course);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "professorId")
  Course toEntityForCreate(CourseDto request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "room", source = "request.room")
  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "request.professorId")
  Course toEntityForUpdate(UUID id, CourseDto request);

  default Professor mapProfessor(UUID professorId) {
    if (professorId == null) {
      return null;
    }
    Professor professor = new Professor();
    professor.setId(professorId);
    return professor;
  }
}
