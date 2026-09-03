package com.collegesquire.config;

import com.collegesquire.model.Direcao;
import com.collegesquire.repository.DirecaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DirecaoSeeder implements CommandLineRunner {

    private final DirecaoRepository direcaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${collegesquire.coordenacao.email}")
    private String emailPadrao;

    @Value("${collegesquire.coordenacao.senha}")
    private String senhaPadrao;

    public DirecaoSeeder(DirecaoRepository direcaoRepository, PasswordEncoder passwordEncoder) {
        this.direcaoRepository = direcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (direcaoRepository.count() > 0) {
            return;
        }

        Direcao direcao = new Direcao();
        direcao.setNome("Coordenacao");
        direcao.setCargo("Coordenador Academico");
        direcao.setEmail(emailPadrao);
        direcao.setSenha(passwordEncoder.encode(senhaPadrao));
        direcaoRepository.save(direcao);
    }
}
