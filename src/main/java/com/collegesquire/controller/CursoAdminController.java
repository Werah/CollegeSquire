package com.collegesquire.controller;

import com.collegesquire.model.Curso;
import com.collegesquire.repository.CursoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cursos")
public class CursoAdminController extends CadastroController<Curso> {

    private final CursoRepository cursoRepository;

    public CursoAdminController(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    protected JpaRepository<Curso, Long> repositorio() {
        return cursoRepository;
    }

    @Override
    protected String prefixoView() {
        return "curso";
    }

    @Override
    protected String atributoLista() {
        return "cursos";
    }

    @Override
    protected String caminhoBase() {
        return "/cursos";
    }

    @Override
    protected String tituloPagina() {
        return "Cursos";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("curso", new Curso());
        model.addAttribute("tituloPagina", "Cursos");

        return "curso-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("curso", cursoRepository.findById(id).orElseThrow());
        model.addAttribute("tituloPagina", "Cursos");

        return "curso-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Curso curso, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        cursoRepository.save(curso);

        return "redirect:/cursos";
    }
}
