package com.collegesquire.mapper;

import com.collegesquire.dto.AlunoDto;
import com.collegesquire.model.Aluno;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CursoMapper.class, TurmaMapper.class})
public interface AlunoMapper {

    AlunoDto toDto(Aluno entity);

    Aluno toEntity(AlunoDto dto);

    List<AlunoDto> toDtoList(List<Aluno> entities);

    List<Aluno> toEntityList(List<AlunoDto> dtos);
}
