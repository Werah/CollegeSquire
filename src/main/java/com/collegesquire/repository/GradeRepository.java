package com.collegesquire.repository;

import com.collegesquire.model.DiaSemana;
import com.collegesquire.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByTurmaIdOrderByLinhaAsc(Long turmaId);

    List<Grade> findByDisciplinaId(Long disciplinaId);

    List<Grade> findByDisciplinaIdAndDiaSemana(Long disciplinaId, DiaSemana diaSemana);

    List<Grade> findByDisciplina_ProfessorId(Long professorId);

    Optional<Grade> findByTurmaIdAndDiaSemanaAndLinha(Long turmaId, DiaSemana diaSemana, Integer linha);

    void deleteByTurmaIdAndLinhaGreaterThan(Long turmaId, Integer linha);
}
