package com.collegesquire.controller;

import com.collegesquire.model.Turma;
import com.collegesquire.repository.CursoRepository;
import com.collegesquire.repository.TurmaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/turmas")
public class TurmaAdminController extends CadastroController<Turma> {

    private final TurmaRepository turmaRepository;
    private final CursoRepository cursoRepository;

    public TurmaAdminController(TurmaRepository turmaRepository, CursoRepository cursoRepository) {
        this.turmaRepository = turmaRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    protected JpaRepository<Turma, Long> repositorio() {
        return turmaRepository;
    }

    @Override
    protected String prefixoView() {
        return "turma";
    }

    @Override
    protected String atributoLista() {
        return "turmas";
    }

    @Override
    protected String caminhoBase() {
        return "/turmas";
    }

    @Override
    protected String tituloPagina() {
        return "Turmas";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("turma", new Turma());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("tituloPagina", "Turmas");

        return "turma-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("turma", turmaRepository.findById(id).orElseThrow());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("tituloPagina", "Turmas");

        return "turma-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Turma turma, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        turmaRepository.save(turma);

        return "redirect:/turmas";
    }
}
