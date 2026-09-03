package com.collegesquire.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgrade")
    private Long id;

    @Column(name = "diasemana")
    private String diaSemana;

    @Column(name = "horainicio")
    private LocalTime horaInicio;

    @Column(name = "horafim")
    private LocalTime horaFim;

    @ManyToOne
    @JoinColumn(name = "idturma")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "idsala")
    private Sala sala;

    public Grade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}
