package com.collegesquire.service;

import com.collegesquire.dto.GradeDto;
import com.collegesquire.exception.ExceptionMessage;
import com.collegesquire.exception.ResourceNotFoundException;
import com.collegesquire.mapper.GradeMapper;
import com.collegesquire.model.DiaSemana;
import com.collegesquire.model.Disciplina;
import com.collegesquire.model.Grade;
import com.collegesquire.model.Turma;
import com.collegesquire.repository.DisciplinaRepository;
import com.collegesquire.repository.GradeRepository;
import com.collegesquire.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GradeService {

    public static final int PERIODOS_PADRAO = 4;

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;

    public GradeService(GradeRepository gradeRepository, GradeMapper gradeMapper,
                         TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository) {
        this.gradeRepository = gradeRepository;
        this.gradeMapper = gradeMapper;
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public int periodosPorDia(Turma turma) {
        return turma.getPeriodosPorDia() != null ? turma.getPeriodosPorDia() : PERIODOS_PADRAO;
    }

    public List<GradeDto> buscarGradeDaTurma(Long turmaId) {
        return gradeMapper.toDtoList(gradeRepository.findByTurmaIdOrderByLinhaAsc(turmaId));
    }

    public List<Grade> buscarEntidadesDaTurma(Long turmaId) {
        return gradeRepository.findByTurmaIdOrderByLinhaAsc(turmaId);
    }

    /**
     * Mapa "DIASEMANA_linha" -> id da disciplina, usado para pre-selecionar
     * as celulas ja preenchidas na tela de edicao da grade.
     */
    public Map<String, Long> buscarMapaCelulas(Long turmaId) {

        Map<String, Long> mapa = new LinkedHashMap<>();

        for (Grade grade : buscarEntidadesDaTurma(turmaId)) {
            mapa.put(grade.getDiaSemana().name() + "_" + grade.getLinha(), grade.getDisciplina().getId());
        }

        return mapa;
    }

    public void definirPeriodosPorDia(Long turmaId, Integer periodos) {

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));

        int novoValor = periodos != null && periodos > 0 ? periodos : PERIODOS_PADRAO;
        turma.setPeriodosPorDia(novoValor);
        turmaRepository.save(turma);

        gradeRepository.deleteByTurmaIdAndLinhaGreaterThan(turmaId, novoValor);
    }

    /**
     * Salva a grade inteira de uma turma a partir dos parametros brutos do
     * formulario (um select por celula, nomeado "celula_DIASEMANA_linha").
     */
    public void salvarGrade(Turma turma, Map<String, String> parametros) {

        int periodos = periodosPorDia(turma);

        for (DiaSemana dia : DiaSemana.values()) {
            for (int linha = 1; linha <= periodos; linha++) {

                String valor = parametros.get("celula_" + dia.name() + "_" + linha);
                Long disciplinaId = (valor != null && !valor.isBlank()) ? Long.valueOf(valor) : null;

                Grade existente = gradeRepository
                        .findByTurmaIdAndDiaSemanaAndLinha(turma.getId(), dia, linha)
                        .orElse(null);

                if (disciplinaId == null) {
                    if (existente != null) {
                        gradeRepository.delete(existente);
                    }
                    continue;
                }

                if (existente == null) {
                    existente = new Grade();
                    existente.setTurma(turma);
                    existente.setDiaSemana(dia);
                    existente.setLinha(linha);
                }

                Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                        .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessage.RESOURCE_NOT_FOUND.toString()));
                existente.setDisciplina(disciplina);
                gradeRepository.save(existente);
            }
        }
    }

    public List<GradeDto> buscarGradeDoProfessor(Long professorId) {
        return gradeMapper.toDtoList(gradeRepository.findByDisciplina_ProfessorId(professorId));
    }

    /**
     * Quantidade de aulas (periodos) que a disciplina tem em um dado dia da
     * semana, de acordo com a grade curricular. Zero quando nao ha aula
     * naquele dia.
     */
    public int contarPeriodos(Long disciplinaId, DiaSemana dia) {
        return gradeRepository.findByDisciplinaIdAndDiaSemana(disciplinaId, dia).size();
    }

    /**
     * Mapa "indice do dia (0=domingo..6=sabado, igual ao Date.getDay() do
     * JS)" -> quantidade de aulas seguidas da disciplina naquele dia,
     * usado para restringir o calendario de chamada aos dias de aula.
     */
    public Map<Integer, Integer> periodosPorDiaSemana(Long disciplinaId) {

        Map<Integer, Integer> mapa = new LinkedHashMap<>();

        for (Grade grade : gradeRepository.findByDisciplinaId(disciplinaId)) {
            mapa.merge(grade.getDiaSemana().getIndiceJs(), 1, Integer::sum);
        }

        return mapa;
    }
}
