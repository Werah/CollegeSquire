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
    public String novo(@RequestParam(required = false) Long cursoId, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", new Disciplina());
        adicionarListas(model, cursoId);
        model.addAttribute("voltarUrl", voltarUrl(cursoId));
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    /**
     * Pre-preenche um formulario de nova disciplina com nome/sigla/professor
     * de uma disciplina existente, deixando a turma em branco. Usado para
     * cadastrar rapidamente a mesma materia como uma aula separada em outra
     * turma (cada turma tem sua propria lista de alunos, notas e faltas).
     */
    @GetMapping("/duplicar/{id}")
    public String duplicar(@PathVariable Long id, @RequestParam(required = false) Long cursoId,
                            HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Disciplina origem = disciplinaService.buscarParaEdicao(id);

        Disciplina copia = new Disciplina();
        copia.setNome(origem.getNome());
        copia.setSigla(origem.getSigla());
        copia.setProfessor(origem.getProfessor());

        model.addAttribute("disciplina", copia);
        model.addAttribute("duplicandoDe", origem.getNome());
        adicionarListas(model, cursoId);
        model.addAttribute("voltarUrl", voltarUrl(cursoId));
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Disciplina disciplina = disciplinaService.buscarParaEdicao(id);
        Long cursoId = disciplina.getTurma() != null && disciplina.getTurma().getCurso() != null
                ? disciplina.getTurma().getCurso().getId()
                : null;

        model.addAttribute("disciplina", disciplina);
        adicionarListas(model, cursoId);
        model.addAttribute("voltarUrl", voltarUrl(cursoId));
        model.addAttribute("tituloPagina", "Disciplinas");

        return "disciplina-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Disciplina disciplina, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Disciplina salva = disciplinaService.salvar(disciplina);

        Long cursoId = salva.getTurma() != null && salva.getTurma().getCurso() != null
                ? salva.getTurma().getCurso().getId()
                : null;

        return "redirect:" + voltarUrl(cursoId);
    }

    private void adicionarListas(Model model, Long cursoId) {
        model.addAttribute("turmas", cursoId != null ? turmaRepository.findByCursoId(cursoId) : turmaRepository.findAll());
        model.addAttribute("professores", professorRepository.findAll());
    }

    private String voltarUrl(Long cursoId) {
        return cursoId != null ? "/cursos/perfil/" + cursoId : "/disciplinas";
    }
}
