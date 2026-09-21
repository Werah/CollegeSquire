package com.collegesquire.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Tratamento centralizado de excecoes para os controllers MVC. Como as
 * telas sao renderizadas em Thymeleaf (nao e uma API JSON), aqui a
 * excecao vira um redirecionamento em vez de um corpo de resposta.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        return "redirect:/home";
    }
}
