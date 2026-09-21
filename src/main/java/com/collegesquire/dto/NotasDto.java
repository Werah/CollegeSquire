package com.collegesquire.dto;

public record NotasDto(
        Long id,
        String descricao,
        String tipo,
        Double peso,
        Double valor,
        AlunoDto aluno,
        DisciplinaDto disciplina
) {
}
