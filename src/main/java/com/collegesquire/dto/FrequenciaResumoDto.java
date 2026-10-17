package com.collegesquire.dto;

import java.util.Locale;

/**
 * Presenca geral de um aluno em uma disciplina: quantas aulas teve
 * (total), em quantas esteve presente e as faltas.
 */
public record FrequenciaResumoDto(int presentes, int total) {

    public int faltas() {
        return total - presentes;
    }

    public double percentual() {
        return total == 0 ? 0.0 : (double) presentes / total * 100.0;
    }

    /** Ex.: "80%" ou "66,7%"; "-" quando ainda nao houve aula lancada. */
    public String percentualFormatado() {

        if (total == 0) {
            return "-";
        }

        double p = percentual();
        if (Math.abs(p - Math.rint(p)) < 0.05) {
            return (int) Math.rint(p) + "%";
        }
        return String.format(Locale.of("pt", "BR"), "%.1f%%", p);
    }

    /** Ex.: "4/5". */
    public String fracao() {
        return presentes + "/" + total;
    }
}
