package com.collegesquire.mapper;

import com.collegesquire.dto.DisciplinaDto;
import com.collegesquire.model.Disciplina;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TurmaMapper.class, ProfessorMapper.class})
public interface DisciplinaMapper {

    DisciplinaDto toDto(Disciplina entity);

    Disciplina toEntity(DisciplinaDto dto);

    List<DisciplinaDto> toDtoList(List<Disciplina> entities);

    List<Disciplina> toEntityList(List<DisciplinaDto> dtos);
}
