package com.collegesquire.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro de uso de uma sala por um professor. Pensado para ser
 * preenchido futuramente de forma automatica pela leitura do RFID do
 * professor (ver {@link Professor#getRfid()}); por enquanto nao existe
 * nenhuma tela no site que crie estes registros.
 */
@Entity
@Table(name = "logsala")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LogSala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlogsala")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idprofessor")
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "idsala")
    private Sala sala;

    @Column(name = "datahora")
    private LocalDateTime dataHora;
}
