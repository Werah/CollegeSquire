package com.collegesquire.repository;

import com.collegesquire.model.LogSala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogSalaRepository extends JpaRepository<LogSala, Long> {

    List<LogSala> findBySalaIdOrderByDataHoraDesc(Long salaId);
}
