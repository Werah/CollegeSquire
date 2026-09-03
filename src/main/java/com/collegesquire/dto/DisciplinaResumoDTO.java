package com.collegesquire.dto;

import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Notas;

import java.util.List;

public class DisciplinaResumoDTO {

    private final Disciplina disciplina;
    private final List<Notas> notas;
    private final Double media;
    private final int totalAulas;
    private final int faltas;
    private final double percentualPresenca;

    public DisciplinaResumoDTO(Disciplina disciplina, List<Notas> notas, Double media,
                                int totalAulas, int faltas, double percentualPresenca) {
        this.disciplina = disciplina;
        this.notas = notas;
        this.media = media;
        this.totalAulas = totalAulas;
        this.faltas = faltas;
        this.percentualPresenca = percentualPresenca;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public List<Notas> getNotas() {
        return notas;
    }

    public Double getMedia() {
        return media;
    }

    public int getTotalAulas() {
        return totalAulas;
    }

    public int getFaltas() {
        return faltas;
    }

    public double getPercentualPresenca() {
        return percentualPresenca;
    }
}
