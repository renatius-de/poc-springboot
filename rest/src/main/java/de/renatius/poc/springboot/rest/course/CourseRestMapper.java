package de.renatius.poc.springboot.rest.course;

import de.renatius.poc.springboot.data.config.CentralMapperConfig;
import de.renatius.poc.springboot.data.dto.CourseDto;
import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface CourseRestMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "room", source = "room")
  @Mapping(target = "professorId", source = "professor.id")
  CourseDto toDto(Course course);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = "name")
  @Mapping(target = "room", source = "room")
  @Mapping(target = "students", ignore = true)
  @Mapping(target = "professor", source = "professorId")
  Course toEntityForCreate(CourseDto request);

  @BeanMapping(ignoreByDefault = true)
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
