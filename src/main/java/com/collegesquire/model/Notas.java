package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnotas")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "valor")
    private Double valor;

    @ManyToOne
    @JoinColumn(name = "idaluno")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "iddisciplina")
    private Disciplina disciplina;
}
