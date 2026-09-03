package com.collegesquire.controller;

import com.collegesquire.model.TipoUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Base generica para as telas administrativas de cadastro (listar e excluir).
 * As telas de novo/editar/salvar ficam em cada controller concreto, pois os
 * campos e relacionamentos variam por entidade.
 */
public abstract class CadastroController<T> {

    protected abstract JpaRepository<T, Long> repositorio();

    protected abstract String prefixoView();

    protected abstract String atributoLista();

    protected abstract String caminhoBase();

    protected abstract String tituloPagina();

    @GetMapping
    public String listar(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute(atributoLista(), repositorio().findAll());
        model.addAttribute("caminhoBase", caminhoBase());
        model.addAttribute("tituloPagina", tituloPagina());

        return prefixoView() + "-lista";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        repositorio().deleteById(id);

        return "redirect:" + caminhoBase();
    }

    protected boolean autorizado(HttpSession session) {
        return session.getAttribute("tipoUsuario") == TipoUsuario.DIRECAO;
    }
}
