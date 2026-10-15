package com.collegesquire.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Dado enviado fora das regras (ex.: CPF invalido). As telas ja barram isso
 * no navegador; esta excecao cobre requisicoes que contornem a validacao.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String message) {
        super(message);
    }
}
