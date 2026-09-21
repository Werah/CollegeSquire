package com.collegesquire.service;

import com.collegesquire.dto.NotasDto;
import com.collegesquire.mapper.NotasMapper;
import com.collegesquire.model.Aluno;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Notas;
import com.collegesquire.repository.NotasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotasService {

    private final NotasRepository notasRepository;
    private final NotasMapper notasMapper;

    public NotasService(NotasRepository notasRepository, NotasMapper notasMapper) {
        this.notasRepository = notasRepository;
        this.notasMapper = notasMapper;
    }

    public List<Notas> listarPorDisciplinaEAluno(Long disciplinaId, Long alunoId) {
        return notasRepository.findByDisciplinaIdAndAlunoId(disciplinaId, alunoId);
    }

    public List<NotasDto> listarDtoPorDisciplinaEAluno(Long disciplinaId, Long alunoId) {
        return notasMapper.toDtoList(listarPorDisciplinaEAluno(disciplinaId, alunoId));
    }

    public List<NotasDto> listarDtoPorDisciplina(Long disciplinaId) {
        return notasMapper.toDtoList(notasRepository.findByDisciplinaId(disciplinaId));
    }

    public List<Notas> listarPorAluno(Long alunoId) {
        return notasRepository.findByAlunoIdOrderByDisciplinaIdAsc(alunoId);
    }

    public Double calcularMediaPorDisciplinaEAluno(Long disciplinaId, Long alunoId) {
        return calcularMedia(listarPorDisciplinaEAluno(disciplinaId, alunoId));
    }

    public Double calcularMedia(List<Notas> notas) {

        if (notas.isEmpty()) {
            return null;
        }

        double somaPesos = notas.stream()
                .mapToDouble(n -> n.getPeso() != null ? n.getPeso() : 1.0)
                .sum();

        double somaPonderada = notas.stream()
                .mapToDouble(n -> (n.getValor() != null ? n.getValor() : 0.0)
                        * (n.getPeso() != null ? n.getPeso() : 1.0))
                .sum();

        return somaPesos == 0 ? null : somaPonderada / somaPesos;
    }

    /**
     * Lanca uma nota para cada aluno informado (em branco = ignorado). Os
     * alunos validos sao os efetivamente matriculados na turma da
     * disciplina; qualquer id fora dessa lista e ignorado por seguranca.
     */
    public void lancarNotas(Disciplina disciplina, List<Aluno> alunosValidos, String descricao, String tipo,
                             Double peso, List<Long> alunoIds, List<String> valores) {

        for (int i = 0; i < alunoIds.size(); i++) {
            String valorTexto = valores.get(i);
            if (valorTexto == null || valorTexto.isBlank()) {
                continue;
            }

            Long alunoId = alunoIds.get(i);
            Aluno aluno = alunosValidos.stream()
                    .filter(a -> a.getId().equals(alunoId))
                    .findFirst()
                    .orElse(null);
            if (aluno == null) {
                continue;
            }

            Notas notas = new Notas();
            notas.setAluno(aluno);
            notas.setDisciplina(disciplina);
            notas.setDescricao(descricao);
            notas.setTipo(tipo);
            notas.setPeso(peso);
            notas.setValor(Double.valueOf(valorTexto.replace(",", ".")));
            notasRepository.save(notas);
        }
    }
}
