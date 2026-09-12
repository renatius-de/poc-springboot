package com.example.data.mapper;

import com.example.data.dto.ProfessorDto;
import com.example.data.entity.Professor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

  ProfessorDto toDto(Professor professor);

  Professor toEntity(ProfessorDto professorDto);
}
