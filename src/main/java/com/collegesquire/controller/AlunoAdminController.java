package com.collegesquire.controller;

import com.collegesquire.model.Aluno;
import com.collegesquire.repository.AlunoRepository;
import com.collegesquire.repository.CursoRepository;
import com.collegesquire.repository.TurmaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alunos")
public class AlunoAdminController extends CadastroController<Aluno> {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoAdminController(AlunoRepository alunoRepository, CursoRepository cursoRepository,
                                 TurmaRepository turmaRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected JpaRepository<Aluno, Long> repositorio() {
        return alunoRepository;
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

        Aluno aluno = alunoRepository.findById(id).orElseThrow();
        aluno.setSenha(null);

        model.addAttribute("aluno", aluno);
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

        if (alunoForm.getId() != null) {
            Aluno existente = alunoRepository.findById(alunoForm.getId()).orElseThrow();
            existente.setNome(alunoForm.getNome());
            existente.setCpf(alunoForm.getCpf());
            existente.setEndereco(alunoForm.getEndereco());
            existente.setTelefone(alunoForm.getTelefone());
            existente.setEmail(alunoForm.getEmail());
            existente.setMatricula(alunoForm.getMatricula());
            existente.setCurso(alunoForm.getCurso());
            existente.setTurma(alunoForm.getTurma());
            if (alunoForm.getSenha() != null && !alunoForm.getSenha().isBlank()) {
                existente.setSenha(passwordEncoder.encode(alunoForm.getSenha()));
            }
            alunoRepository.save(existente);
        } else {
            alunoForm.setSenha(passwordEncoder.encode(alunoForm.getSenha()));
            alunoRepository.save(alunoForm);
        }

        return "redirect:/alunos";
    }
}
