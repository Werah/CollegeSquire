package com.collegesquire.service;

import com.collegesquire.dto.DisciplinaDto;
import com.collegesquire.dto.DisciplinaProfessorDTO;
import com.collegesquire.dto.ProfessorDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.ProfessorMapper;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Professor;
import com.collegesquire.repository.ProfessorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService implements CrudService<ProfessorDto> {

    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final DisciplinaService disciplinaService;
    private final AlunoService alunoService;
    private final PasswordEncoder passwordEncoder;

    public ProfessorService(ProfessorRepository professorRepository, ProfessorMapper professorMapper,
                             DisciplinaService disciplinaService, AlunoService alunoService,
                             PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.professorMapper = professorMapper;
        this.disciplinaService = disciplinaService;
        this.alunoService = alunoService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ProfessorDto> listarTodos() {
        return professorMapper.toDtoList(professorRepository.findAll());
    }

    public Professor buscarParaEdicao(Long id) {
        Professor professor = buscarEntidade(id);
        professor.setSenha(null);
        return professor;
    }

    public Optional<Professor> buscarEntidadeOpcional(Long id) {
        return professorRepository.findById(id);
    }

    public Professor buscarEntidade(Long id) {
        return buscarEntidadeOpcional(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
    }

    public ProfessorDto buscarDto(Long id) {
        return professorMapper.toDto(buscarEntidade(id));
    }

    public Professor salvar(Professor professorForm) {

        if (professorForm.getId() != null) {
            Professor existente = buscarEntidade(professorForm.getId());
            existente.setNome(professorForm.getNome());
            existente.setCpf(professorForm.getCpf());
            existente.setEndereco(professorForm.getEndereco());
            existente.setTelefone(professorForm.getTelefone());
            existente.setEmail(professorForm.getEmail());
            existente.setRegistro(professorForm.getRegistro());
            if (professorForm.getSenha() != null && !professorForm.getSenha().isBlank()) {
                existente.setSenha(passwordEncoder.encode(professorForm.getSenha()));
            }
            return professorRepository.save(existente);
        }

        professorForm.setSenha(passwordEncoder.encode(professorForm.getSenha()));
        return professorRepository.save(professorForm);
    }

    public void deletar(Long id) {
        Professor professor = buscarEntidade(id);
        professorRepository.delete(professor);
    }

    /**
     * Disciplinas lecionadas por este professor, usado no perfil exibido
     * para a coordenacao.
     */
    public List<DisciplinaDto> buscarDisciplinasLecionadas(Long professorId) {
        return disciplinaService.listarPorProfessor(professorId).stream()
                .map(disciplinaService::paraDto)
                .toList();
    }

    /**
     * Disciplinas lecionadas por este professor com a quantidade de
     * alunos matriculados na turma, usado no painel inicial do professor.
     */
    public List<DisciplinaProfessorDTO> montarPainelDisciplinas(Long professorId) {

        List<Disciplina> disciplinas = disciplinaService.listarPorProfessor(professorId);

        return disciplinas.stream()
                .map(disciplina -> {
                    int totalAlunos = disciplina.getTurma() != null
                            ? alunoService.listarPorTurma(disciplina.getTurma().getId()).size()
                            : 0;
                    return new DisciplinaProfessorDTO(disciplinaService.paraDto(disciplina), totalAlunos);
                })
                .toList();
    }
}
