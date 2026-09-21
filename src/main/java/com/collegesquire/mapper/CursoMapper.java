package com.collegesquire.mapper;

import com.collegesquire.dto.CursoDto;
import com.collegesquire.model.Curso;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    CursoDto toDto(Curso entity);

    Curso toEntity(CursoDto dto);

    List<CursoDto> toDtoList(List<Curso> entities);

    List<Curso> toEntityList(List<CursoDto> dtos);
}
