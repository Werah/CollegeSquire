package com.collegesquire.service;

import com.collegesquire.dto.CursoDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.CursoMapper;
import com.collegesquire.model.Curso;
import com.collegesquire.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService implements CrudService<CursoDto> {

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;

    public CursoService(CursoRepository cursoRepository, CursoMapper cursoMapper) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
    }

    public List<CursoDto> listarTodos() {
        return cursoMapper.toDtoList(cursoRepository.findAll());
    }

    public CursoDto buscarDto(Long id) {
        return cursoMapper.toDto(buscarParaEdicao(id));
    }

    public Curso buscarParaEdicao(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
    }

    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void deletar(Long id) {
        Curso curso = buscarParaEdicao(id);
        cursoRepository.delete(curso);
    }
}
