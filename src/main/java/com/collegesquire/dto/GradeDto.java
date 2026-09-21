package com.collegesquire.dto;

import com.collegesquire.model.DiaSemana;

public record GradeDto(
        Long id,
        DiaSemana diaSemana,
        Integer linha,
        DisciplinaDto disciplina
) {
}
