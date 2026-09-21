package com.collegesquire.controller;

import com.collegesquire.dto.ProfessorDto;
import com.collegesquire.model.Professor;
import com.collegesquire.service.CrudService;
import com.collegesquire.service.ProfessorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/professores")
public class ProfessorAdminController extends CadastroController<ProfessorDto> {

    private final ProfessorService professorService;

    public ProfessorAdminController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @Override
    protected CrudService<ProfessorDto> servico() {
        return professorService;
    }

    @Override
    protected String prefixoView() {
        return "professor";
    }

    @Override
    protected String atributoLista() {
        return "professores";
    }

    @Override
    protected String caminhoBase() {
        return "/professores";
    }

    @Override
    protected String tituloPagina() {
        return "Professores";
    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("professor", professorService.buscarDto(id));
        model.addAttribute("disciplinas", professorService.buscarDisciplinasLecionadas(id));
        model.addAttribute("tituloPagina", "Professores");

        return "professor-perfil";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("professor", new Professor());
        model.addAttribute("tituloPagina", "Professores");

        return "professor-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        model.addAttribute("professor", professorService.buscarParaEdicao(id));
        model.addAttribute("tituloPagina", "Professores");

        return "professor-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Professor professorForm, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        professorService.salvar(professorForm);

        return "redirect:/professores";
    }
}
