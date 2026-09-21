package com.collegesquire.dto;

public record ProfessorDto(
        Long id,
        String registro,
        String nome,
        String cpf,
        String endereco,
        String telefone,
        String email
) {
}
