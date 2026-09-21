package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sala")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsala")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "predio")
    private Integer predio;

    @Column(name = "descricao")
    private String descricao;
}
