package com.collegesquire.controller;

import com.collegesquire.dto.SalaDto;
import com.collegesquire.model.Sala;
import com.collegesquire.model.TipoUsuario;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.SalaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/salas")
public class SalaAdminController extends CadastroController<SalaDto> {

    private final SalaService salaService;

    public SalaAdminController(SalaService salaService) {
        this.salaService = salaService;
    }

    @Override
    protected CrudService<SalaDto> servico() {
        return salaService;
    }

    @Override
    protected String prefixoView() {
        return "sala";
    }

    @Override
    protected String atributoLista() {
        return "salas";
    }

    @Override
    protected String caminhoBase() {
        return "/salas";
    }

    @Override
    protected String tituloPagina() {
        return "Salas";
    }

    @Override
    @GetMapping
    public String listar(HttpSession session, Model model) {

        if (!autorizadoVisualizar(session)) {
            return "redirect:/home";
        }

        model.addAttribute("salas", salaService.listarTodos());
        model.addAttribute("tituloPagina", "Salas");
        model.addAttribute("tipo", session.getAttribute("tipoUsuario"));

        if (!model.containsAttribute("sala")) {
            model.addAttribute("sala", new Sala());
        }
        if (!model.containsAttribute("logs")) {
            model.addAttribute("logs", List.of());
        }

        return "sala-lista";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        return "redirect:/salas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizadoVisualizar(session)) {
            return "redirect:/home";
        }

        model.addAttribute("sala", salaService.buscarParaEdicao(id));
        model.addAttribute("logs", salaService.buscarLogsPorSala(id));
        model.addAttribute("salas", salaService.listarTodos());
        model.addAttribute("tituloPagina", "Salas");
        model.addAttribute("tipo", session.getAttribute("tipoUsuario"));

        return "sala-lista";
    }

    @PostMapping
    public String salvar(@ModelAttribute Sala sala, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Sala salva = salaService.salvar(sala);

        return "redirect:/salas/editar/" + salva.getId();
    }

    private boolean autorizadoVisualizar(HttpSession session) {
        TipoUsuario tipo = (TipoUsuario) session.getAttribute("tipoUsuario");
        return tipo == TipoUsuario.DIRECAO || tipo == TipoUsuario.PROFESSOR;
    }
}
