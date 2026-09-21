package com.collegesquire.dto;

public record AlunoDto(
        Long id,
        String cpf,
        String nome,
        String endereco,
        String telefone,
        String email,
        String matricula,
        CursoDto curso,
        TurmaDto turma
) {
}
