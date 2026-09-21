package com.collegesquire.service;

import com.collegesquire.dto.TurmaDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.TurmaMapper;
import com.collegesquire.model.Turma;
import com.collegesquire.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService implements CrudService<TurmaDto> {

    private final TurmaRepository turmaRepository;
    private final TurmaMapper turmaMapper;

    public TurmaService(TurmaRepository turmaRepository, TurmaMapper turmaMapper) {
        this.turmaRepository = turmaRepository;
        this.turmaMapper = turmaMapper;
    }

    public List<TurmaDto> listarTodos() {
        return turmaMapper.toDtoList(turmaRepository.findAll());
    }

    public Turma buscarParaEdicao(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
    }

    public Turma salvar(Turma turma) {
        return turmaRepository.save(turma);
    }

    public void deletar(Long id) {
        Turma turma = buscarParaEdicao(id);
        turmaRepository.delete(turma);
    }
}
