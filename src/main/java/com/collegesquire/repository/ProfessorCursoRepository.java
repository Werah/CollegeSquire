package com.collegesquire.repository;

import com.collegesquire.model.ProfessorCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorCursoRepository extends JpaRepository<ProfessorCurso, Long> {

    List<ProfessorCurso> findByProfessorId(Long professorId);
}
