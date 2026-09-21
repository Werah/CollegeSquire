package com.collegesquire.controller;

import com.collegesquire.dto.AlunoDto;
import com.collegesquire.dto.DisciplinaDto;
import com.collegesquire.dto.DisciplinaProfessorDTO;
import com.collegesquire.dto.GradeDto;
import com.collegesquire.model.DiaSemana;
import com.collegesquire.model.TipoUsuario;
import com.collegesquire.service.AlunoService;
import com.collegesquire.service.DisciplinaService;
import com.collegesquire.service.GradeService;
import com.collegesquire.service.ProfessorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {

    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final DisciplinaService disciplinaService;
    private final GradeService gradeService;

    public HomeController(AlunoService alunoService, ProfessorService professorService,
                           DisciplinaService disciplinaService, GradeService gradeService) {
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.disciplinaService = disciplinaService;
        this.gradeService = gradeService;
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
        model.addAttribute("diasSemana", List.of(DiaSemana.values()));

        if (tipo == TipoUsuario.ALUNO) {
            montarPainelAluno(usuarioId, model);
        } else if (tipo == TipoUsuario.PROFESSOR) {
            montarPainelProfessor(usuarioId, model);
        }

        return "home";
    }

    private void montarPainelAluno(Long alunoId, Model model) {

        AlunoDto aluno = alunoService.buscarDto(alunoId);

        List<DisciplinaDto> disciplinas = aluno.turma() != null
                ? disciplinaService.listarDtoPorTurma(aluno.turma().id())
                : List.of();

        List<GradeDto> grade = aluno.turma() != null
                ? gradeService.buscarGradeDaTurma(aluno.turma().id())
                : List.of();

        int periodosPorDia = aluno.turma() != null && aluno.turma().periodosPorDia() != null
                ? aluno.turma().periodosPorDia()
                : 0;

        model.addAttribute("curso", aluno.curso());
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("gradeMapa", paraMapa(grade));
        model.addAttribute("linhasGrade", rangeDe1Ate(periodosPorDia));
    }

    private void montarPainelProfessor(Long professorId, Model model) {

        List<DisciplinaProfessorDTO> minhasDisciplinas = professorService.montarPainelDisciplinas(professorId);
        List<GradeDto> grade = gradeService.buscarGradeDoProfessor(professorId);

        int periodosPorDia = grade.stream().mapToInt(GradeDto::linha).max().orElse(0);

        model.addAttribute("minhasDisciplinas", minhasDisciplinas);
        model.addAttribute("gradeMapa", paraMapa(grade));
        model.addAttribute("linhasGrade", rangeDe1Ate(periodosPorDia));
    }

    private Map<String, GradeDto> paraMapa(List<GradeDto> grade) {
        return grade.stream()
                .collect(Collectors.toMap(g -> g.diaSemana().name() + "_" + g.linha(), g -> g, (a, b) -> a));
    }

    private List<Integer> rangeDe1Ate(int n) {
        return n > 0 ? IntStream.rangeClosed(1, n).boxed().toList() : List.of();
    }
}
