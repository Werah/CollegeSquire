package com.collegesquire.repository;

import com.collegesquire.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByCursoId(Long cursoId);
}
