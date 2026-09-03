package com.collegesquire.repository;

import com.collegesquire.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMatricula(String matricula);

    List<Aluno> findByTurmaId(Long turmaId);
}
