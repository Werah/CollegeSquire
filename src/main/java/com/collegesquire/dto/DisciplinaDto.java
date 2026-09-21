package com.collegesquire.dto;

public record DisciplinaDto(
        Long id,
        String nome,
        String sigla,
        TurmaDto turma,
        ProfessorDto professor
) {
}
