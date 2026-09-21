package com.collegesquire.controller;

import com.collegesquire.model.TipoUsuario;
import com.collegesquire.service.CrudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Base generica para as telas administrativas de cadastro (listar e excluir).
 * As telas de novo/editar/salvar ficam em cada controller concreto, pois os
 * campos e relacionamentos variam por entidade. A listagem trabalha sempre
 * com DTOs (nunca a entidade JPA), delegando as regras de negocio para o
 * Service dedicado de cada entidade.
 */
public abstract class CadastroController<D> {

    protected abstract CrudService<D> servico();

    protected abstract String prefixoView();

    protected abstract String atributoLista();

    protected abstract String caminhoBase();

    protected abstract String tituloPagina();

    @GetMapping
    public String listar(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute(atributoLista(), servico().listarTodos());
        model.addAttribute("caminhoBase", caminhoBase());
        model.addAttribute("tituloPagina", tituloPagina());

        return prefixoView() + "-lista";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        servico().deletar(id);

        return "redirect:" + caminhoBase();
    }

    protected boolean autorizado(HttpSession session) {
        return session.getAttribute("tipoUsuario") == TipoUsuario.DIRECAO;
    }
}
