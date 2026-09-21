package com.collegesquire.mapper;

import com.collegesquire.dto.LogSalaDto;
import com.collegesquire.model.LogSala;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfessorMapper.class, SalaMapper.class})
public interface LogSalaMapper {

    LogSalaDto toDto(LogSala entity);

    LogSala toEntity(LogSalaDto dto);

    List<LogSalaDto> toDtoList(List<LogSala> entities);

    List<LogSala> toEntityList(List<LogSalaDto> dtos);
}
