package com.collegesquire.service;

import com.collegesquire.dto.LogSalaDto;
import com.collegesquire.dto.SalaDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.LogSalaMapper;
import com.collegesquire.mapper.SalaMapper;
import com.collegesquire.model.Sala;
import com.collegesquire.repository.LogSalaRepository;
import com.collegesquire.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService implements CrudService<SalaDto> {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final LogSalaRepository logSalaRepository;
    private final LogSalaMapper logSalaMapper;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper,
                        LogSalaRepository logSalaRepository, LogSalaMapper logSalaMapper) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.logSalaRepository = logSalaRepository;
        this.logSalaMapper = logSalaMapper;
    }

    public List<SalaDto> listarTodos() {
        return salaMapper.toDtoList(salaRepository.findAll());
    }

    public Sala buscarParaEdicao(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
    }

    public Sala salvar(Sala sala) {
        return salaRepository.save(sala);
    }

    public void deletar(Long id) {
        Sala sala = buscarParaEdicao(id);
        salaRepository.delete(sala);
    }

    public List<LogSalaDto> buscarLogsPorSala(Long salaId) {
        return logSalaMapper.toDtoList(logSalaRepository.findBySalaIdOrderByDataHoraDesc(salaId));
    }
}
