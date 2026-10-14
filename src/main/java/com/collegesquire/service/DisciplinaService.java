package com.collegesquire.service;

import com.collegesquire.dto.DisciplinaDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.DisciplinaMapper;
import com.collegesquire.model.Disciplina;
import com.collegesquire.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaService implements CrudService<DisciplinaDto> {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaMapper disciplinaMapper;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, DisciplinaMapper disciplinaMapper) {
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaMapper = disciplinaMapper;
    }

    public List<DisciplinaDto> listarTodos() {
        return disciplinaMapper.toDtoList(disciplinaRepository.findAll());
    }

    public Disciplina buscarParaEdicao(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
    }

    public Optional<Disciplina> buscarEntidade(Long id) {
        return disciplinaRepository.findById(id);
    }

    public Disciplina salvar(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public void deletar(Long id) {
        Disciplina disciplina = buscarParaEdicao(id);
        disciplinaRepository.delete(disciplina);
    }

    public List<Disciplina> listarPorTurma(Long turmaId) {
        return disciplinaRepository.findByTurmaId(turmaId);
    }

    public List<DisciplinaDto> listarDtoPorTurma(Long turmaId) {
        return disciplinaMapper.toDtoList(disciplinaRepository.findByTurmaId(turmaId));
    }

    public List<Disciplina> listarPorProfessor(Long professorId) {
        return disciplinaRepository.findByProfessorId(professorId);
    }

    public List<DisciplinaDto> listarPorCurso(Long cursoId) {
        return disciplinaMapper.toDtoList(disciplinaRepository.findByTurma_CursoId(cursoId));
    }

    public DisciplinaDto paraDto(Disciplina disciplina) {
        return disciplinaMapper.toDto(disciplina);
    }
}
