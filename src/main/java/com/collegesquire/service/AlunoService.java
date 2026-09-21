package com.collegesquire.service;

import com.collegesquire.dto.AlunoDto;
import com.collegesquire.dto.DisciplinaResumoDTO;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.AlunoMapper;
import com.collegesquire.mapper.NotasMapper;
import com.collegesquire.model.Aluno;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Frequencia;
import com.collegesquire.model.Notas;
import com.collegesquire.model.Turma;
import com.collegesquire.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService implements CrudService<AlunoDto> {

    private final AlunoRepository alunoRepository;
    private final AlunoMapper alunoMapper;
    private final NotasMapper notasMapper;
    private final DisciplinaService disciplinaService;
    private final NotasService notasService;
    private final FrequenciaService frequenciaService;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, AlunoMapper alunoMapper, NotasMapper notasMapper,
                         DisciplinaService disciplinaService, NotasService notasService,
                         FrequenciaService frequenciaService, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.alunoMapper = alunoMapper;
        this.notasMapper = notasMapper;
        this.disciplinaService = disciplinaService;
        this.notasService = notasService;
        this.frequenciaService = frequenciaService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AlunoDto> listarTodos() {
        return alunoMapper.toDtoList(alunoRepository.findAll());
    }

    public Aluno buscarParaEdicao(Long id) {
        Aluno aluno = buscarEntidade(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
        aluno.setSenha(null);
        return aluno;
    }

    public Optional<Aluno> buscarEntidade(Long id) {
        return alunoRepository.findById(id);
    }

    public AlunoDto buscarDto(Long id) {
        Aluno aluno = buscarEntidade(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
        return alunoMapper.toDto(aluno);
    }

    public List<Aluno> listarPorTurma(Long turmaId) {
        return alunoRepository.findByTurmaId(turmaId);
    }

    public Aluno salvar(Aluno alunoForm) {

        if (alunoForm.getId() != null) {
            Aluno existente = buscarEntidade(alunoForm.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
            existente.setNome(alunoForm.getNome());
            existente.setCpf(alunoForm.getCpf());
            existente.setEndereco(alunoForm.getEndereco());
            existente.setTelefone(alunoForm.getTelefone());
            existente.setEmail(alunoForm.getEmail());
            existente.setMatricula(alunoForm.getMatricula());
            existente.setCurso(alunoForm.getCurso());
            existente.setTurma(alunoForm.getTurma());
            if (alunoForm.getSenha() != null && !alunoForm.getSenha().isBlank()) {
                existente.setSenha(passwordEncoder.encode(alunoForm.getSenha()));
            }
            return alunoRepository.save(existente);
        }

        alunoForm.setSenha(passwordEncoder.encode(alunoForm.getSenha()));
        return alunoRepository.save(alunoForm);
    }

    public void deletar(Long id) {
        Aluno aluno = buscarEntidade(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
        alunoRepository.delete(aluno);
    }

    /**
     * Resumo de notas e faltas do aluno em cada disciplina da sua turma,
     * usado no perfil exibido para a coordenacao.
     */
    public List<DisciplinaResumoDTO> montarResumoDisciplinas(Long alunoId) {

        Aluno aluno = buscarEntidade(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
        Turma turma = aluno.getTurma();

        List<Disciplina> disciplinas = turma != null
                ? disciplinaService.listarPorTurma(turma.getId())
                : List.of();

        List<Notas> todasNotas = notasService.listarPorAluno(alunoId);
        List<Frequencia> todasFrequencias = frequenciaService.listarPorAluno(alunoId);

        return disciplinas.stream()
                .map(disciplina -> {
                    List<Notas> notasDisciplina = todasNotas.stream()
                            .filter(n -> n.getDisciplina().getId().equals(disciplina.getId()))
                            .toList();

                    List<Frequencia> freqDisciplina = todasFrequencias.stream()
                            .filter(f -> f.getDisciplina().getId().equals(disciplina.getId()))
                            .toList();

                    Double media = notasService.calcularMedia(notasDisciplina);
                    int totalAulas = freqDisciplina.size();
                    int faltas = (int) freqDisciplina.stream()
                            .filter(f -> Boolean.FALSE.equals(f.getPresente()))
                            .count();
                    double percentual = totalAulas == 0
                            ? 100.0
                            : (double) (totalAulas - faltas) / totalAulas * 100.0;

                    return new DisciplinaResumoDTO(disciplinaService.paraDto(disciplina),
                            notasMapper.toDtoList(notasDisciplina), media, totalAulas, faltas, percentual);
                })
                .toList();
    }
}
