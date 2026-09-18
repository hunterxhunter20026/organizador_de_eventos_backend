package com.organizadoreventos.domain.planificacion;

import com.organizadoreventos.domain.evento.Subtarea;

import java.time.LocalDate;
import java.util.List;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Value Object
 * Traza: Matriz de Riesgos §1 "Salida del Motor de Reglas"
 * {fecha, horas_totales, horas_excedentes, lista_subtareas_involucradas, severidad}
 */
public record ConflictoSobrecarga(
        LocalDate fecha,
        double horasTotales,
        double horasExcedentes,
        List<Subtarea> subtareasInvolucradas,
        Severidad severidad
) {
    public static Severidad calcularSeveridad(double horasExcedentes) {
        if (horasExcedentes >= 3.0) return Severidad.ALTA;
        if (horasExcedentes >= 1.0) return Severidad.MEDIA;
        return Severidad.BAJA;
    }
}
