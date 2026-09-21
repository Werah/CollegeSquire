package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Uma celula da grade curricular semanal de uma turma: em qual dia da
 * semana e em qual linha (periodo/aula) uma disciplina acontece.
 */
@Entity
@Table(name = "grade", uniqueConstraints = @UniqueConstraint(columnNames = {"idturma", "diasemana", "linha"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgrade")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idturma")
    private Turma turma;

    @Enumerated(EnumType.STRING)
    @Column(name = "diasemana")
    private DiaSemana diaSemana;

    @Column(name = "linha")
    private Integer linha;

    @ManyToOne
    @JoinColumn(name = "iddisciplina")
    private Disciplina disciplina;
}
