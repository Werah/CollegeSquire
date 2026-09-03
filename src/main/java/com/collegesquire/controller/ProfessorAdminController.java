package com.collegesquire.controller;

import com.collegesquire.model.Professor;
import com.collegesquire.repository.ProfessorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/professores")
public class ProfessorAdminController extends CadastroController<Professor> {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessorAdminController(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected JpaRepository<Professor, Long> repositorio() {
        return professorRepository;
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

        Professor professor = professorRepository.findById(id).orElseThrow();
        professor.setSenha(null);

        model.addAttribute("professor", professor);
        model.addAttribute("tituloPagina", "Professores");

        return "professor-form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Professor professorForm, HttpSession session) {

        if (!autorizado(session)) {
            return "redirect:/home";
        }

        if (professorForm.getId() != null) {
            Professor existente = professorRepository.findById(professorForm.getId()).orElseThrow();
            existente.setNome(professorForm.getNome());
            existente.setCpf(professorForm.getCpf());
            existente.setEndereco(professorForm.getEndereco());
            existente.setTelefone(professorForm.getTelefone());
            existente.setEmail(professorForm.getEmail());
            existente.setRegistro(professorForm.getRegistro());
            if (professorForm.getSenha() != null && !professorForm.getSenha().isBlank()) {
                existente.setSenha(passwordEncoder.encode(professorForm.getSenha()));
            }
            professorRepository.save(existente);
        } else {
            professorForm.setSenha(passwordEncoder.encode(professorForm.getSenha()));
            professorRepository.save(professorForm);
        }

        return "redirect:/professores";
    }
}
