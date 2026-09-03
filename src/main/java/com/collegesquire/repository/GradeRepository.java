package com.collegesquire.repository;

import com.collegesquire.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByTurmaId(Long turmaId);
}
