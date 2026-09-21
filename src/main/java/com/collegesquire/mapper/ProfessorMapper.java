package com.collegesquire.mapper;

import com.collegesquire.dto.ProfessorDto;
import com.collegesquire.model.Professor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

    ProfessorDto toDto(Professor entity);

    Professor toEntity(ProfessorDto dto);

    List<ProfessorDto> toDtoList(List<Professor> entities);

    List<Professor> toEntityList(List<ProfessorDto> dtos);
}
