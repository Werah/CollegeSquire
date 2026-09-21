package com.collegesquire.controller;

import com.collegesquire.model.Aluno;
import com.collegesquire.model.DiaSemana;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final DisciplinaService disciplinaService;
    private final AlunoService alunoService;
    private final NotasService notasService;
    private final FrequenciaService frequenciaService;
    private final GradeService gradeService;

    public ProfessorController(DisciplinaService disciplinaService,
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

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplinaService.paraDto(disciplina));
        model.addAttribute("alunos", alunosDaTurma(disciplina));
        model.addAttribute("notas", notasService.listarDtoPorDisciplina(id));
        model.addAttribute("frequenciasPorDia", frequenciaService.mapaPorDiaETurma(id));
        model.addAttribute("diasPermitidos", List.copyOf(gradeService.periodosPorDiaSemana(id).keySet()));

        return "professor-disciplina";
    }

    @GetMapping("/disciplinas/{id}/notas")
    public String telaNotas(@PathVariable Long id, HttpSession session, Model model) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplinaService.paraDto(disciplina));
        model.addAttribute("alunos", alunosDaTurma(disciplina));

        return "professor-disciplina-notas";
    }

    @PostMapping("/disciplinas/{id}/notas")
    public String lancarNotas(@PathVariable Long id,
                               @RequestParam String descricao,
                               @RequestParam String tipo,
                               @RequestParam Double peso,
                               @RequestParam List<Long> alunoIds,
                               @RequestParam List<String> valores,
                               HttpSession session) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        notasService.lancarNotas(disciplina, alunosDaTurma(disciplina), descricao, tipo, peso, alunoIds, valores);

        return "redirect:/professor/disciplinas/" + id;
    }

    @GetMapping("/disciplinas/{id}/frequencia")
    public String telaFrequencia(@PathVariable Long id, HttpSession session, Model model) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        Map<Integer, Integer> periodosPorDiaSemana = gradeService.periodosPorDiaSemana(id);
        int maxPeriodos = periodosPorDiaSemana.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        model.addAttribute("disciplina", disciplinaService.paraDto(disciplina));
        model.addAttribute("alunos", alunosDaTurma(disciplina));
        model.addAttribute("diasPermitidos", List.copyOf(periodosPorDiaSemana.keySet()));
        model.addAttribute("periodosPorDiaSemana", periodosPorDiaSemana);
        model.addAttribute("maxPeriodos", maxPeriodos);
        model.addAttribute("periodos", maxPeriodos > 0 ? rangeDe1Ate(maxPeriodos) : List.of());

        return "professor-disciplina-frequencia";
    }

    @PostMapping("/disciplinas/{id}/frequencia")
    public String lancarFrequencia(@PathVariable Long id,
                                    @RequestParam String data,
                                    @RequestParam(required = false) List<String> presentes,
                                    HttpSession session) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        LocalDate dataAula = LocalDate.parse(data);
        int totalPeriodos = gradeService.contarPeriodos(id, DiaSemana.deLocalDate(dataAula));

        if (totalPeriodos > 0) {
            frequenciaService.lancarFrequencia(disciplina, alunosDaTurma(disciplina), dataAula, totalPeriodos, presentes);
        }

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

        model.addAttribute("aluno", alunoService.buscarDto(id));
        model.addAttribute("disciplina", disciplinaService.paraDto(disciplina));
        model.addAttribute("notas", notasService.listarDtoPorDisciplinaEAluno(disciplinaId, id));
        model.addAttribute("frequenciasPorDia", frequenciaService.mapaPorDiaDoAluno(disciplinaId, id));
        model.addAttribute("diasPermitidos", List.copyOf(gradeService.periodosPorDiaSemana(disciplinaId).keySet()));

        return "professor-aluno";
    }

    private Disciplina disciplinaAutorizada(Long disciplinaId, HttpSession session) {

        TipoUsuario tipo = (TipoUsuario) session.getAttribute("tipoUsuario");
        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (tipo != TipoUsuario.PROFESSOR) {
            return null;
        }

        Disciplina disciplina = disciplinaService.buscarEntidade(disciplinaId).orElse(null);
        if (disciplina == null || disciplina.getProfessor() == null
                || !disciplina.getProfessor().getId().equals(usuarioId)) {
            return null;
        }

        return disciplina;
    }

    private List<Aluno> alunosDaTurma(Disciplina disciplina) {
        return disciplina.getTurma() != null
                ? alunoService.listarPorTurma(disciplina.getTurma().getId())
                : List.of();
    }

    private boolean alunoNaTurma(Long alunoId, Disciplina disciplina) {
        return alunosDaTurma(disciplina).stream()
                .anyMatch(aluno -> aluno.getId().equals(alunoId));
    }

    private List<Integer> rangeDe1Ate(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n).boxed().toList();
    }
}
