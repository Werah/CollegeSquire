package com.collegesquire.controller;

import com.collegesquire.dto.DisciplinaDto;
import com.collegesquire.model.Disciplina;
import com.collegesquire.repository.ProfessorRepository;
import com.collegesquire.repository.TurmaRepository;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.DisciplinaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaAdminController extends CadastroController<DisciplinaDto> {

    private final DisciplinaService disciplinaService;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public DisciplinaAdminController(DisciplinaService disciplinaService, TurmaRepository turmaRepository,
                                      ProfessorRepository professorRepository) {
        this.disciplinaService = disciplinaService;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    protected CrudService<DisciplinaDto> servico() {
        return disciplinaService;
    }

    @Override
    protected String prefixoView() {
        return "disciplina";
    }

    @Override
    protected String atributoLista() {
        return "disciplinas";
    }

    @Override
    protected String caminhoBase() {
        return "/disciplinas";
    }

    @Override
    protected String tituloPagina() {
        return "Disciplinas";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", new Disciplina());
        adicionarListas(model);
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplinaService.buscarParaEdicao(id));
        adicionarListas(model);
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Disciplina disciplina, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        disciplinaService.salvar(disciplina);

        return "redirect:/disciplinas";
    }

    private void adicionarListas(Model model) {
        model.addAttribute("turmas", turmaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
    }
}
