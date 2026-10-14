package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "disciplina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddisciplina")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "sigla")
    private String sigla;

    /**
     * Cada linha de Disciplina e uma aula/oferta especifica de UMA turma.
     * Se a mesma materia e dada em duas turmas, sao duas linhas de
     * Disciplina diferentes (cada uma com sua propria lista de alunos,
     * notas e frequencia) - ver DisciplinaAdminController#duplicar para
     * facilitar a criacao da segunda a partir da primeira.
     */
    @ManyToOne
    @JoinColumn(name = "idturma")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "idprofessor")
    private Professor professor;
}
