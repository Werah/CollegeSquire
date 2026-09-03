package com.collegesquire.repository;

import com.collegesquire.model.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    List<Frequencia> findByAlunoIdOrderByDisciplinaIdAsc(Long alunoId);

    List<Frequencia> findByDisciplinaIdAndAlunoId(Long disciplinaId, Long alunoId);

    List<Frequencia> findByDisciplinaId(Long disciplinaId);
}
