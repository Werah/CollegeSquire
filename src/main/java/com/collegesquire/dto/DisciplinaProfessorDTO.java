package com.collegesquire.dto;

import com.collegesquire.model.Disciplina;

public class DisciplinaProfessorDTO {

    private final Disciplina disciplina;
    private final int totalAlunos;

    public DisciplinaProfessorDTO(Disciplina disciplina, int totalAlunos) {
        this.disciplina = disciplina;
        this.totalAlunos = totalAlunos;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public int getTotalAlunos() {
        return totalAlunos;
    }
}
