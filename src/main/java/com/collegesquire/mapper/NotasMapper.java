package com.collegesquire.mapper;

import com.collegesquire.dto.NotasDto;
import com.collegesquire.model.Notas;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AlunoMapper.class, DisciplinaMapper.class})
public interface NotasMapper {

    NotasDto toDto(Notas entity);

    Notas toEntity(NotasDto dto);

    List<NotasDto> toDtoList(List<Notas> entities);

    List<Notas> toEntityList(List<NotasDto> dtos);
}
