package com.collegesquire.repository;

import com.collegesquire.model.Direcao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirecaoRepository extends JpaRepository<Direcao, Long> {

    Optional<Direcao> findByEmail(String email);

    boolean existsByEmail(String email);
}
