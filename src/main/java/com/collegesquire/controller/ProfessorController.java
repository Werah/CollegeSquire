package com.collegesquire.controller;

import com.collegesquire.model.*;
import com.collegesquire.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("frequenciasPorDia", frequenciasPorDia(id));

        return "professor-disciplina";
    }

    @GetMapping("/disciplinas/{id}/notas")
    public String telaNotas(@PathVariable Long id, HttpSession session, Model model) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplina);
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

        for (int i = 0; i < alunoIds.size(); i++) {
            String valorTexto = valores.get(i);
            if (valorTexto == null || valorTexto.isBlank()) {
                continue;
            }

            Long alunoId = alunoIds.get(i);
            if (!alunoNaTurma(alunoId, disciplina)) {
                continue;
            }

            Notas notas = new Notas();
            notas.setAluno(alunoRepository.findById(alunoId).orElseThrow());
            notas.setDisciplina(disciplina);
            notas.setDescricao(descricao);
            notas.setTipo(tipo);
            notas.setPeso(peso);
            notas.setValor(Double.valueOf(valorTexto.replace(",", ".")));
            notasRepository.save(notas);
        }

        return "redirect:/professor/disciplinas/" + id;
    }

    @GetMapping("/disciplinas/{id}/frequencia")
    public String telaFrequencia(@PathVariable Long id, HttpSession session, Model model) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        model.addAttribute("disciplina", disciplina);
        model.addAttribute("alunos", alunosDaTurma(disciplina));

        return "professor-disciplina-frequencia";
    }

    @PostMapping("/disciplinas/{id}/frequencia")
    public String lancarFrequencia(@PathVariable Long id,
                                    @RequestParam String data,
                                    @RequestParam(required = false) List<Long> presentes,
                                    HttpSession session) {

        Disciplina disciplina = disciplinaAutorizada(id, session);
        if (disciplina == null) {
            return "redirect:/home";
        }

        LocalDate dataAula = LocalDate.parse(data);
        List<Long> idsPresentes = presentes != null ? presentes : List.of();

        for (Aluno aluno : alunosDaTurma(disciplina)) {
            Frequencia frequencia = frequenciaRepository
                    .findByDisciplinaIdAndAlunoIdAndData(id, aluno.getId(), dataAula)
                    .orElseGet(Frequencia::new);

            frequencia.setAluno(aluno);
            frequencia.setDisciplina(disciplina);
            frequencia.setData(dataAula);
            frequencia.setPresente(idsPresentes.contains(aluno.getId()));
            frequenciaRepository.save(frequencia);
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

        Aluno aluno = alunoRepository.findById(id).orElseThrow();

        model.addAttribute("aluno", aluno);
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("notas", notasRepository.findByDisciplinaIdAndAlunoId(disciplinaId, id));
        model.addAttribute("frequenciasPorDia", frequenciasPorDiaDoAluno(disciplinaId, id));

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

    /**
     * Mapa "data (yyyy-MM-dd)" -> "id do aluno" -> presente, usado pelo
     * calendario de frequencia lancada (um dia, todos os alunos da turma).
     */
    private Map<String, Map<Long, Boolean>> frequenciasPorDia(Long disciplinaId) {

        Map<String, Map<Long, Boolean>> resultado = new LinkedHashMap<>();

        for (Frequencia frequencia : frequenciaRepository.findByDisciplinaId(disciplinaId)) {
            resultado
                    .computeIfAbsent(frequencia.getData().toString(), chave -> new LinkedHashMap<>())
                    .put(frequencia.getAluno().getId(), frequencia.getPresente());
        }

        return resultado;
    }

    /**
     * Mapa "data (yyyy-MM-dd)" -> presente, usado pelo calendario no perfil
     * de um unico aluno.
     */
    private Map<String, Boolean> frequenciasPorDiaDoAluno(Long disciplinaId, Long alunoId) {

        Map<String, Boolean> resultado = new LinkedHashMap<>();

        for (Frequencia frequencia : frequenciaRepository.findByDisciplinaIdAndAlunoId(disciplinaId, alunoId)) {
            resultado.put(frequencia.getData().toString(), frequencia.getPresente());
        }

        return resultado;
    }
}
