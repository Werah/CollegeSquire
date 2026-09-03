package com.collegesquire.controller;

import com.collegesquire.model.Sala;
import com.collegesquire.repository.SalaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/salas")
public class SalaAdminController extends CadastroController<Sala> {

    private final SalaRepository salaRepository;

    public SalaAdminController(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Override
    protected JpaRepository<Sala, Long> repositorio() {
        return salaRepository;
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

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("sala", new Sala());
        model.addAttribute("tituloPagina", "Salas");

        return "sala-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("sala", salaRepository.findById(id).orElseThrow());
        model.addAttribute("tituloPagina", "Salas");

        return "sala-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Sala sala, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        salaRepository.save(sala);

        return "redirect:/salas";
    }
}
