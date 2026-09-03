package com.collegesquire.controller;

import com.collegesquire.dto.DisciplinaProfessorDTO;
import com.collegesquire.dto.DisciplinaResumoDTO;
import com.collegesquire.model.*;
import com.collegesquire.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final NotasRepository notasRepository;
    private final FrequenciaRepository frequenciaRepository;

    public HomeController(AlunoRepository alunoRepository,
                           DisciplinaRepository disciplinaRepository,
                           NotasRepository notasRepository,
                           FrequenciaRepository frequenciaRepository) {
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.notasRepository = notasRepository;
        this.frequenciaRepository = frequenciaRepository;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        TipoUsuario tipo = (TipoUsuario) session.getAttribute("tipoUsuario");
        if (tipo == null) {
            return "redirect:/";
        }

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");

        model.addAttribute("tipo", tipo);
        model.addAttribute("nome", usuarioNome);

        if (tipo == TipoUsuario.ALUNO) {
            montarPainelAluno(usuarioId, model);
        } else if (tipo == TipoUsuario.PROFESSOR) {
            montarPainelProfessor(usuarioId, model);
        }

        return "home";
    }

    private void montarPainelAluno(Long alunoId, Model model) {

        Aluno aluno = alunoRepository.findById(alunoId).orElseThrow();
        Curso curso = aluno.getCurso();
        Turma turma = aluno.getTurma();

        List<Disciplina> disciplinas = turma != null
                ? disciplinaRepository.findByTurmaId(turma.getId())
                : List.of();

        List<Notas> todasNotas = notasRepository.findByAlunoIdOrderByDisciplinaIdAsc(alunoId);
        List<Frequencia> todasFrequencias = frequenciaRepository.findByAlunoIdOrderByDisciplinaIdAsc(alunoId);

        List<DisciplinaResumoDTO> resumoDisciplinas = disciplinas.stream()
                .map(disciplina -> {
                    List<Notas> notasDisciplina = todasNotas.stream()
                            .filter(n -> n.getDisciplina().getId().equals(disciplina.getId()))
                            .toList();

                    List<Frequencia> freqDisciplina = todasFrequencias.stream()
                            .filter(f -> f.getDisciplina().getId().equals(disciplina.getId()))
                            .toList();

                    Double media = calcularMedia(notasDisciplina);
                    int totalAulas = freqDisciplina.size();
                    int faltas = (int) freqDisciplina.stream()
                            .filter(f -> Boolean.FALSE.equals(f.getPresente()))
                            .count();
                    double percentual = totalAulas == 0
                            ? 100.0
                            : (double) (totalAulas - faltas) / totalAulas * 100.0;

                    return new DisciplinaResumoDTO(disciplina, notasDisciplina, media, totalAulas, faltas, percentual);
                })
                .toList();

        model.addAttribute("curso", curso);
        model.addAttribute("turmas", turma != null ? List.of(turma) : List.of());
        model.addAttribute("resumoDisciplinas", resumoDisciplinas);
    }

    private void montarPainelProfessor(Long professorId, Model model) {

        List<Disciplina> disciplinas = disciplinaRepository.findByProfessorId(professorId);

        List<DisciplinaProfessorDTO> minhasDisciplinas = disciplinas.stream()
                .map(disciplina -> {
                    int totalAlunos = disciplina.getTurma() != null
                            ? alunoRepository.findByTurmaId(disciplina.getTurma().getId()).size()
                            : 0;
                    return new DisciplinaProfessorDTO(disciplina, totalAlunos);
                })
                .toList();

        model.addAttribute("minhasDisciplinas", minhasDisciplinas);
    }

    private Double calcularMedia(List<Notas> notas) {

        if (notas.isEmpty()) {
            return null;
        }

        double somaPesos = notas.stream()
                .mapToDouble(n -> n.getPeso() != null ? n.getPeso() : 1.0)
                .sum();

        double somaPonderada = notas.stream()
                .mapToDouble(n -> (n.getValor() != null ? n.getValor() : 0.0)
                        * (n.getPeso() != null ? n.getPeso() : 1.0))
                .sum();

        return somaPesos == 0 ? null : somaPonderada / somaPesos;
    }
}
