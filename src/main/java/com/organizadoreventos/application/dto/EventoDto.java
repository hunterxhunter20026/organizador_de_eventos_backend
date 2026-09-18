package com.organizadoreventos.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Application Layer — DTO
 * Separa el modelo de dominio (Evento) del contrato expuesto por la API REST.
 */
public record EventoDto(
        UUID id,
        String nombre,
        LocalDate fecha,
        String ubicacion,
        double progreso,
        List<SubtareaDto> subtareas
) {
    public static EventoDto desde(com.organizadoreventos.domain.evento.Evento evento) {
        return new EventoDto(
                evento.getId(),
                evento.getNombre(),
                evento.getFecha(),
                evento.getUbicacion(),
                evento.calcularProgreso(),
                evento.getSubtareas().stream().map(SubtareaDto::desde).toList()
        );
    }
}
