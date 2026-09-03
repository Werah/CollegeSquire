package com.collegesquire.controller;

import com.collegesquire.model.*;
import com.collegesquire.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final DisciplinaRepository disciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final NotasRepository notasRepository;
    private final FrequenciaRepository frequenciaRepository;

    public ProfessorController(DisciplinaRepository disciplinaRepository,
                                AlunoRepository alunoRepository,
                                NotasRepository notasRepository,
                                FrequenciaRepository frequenciaRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.alunoRepository = alunoRepository;
        this.notasRepository = notasRepository;
        this.frequenciaRepository = frequenciaRepository;
    }

    @GetMapping("/disciplinas/{id}")
    public String disciplina(@PathVariable Long id, HttpSession session, Model model) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplina);
        model.addAttribute("alunos", alunosDaTurma(disciplina));
        model.addAttribute("notas", notasRepository.findByDisciplinaId(id));
        model.addAttribute("frequencias", frequenciaRepository.findByDisciplinaId(id));

        return "professor-disciplina";
    }

    @PostMapping("/disciplinas/{id}/notas")
    public String lancarNota(@PathVariable Long id,
                              @RequestParam Long alunoId,
                              @RequestParam String descricao,
                              @RequestParam String tipo,
                              @RequestParam Double peso,
                              @RequestParam Double valor,
                              HttpSession session) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null || !alunoNaTurma(alunoId, disciplina)) {
            return "redirect:/home";
        }

        Notas notas = new Notas();
        notas.setAluno(alunoRepository.findById(alunoId).orElseThrow());
        notas.setDisciplina(disciplina);
        notas.setDescricao(descricao);
        notas.setTipo(tipo);
        notas.setPeso(peso);
        notas.setValor(valor);
        notasRepository.save(notas);

        return "redirect:/professor/disciplinas/" + id;
    }

    @PostMapping("/disciplinas/{id}/frequencia")
    public String lancarFrequencia(@PathVariable Long id,
                                    @RequestParam Long alunoId,
                                    @RequestParam(required = false) String data,
                                    @RequestParam(defaultValue = "false") boolean presente,
                                    HttpSession session) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null || !alunoNaTurma(alunoId, disciplina)) {
            return "redirect:/home";
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setAluno(alunoRepository.findById(alunoId).orElseThrow());
        frequencia.setDisciplina(disciplina);
        frequencia.setData(data != null && !data.isBlank() ? LocalDate.parse(data) : LocalDate.now());
        frequencia.setPresente(presente);
        frequenciaRepository.save(frequencia);

        return "redirect:/professor/disciplinas/" + id;
    }

    @GetMapping("/alunos/{id}")
    public String perfilAluno(@PathVariable Long id,
                               @RequestParam Long disciplinaId,
                               HttpSession session,
                               Model model) {

        Disciplina disciplina = disciplinaAutorizada(disciplinaId, session);
        if (disciplina == null || !alunoNaTurma(id, disciplina)) {
            return "redirect:/home";
        }

        Aluno aluno = alunoRepository.findById(id).orElseThrow();

        model.addAttribute("aluno", aluno);
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("notas", notasRepository.findByDisciplinaIdAndAlunoId(disciplinaId, id));
        model.addAttribute("frequencias", frequenciaRepository.findByDisciplinaIdAndAlunoId(disciplinaId, id));

        return "professor-aluno";
    }

    private Disciplina disciplinaAutorizada(Long disciplinaId, HttpSession session) {

        TipoUsuario tipo = (TipoUsuario) session.getAttribute("tipoUsuario");
        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (tipo != TipoUsuario.PROFESSOR) {
            return null;
        }

        Disciplina disciplina = disciplinaRepository.findById(disciplinaId).orElse(null);
        if (disciplina == null || disciplina.getProfessor() == null
                || !disciplina.getProfessor().getId().equals(usuarioId)) {
            return null;
        }

        return disciplina;
    }

    private List<Aluno> alunosDaTurma(Disciplina disciplina) {
        return disciplina.getTurma() != null
                ? alunoRepository.findByTurmaId(disciplina.getTurma().getId())
                : List.of();
    }

    private boolean alunoNaTurma(Long alunoId, Disciplina disciplina) {
        return alunosDaTurma(disciplina).stream()
                .anyMatch(aluno -> aluno.getId().equals(alunoId));
    }
}
