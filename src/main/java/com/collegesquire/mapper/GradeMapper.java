package com.collegesquire.mapper;

import com.collegesquire.dto.GradeDto;
import com.collegesquire.model.Grade;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DisciplinaMapper.class)
public interface GradeMapper {

    GradeDto toDto(Grade entity);

    List<GradeDto> toDtoList(List<Grade> entities);
}
