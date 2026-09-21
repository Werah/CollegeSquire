package com.collegesquire.dto;

import java.time.LocalDateTime;

public record LogSalaDto(
        Long id,
        ProfessorDto professor,
        SalaDto sala,
        LocalDateTime dataHora
) {
}
