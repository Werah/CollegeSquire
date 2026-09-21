package com.collegesquire.controller;

import com.collegesquire.dto.CursoDto;
import com.collegesquire.model.Curso;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.CursoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cursos")
public class CursoAdminController extends CadastroController<CursoDto> {

    private final CursoService cursoService;

    public CursoAdminController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Override
    protected CrudService<CursoDto> servico() {
        return cursoService;
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

        model.addAttribute("curso", cursoService.buscarParaEdicao(id));
        model.addAttribute("tituloPagina", "Cursos");

        return "curso-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Curso curso, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        cursoService.salvar(curso);

        return "redirect:/cursos";
    }
}
