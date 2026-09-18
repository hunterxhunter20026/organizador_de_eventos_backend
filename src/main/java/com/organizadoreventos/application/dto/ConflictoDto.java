package com.organizadoreventos.application.dto;

import com.organizadoreventos.domain.planificacion.ConflictoSobrecarga;
import com.organizadoreventos.domain.planificacion.Severidad;

import java.time.LocalDate;
import java.util.List;

/**
 * ARCHITECTURAL TRACE: Application Layer — DTO
 * Traza: R-02 — mensaje explícito con día afectado, horas excedidas y tareas involucradas.
 */
public record ConflictoDto(
        LocalDate fecha,
        double horasTotales,
        double horasExcedentes,
        List<SubtareaDto> subtareasInvolucradas,
        Severidad severidad
) {
    public static ConflictoDto desde(ConflictoSobrecarga conflicto) {
        return new ConflictoDto(
                conflicto.fecha(),
                conflicto.horasTotales(),
                conflicto.horasExcedentes(),
                conflicto.subtareasInvolucradas().stream().map(SubtareaDto::desde).toList(),
                conflicto.severidad()
        );
    }
}
