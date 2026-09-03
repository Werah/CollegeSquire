package com.collegesquire.controller;

import com.collegesquire.model.TipoUsuario;
import com.collegesquire.repository.AlunoRepository;
import com.collegesquire.repository.DirecaoRepository;
import com.collegesquire.repository.ProfessorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final DirecaoRepository direcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AlunoRepository alunoRepository,
                           ProfessorRepository professorRepository,
                           DirecaoRepository direcaoRepository,
                           PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.direcaoRepository = direcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String login(HttpSession session, Model model) {

        if (session.getAttribute("tipoUsuario") != null) {
            return "redirect:/home";
        }

        if (!model.containsAttribute("abaAtiva")) {
            model.addAttribute("abaAtiva", "login");
        }

        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String email,
                              @RequestParam String senha,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        var aluno = alunoRepository.findByEmail(email);
        if (aluno.isPresent() && passwordEncoder.matches(senha, aluno.get().getSenha())) {
            session.setAttribute("tipoUsuario", TipoUsuario.ALUNO);
            session.setAttribute("usuarioId", aluno.get().getId());
            session.setAttribute("usuarioNome", aluno.get().getNome());
            return "redirect:/home";
        }

        var professor = professorRepository.findByEmail(email);
        if (professor.isPresent() && passwordEncoder.matches(senha, professor.get().getSenha())) {
            session.setAttribute("tipoUsuario", TipoUsuario.PROFESSOR);
            session.setAttribute("usuarioId", professor.get().getId());
            session.setAttribute("usuarioNome", professor.get().getNome());
            return "redirect:/home";
        }

        var direcao = direcaoRepository.findByEmail(email);
        if (direcao.isPresent() && passwordEncoder.matches(senha, direcao.get().getSenha())) {
            session.setAttribute("tipoUsuario", TipoUsuario.DIRECAO);
            session.setAttribute("usuarioId", direcao.get().getId());
            session.setAttribute("usuarioNome", direcao.get().getNome());
            return "redirect:/home";
        }

        redirectAttributes.addFlashAttribute("erroLogin", "E-mail ou senha invalidos.");
        redirectAttributes.addFlashAttribute("abaAtiva", "login");
        return "redirect:/";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(@RequestParam String email,
                                @RequestParam String senhaAtual,
                                @RequestParam String novaSenha,
                                @RequestParam String confirmarNovaSenha,
                                RedirectAttributes redirectAttributes) {

        if (!novaSenha.equals(confirmarNovaSenha)) {
            redirectAttributes.addFlashAttribute("erroSenha", "As senhas nao coincidem.");
            redirectAttributes.addFlashAttribute("abaAtiva", "senha");
            return "redirect:/";
        }

        var aluno = alunoRepository.findByEmail(email);
        if (aluno.isPresent() && passwordEncoder.matches(senhaAtual, aluno.get().getSenha())) {
            aluno.get().setSenha(passwordEncoder.encode(novaSenha));
            alunoRepository.save(aluno.get());
            redirectAttributes.addFlashAttribute("sucessoSenha", "Senha alterada com sucesso.");
            redirectAttributes.addFlashAttribute("abaAtiva", "login");
            return "redirect:/";
        }

        var professor = professorRepository.findByEmail(email);
        if (professor.isPresent() && passwordEncoder.matches(senhaAtual, professor.get().getSenha())) {
            professor.get().setSenha(passwordEncoder.encode(novaSenha));
            professorRepository.save(professor.get());
            redirectAttributes.addFlashAttribute("sucessoSenha", "Senha alterada com sucesso.");
            redirectAttributes.addFlashAttribute("abaAtiva", "login");
            return "redirect:/";
        }

        var direcao = direcaoRepository.findByEmail(email);
        if (direcao.isPresent() && passwordEncoder.matches(senhaAtual, direcao.get().getSenha())) {
            direcao.get().setSenha(passwordEncoder.encode(novaSenha));
            direcaoRepository.save(direcao.get());
            redirectAttributes.addFlashAttribute("sucessoSenha", "Senha alterada com sucesso.");
            redirectAttributes.addFlashAttribute("abaAtiva", "login");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("erroSenha", "E-mail ou senha atual invalidos.");
        redirectAttributes.addFlashAttribute("abaAtiva", "senha");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
