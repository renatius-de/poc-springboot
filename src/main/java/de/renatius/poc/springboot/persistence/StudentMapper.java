package de.renatius.poc.springboot.persistence;

import de.renatius.poc.springboot.domain.entity.CourseParticipant;
import de.renatius.poc.springboot.persistence.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface StudentMapper {
    CourseParticipant toCourseParticipant(Student student);

    @Mapping(source = "createdAt", target = "createdAt", defaultExpression = "java(OffsetDateTime.now())")
    @Mapping(source = "updatedAt", target = "updatedAt", defaultExpression = "java(OffsetDateTime.now())")
    @Mapping(source = "courses", target = "courses", defaultExpression = "java(new LinkedHashSet<>())")
    Student toStudent(CourseParticipant courseParticipant);
}
