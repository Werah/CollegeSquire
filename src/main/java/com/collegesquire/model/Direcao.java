package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Direcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddirecao")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "registro")
    private String registro;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "senha")
    private String senha;
}
