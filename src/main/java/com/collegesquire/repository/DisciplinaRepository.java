package com.collegesquire.repository;

import com.collegesquire.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    List<Disciplina> findByTurmaId(Long turmaId);

    List<Disciplina> findByProfessorId(Long professorId);

    List<Disciplina> findByTurma_CursoId(Long cursoId);
}
