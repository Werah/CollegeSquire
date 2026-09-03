package com.collegesquire.repository;

import com.collegesquire.model.Notas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotasRepository extends JpaRepository<Notas, Long> {

    List<Notas> findByAlunoIdOrderByDisciplinaIdAsc(Long alunoId);

    List<Notas> findByDisciplinaIdAndAlunoId(Long disciplinaId, Long alunoId);

    List<Notas> findByDisciplinaId(Long disciplinaId);
}
