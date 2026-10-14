package com.collegesquire.controller;

import com.collegesquire.dto.CursoDto;
import com.collegesquire.model.Curso;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.CursoService;
import com.collegesquire.service.DisciplinaService;
import com.collegesquire.service.TurmaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cursos")
public class CursoAdminController extends CadastroController<CursoDto> {

    private final CursoService cursoService;
    private final TurmaService turmaService;
    private final DisciplinaService disciplinaService;

    public CursoAdminController(CursoService cursoService, TurmaService turmaService,
                                 DisciplinaService disciplinaService) {
        this.cursoService = cursoService;
        this.turmaService = turmaService;
        this.disciplinaService = disciplinaService;
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

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("curso", cursoService.buscarDto(id));
        model.addAttribute("turmas", turmaService.listarPorCurso(id));
        model.addAttribute("disciplinas", disciplinaService.listarPorCurso(id));
        model.addAttribute("tituloPagina", "Cursos");

        return "curso-perfil";
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

        Curso salvo = cursoService.salvar(curso);

        return "redirect:/cursos/perfil/" + salvo.getId();
    }
}
