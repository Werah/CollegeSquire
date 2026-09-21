package com.collegesquire.controller;

import com.collegesquire.model.DiaSemana;
import com.collegesquire.model.Turma;
import com.collegesquire.repository.CursoRepository;
import com.collegesquire.repository.TurmaRepository;
import com.collegesquire.service.DisciplinaService;
import com.collegesquire.service.GradeService;
import com.collegesquire.service.TurmaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.collegesquire.model.TipoUsuario.DIRECAO;

@Controller
@RequestMapping("/grades")
public class GradeAdminController {

    private final CursoRepository cursoRepository;
    private final TurmaRepository turmaRepository;
    private final TurmaService turmaService;
    private final DisciplinaService disciplinaService;
    private final GradeService gradeService;

    public GradeAdminController(CursoRepository cursoRepository, TurmaRepository turmaRepository,
                                 TurmaService turmaService, DisciplinaService disciplinaService,
                                 GradeService gradeService) {
        this.cursoRepository = cursoRepository;
        this.turmaRepository = turmaRepository;
        this.turmaService = turmaService;
        this.disciplinaService = disciplinaService;
        this.gradeService = gradeService;
    }

    @GetMapping
    public String grade(@RequestParam(required = false) Long cursoId,
                         @RequestParam(required = false) Long turmaId,
                         HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("tituloPagina", "Grade curricular");
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("turmaId", turmaId);

        if (cursoId != null) {
            model.addAttribute("turmas", turmaRepository.findByCursoId(cursoId));
        }

        if (cursoId != null && turmaId != null) {
            Turma turma = turmaService.buscarParaEdicao(turmaId);
            int periodosPorDia = gradeService.periodosPorDia(turma);

            model.addAttribute("turma", turma);
            model.addAttribute("periodosPorDia", periodosPorDia);
            model.addAttribute("linhas", rangeDe1Ate(periodosPorDia));
            model.addAttribute("diasSemana", List.of(DiaSemana.values()));
            model.addAttribute("disciplinasDaTurma", disciplinaService.listarPorTurma(turmaId));
            model.addAttribute("celulas", gradeService.buscarMapaCelulas(turmaId));
        }

        return "grade-curricular";
    }

    @PostMapping("/periodos")
    public String atualizarPeriodos(@RequestParam Long cursoId,
                                     @RequestParam Long turmaId,
                                     @RequestParam Integer periodos,
                                     HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        gradeService.definirPeriodosPorDia(turmaId, periodos);

        return "redirect:/grades?cursoId=" + cursoId + "&turmaId=" + turmaId;
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Long cursoId,
                          @RequestParam Long turmaId,
                          @RequestParam Map<String, String> parametros,
                          HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        Turma turma = turmaService.buscarParaEdicao(turmaId);
        gradeService.salvarGrade(turma, parametros);

        return "redirect:/grades?cursoId=" + cursoId + "&turmaId=" + turmaId;
    }

    private boolean autorizado(HttpSession session) {
        return session.getAttribute("tipoUsuario") == DIRECAO;
    }

    private List<Integer> rangeDe1Ate(int n) {
        return IntStream.rangeClosed(1, n).boxed().toList();
    }
}
