package com.collegesquire.service;

import java.util.List;

/**
 * Operacoes minimas que toda entidade de cadastro precisa expor para a
 * tela generica de listagem/exclusao (ver CadastroController).
 */
public interface CrudService<D> {

    List<D> listarTodos();

    void deletar(Long id);
}
