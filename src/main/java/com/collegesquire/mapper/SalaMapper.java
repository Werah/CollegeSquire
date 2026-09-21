package com.collegesquire.mapper;

import com.collegesquire.dto.SalaDto;
import com.collegesquire.model.Sala;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalaMapper {

    SalaDto toDto(Sala entity);

    Sala toEntity(SalaDto dto);

    List<SalaDto> toDtoList(List<Sala> entities);

    List<Sala> toEntityList(List<SalaDto> dtos);
}
