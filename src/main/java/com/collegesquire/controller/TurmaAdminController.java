package com.collegesquire.controller;

import com.collegesquire.dto.TurmaDto;
import com.collegesquire.model.Curso;
import com.collegesquire.model.Turma;
import com.collegesquire.repository.CursoRepository;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.TurmaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/turmas")
public class TurmaAdminController extends CadastroController<TurmaDto> {

    private final TurmaService turmaService;
    private final CursoRepository cursoRepository;

    public TurmaAdminController(TurmaService turmaService, CursoRepository cursoRepository) {
        this.turmaService = turmaService;
        this.cursoRepository = cursoRepository;
    }

    @Override
    protected CrudService<TurmaDto> servico() {
        return turmaService;
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
    public String novo(@RequestParam(required = false) Long cursoId, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Turma turma = new Turma();
        if (cursoId != null) {
            Curso curso = cursoRepository.findById(cursoId).orElse(null);
            turma.setCurso(curso);
        }

        model.addAttribute("turma", turma);
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("voltarUrl", voltarUrl(cursoId));
        model.addAttribute("tituloPagina", "Turmas");

        return "turma-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Turma turma = turmaService.buscarParaEdicao(id);
        Long cursoId = turma.getCurso() != null ? turma.getCurso().getId() : null;

        model.addAttribute("turma", turma);
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("voltarUrl", voltarUrl(cursoId));
        model.addAttribute("tituloPagina", "Turmas");

        return "turma-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Turma turma, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Turma salva = turmaService.salvar(turma);

        return "redirect:" + voltarUrl(salva.getCurso() != null ? salva.getCurso().getId() : null);
    }

    private String voltarUrl(Long cursoId) {
        return cursoId != null ? "/cursos/perfil/" + cursoId : "/turmas";
    }
}
