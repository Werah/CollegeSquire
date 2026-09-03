package com.collegesquire.controller;

import com.collegesquire.model.Disciplina;
import com.collegesquire.repository.DisciplinaRepository;
import com.collegesquire.repository.ProfessorRepository;
import com.collegesquire.repository.TurmaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaAdminController extends CadastroController<Disciplina> {

    private final DisciplinaRepository disciplinaRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public DisciplinaAdminController(DisciplinaRepository disciplinaRepository, TurmaRepository turmaRepository,
                                      ProfessorRepository professorRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    protected JpaRepository<Disciplina, Long> repositorio() {
        return disciplinaRepository;
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

        model.addAttribute("disciplina", disciplinaRepository.findById(id).orElseThrow());
        adicionarListas(model);
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Disciplina disciplina, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        disciplinaRepository.save(disciplina);

        return "redirect:/disciplinas";
    }

    private void adicionarListas(Model model) {
        model.addAttribute("turmas", turmaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
    }
}
