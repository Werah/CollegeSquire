package com.collegesquire.controller;

import com.collegesquire.dto.AlunoDto;
import com.collegesquire.model.Aluno;
import com.collegesquire.repository.CursoRepository;
import com.collegesquire.repository.TurmaRepository;
import com.collegesquire.service.AlunoService;
import com.collegesquire.service.CrudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alunos")
public class AlunoAdminController extends CadastroController<AlunoDto> {

    private final AlunoService alunoService;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;

    public AlunoAdminController(AlunoService alunoService, CursoRepository cursoRepository,
                                 TurmaRepository turmaRepository) {
        this.alunoService = alunoService;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
    }

    @Override
    protected CrudService<AlunoDto> servico() {
        return alunoService;
    }

    @Override
    protected String prefixoView() {
        return "aluno";
    }

    @Override
    protected String atributoLista() {
        return "alunos";
    }

    @Override
    protected String caminhoBase() {
        return "/alunos";
    }

    @Override
    protected String tituloPagina() {
        return "Alunos";
    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("aluno", alunoService.buscarDto(id));
        model.addAttribute("resumoDisciplinas", alunoService.montarResumoDisciplinas(id));
        model.addAttribute("tituloPagina", "Alunos");

        return "aluno-perfil";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("aluno", new Aluno());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("turmas", turmaRepository.findAll());
        model.addAttribute("tituloPagina", "Alunos");

        return "aluno-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("aluno", alunoService.buscarParaEdicao(id));
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("turmas", turmaRepository.findAll());
        model.addAttribute("tituloPagina", "Alunos");

        return "aluno-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Aluno alunoForm, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        alunoService.salvar(alunoForm);

        return "redirect:/alunos";
    }
}
