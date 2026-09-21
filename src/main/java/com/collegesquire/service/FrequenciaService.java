package com.collegesquire.service;

import com.collegesquire.model.Aluno;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Frequencia;
import com.collegesquire.repository.FrequenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;

    public FrequenciaService(FrequenciaRepository frequenciaRepository) {
        this.frequenciaRepository = frequenciaRepository;
    }

    public List<Frequencia> listarPorDisciplinaEAluno(Long disciplinaId, Long alunoId) {
        return frequenciaRepository.findByDisciplinaIdAndAlunoIdOrderByDataAscNumeroAulaAsc(disciplinaId, alunoId);
    }

    public List<Frequencia> listarPorAluno(Long alunoId) {
        return frequenciaRepository.findByAlunoIdOrderByDisciplinaIdAsc(alunoId);
    }

    /**
     * Mapa "data (yyyy-MM-dd)" -> lista de presencas (uma por periodo/aula,
     * na ordem), usado pelo calendario no perfil de um unico aluno em uma
     * disciplina.
     */
    public Map<String, List<Boolean>> mapaPorDiaDoAluno(Long disciplinaId, Long alunoId) {

        Map<String, List<Boolean>> resultado = new LinkedHashMap<>();

        for (Frequencia frequencia : listarPorDisciplinaEAluno(disciplinaId, alunoId)) {
            resultado.computeIfAbsent(frequencia.getData().toString(), chave -> new ArrayList<>())
                    .add(frequencia.getPresente());
        }

        return resultado;
    }

    /**
     * Mapa "data (yyyy-MM-dd)" -> "id do aluno" -> lista de presencas (uma
     * por periodo/aula), usado pelo calendario de frequencia lancada (um
     * dia, todos os alunos da turma).
     */
    public Map<String, Map<Long, List<Boolean>>> mapaPorDiaETurma(Long disciplinaId) {

        Map<String, Map<Long, List<Boolean>>> resultado = new LinkedHashMap<>();

        for (Frequencia frequencia : frequenciaRepository.findByDisciplinaIdOrderByDataAscNumeroAulaAsc(disciplinaId)) {
            resultado
                    .computeIfAbsent(frequencia.getData().toString(), chave -> new LinkedHashMap<>())
                    .computeIfAbsent(frequencia.getAluno().getId(), chave -> new ArrayList<>())
                    .add(frequencia.getPresente());
        }

        return resultado;
    }

    /**
     * Lanca a presenca de cada aluno da turma, periodo a periodo, para o
     * dia informado (atualiza os registros existentes, se houver).
     * "presentesCompostos" contem strings "idAluno:periodo" para cada
     * checkbox marcado como presente.
     */
    public void lancarFrequencia(Disciplina disciplina, List<Aluno> alunosDaTurma, LocalDate dataAula,
                                  int totalPeriodos, List<String> presentesCompostos) {

        List<String> presentes = presentesCompostos != null ? presentesCompostos : List.of();

        for (Aluno aluno : alunosDaTurma) {
            for (int periodo = 1; periodo <= totalPeriodos; periodo++) {

                Frequencia frequencia = frequenciaRepository
                        .findByDisciplinaIdAndAlunoIdAndDataAndNumeroAula(disciplina.getId(), aluno.getId(), dataAula, periodo)
                        .orElseGet(Frequencia::new);

                boolean presente = presentes.contains(aluno.getId() + ":" + periodo);

                frequencia.setAluno(aluno);
                frequencia.setDisciplina(disciplina);
                frequencia.setData(dataAula);
                frequencia.setNumeroAula(periodo);
                frequencia.setPresente(presente);
                frequenciaRepository.save(frequencia);
            }
        }
    }
}
