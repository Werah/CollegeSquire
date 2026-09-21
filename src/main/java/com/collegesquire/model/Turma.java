package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "turma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idturma")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "periodo")
    private String periodo;

    @Column(name = "periodospordia")
    private Integer periodosPorDia = 4;

    @ManyToOne
    @JoinColumn(name = "idcurso")
    private Curso curso;
}
