package com.collegesquire.dto;

public record TurmaDto(
        Long id,
        Integer numero,
        String periodo,
        Integer periodosPorDia,
        CursoDto curso
) {
}
