package com.collegesquire.controller;

import com.collegesquire.model.Aluno;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.TipoUsuario;
import com.collegesquire.service.AlunoService;
import com.collegesquire.service.DisciplinaService;
import com.collegesquire.service.FrequenciaService;
import com.collegesquire.service.GradeService;
import com.collegesquire.service.NotasService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    private final DisciplinaService disciplinaService;
    private final AlunoService alunoService;
    private final NotasService notasService;
    private final FrequenciaService frequenciaService;
    private final GradeService gradeService;

    public AlunoController(DisciplinaService disciplinaService,
                            AlunoService alunoService,
                            NotasService notasService,
                            FrequenciaService frequenciaService,
                            GradeService gradeService) {
        this.disciplinaService = disciplinaService;
        this.alunoService = alunoService;
        this.notasService = notasService;
        this.frequenciaService = frequenciaService;
        this.gradeService = gradeService;
    }

    @GetMapping("/disciplinas/{id}")
    public String disciplina(@PathVariable Long id, HttpSession session, Model model) {

        Long alunoId = (Long) session.getAttribute("usuarioId");
        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        var periodosPorDiaSemana = gradeService.periodosPorDiaSemana(id);

        model.addAttribute("disciplina", disciplinaService.paraDto(disciplina));
        model.addAttribute("notas", notasService.listarDtoPorDisciplinaEAluno(id, alunoId));
        model.addAttribute("media", notasService.calcularMediaPorDisciplinaEAluno(id, alunoId));
        model.addAttribute("frequenciasPorDia", frequenciaService.mapaPorDiaDoAluno(id, alunoId));
        model.addAttribute("resumoPresenca", frequenciaService.resumoDoAluno(id, alunoId));
        model.addAttribute("diasPermitidos", List.copyOf(periodosPorDiaSemana.keySet()));

        return "aluno-disciplina";
    }

    private Disciplina disciplinaAutorizada(Long disciplinaId, HttpSession session) {

        TipoUsuario tipo = (TipoUsuario) session.getAttribute("tipoUsuario");
        Long alunoId = (Long) session.getAttribute("usuarioId");

        if (tipo != TipoUsuario.ALUNO) {
            return null;
        }

        Aluno aluno = alunoService.buscarEntidade(alunoId).orElse(null);
        Disciplina disciplina = disciplinaService.buscarEntidade(disciplinaId).orElse(null);

        if (aluno == null || aluno.getTurma() == null || disciplina == null || disciplina.getTurma() == null) {
            return null;
        }

        if (!aluno.getTurma().getId().equals(disciplina.getTurma().getId())) {
            return null;
        }

        return disciplina;
    }
}
