package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "frequencia", uniqueConstraints = @UniqueConstraint(
        columnNames = {"idaluno", "iddisciplina", "data", "numeroaula"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfrequencia")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "data")
    private LocalDate data;

    /**
     * Numero do periodo/aula dentro do dia (1-based), para disciplinas que
     * tem mais de uma aula seguida no mesmo dia da semana (ver Grade).
     */
    @Column(name = "numeroaula")
    private Integer numeroAula = 1;

    @Column(name = "presente")
    private Boolean presente;

    @ManyToOne
    @JoinColumn(name = "idaluno")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "iddisciplina")
    private Disciplina disciplina;
}
