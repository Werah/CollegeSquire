package com.collegesquire.repository;

import com.collegesquire.model.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    List<Frequencia> findByAlunoIdOrderByDisciplinaIdAsc(Long alunoId);

    List<Frequencia> findByDisciplinaIdAndAlunoIdOrderByDataAscNumeroAulaAsc(Long disciplinaId, Long alunoId);

    List<Frequencia> findByDisciplinaIdOrderByDataAscNumeroAulaAsc(Long disciplinaId);

    Optional<Frequencia> findByDisciplinaIdAndAlunoIdAndDataAndNumeroAula(Long disciplinaId, Long alunoId,
                                                                           LocalDate data, Integer numeroAula);
}
