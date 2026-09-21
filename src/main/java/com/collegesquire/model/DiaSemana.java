package com.collegesquire.model;

import java.time.LocalDate;
import java.time.DayOfWeek;

public enum DiaSemana {

    DOMINGO("Dom"),
    SEGUNDA("Seg"),
    TERCA("Ter"),
    QUARTA("Qua"),
    QUINTA("Qui"),
    SEXTA("Sex"),
    SABADO("Sab");

    private final String rotulo;

    DiaSemana(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static DiaSemana deLocalDate(LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        return switch (dia) {
            case SUNDAY -> DOMINGO;
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
        };
    }

    /**
     * Indice no estilo JS Date.getDay() (0 = domingo .. 6 = sabado), usado
     * para casar com o calendario no front-end.
     */
    public int getIndiceJs() {
        return ordinal();
    }
}
