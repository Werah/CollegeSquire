package com.collegesquire.mapper;

import com.collegesquire.dto.TurmaDto;
import com.collegesquire.model.Turma;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CursoMapper.class)
public interface TurmaMapper {

    TurmaDto toDto(Turma entity);

    Turma toEntity(TurmaDto dto);

    List<TurmaDto> toDtoList(List<Turma> entities);

    List<Turma> toEntityList(List<TurmaDto> dtos);
}
