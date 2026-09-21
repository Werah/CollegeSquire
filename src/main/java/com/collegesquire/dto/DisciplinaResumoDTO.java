package com.collegesquire.dto;

import java.util.List;

public record DisciplinaResumoDTO(
        DisciplinaDto disciplina,
        List<NotasDto> notas,
        Double media,
        int totalAulas,
        int faltas,
        double percentualPresenca
) {
}
