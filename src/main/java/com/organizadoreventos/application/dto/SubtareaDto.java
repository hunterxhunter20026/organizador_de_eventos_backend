package com.organizadoreventos.application.dto;

import com.organizadoreventos.domain.evento.EstadoSubtarea;
import com.organizadoreventos.domain.evento.Subtarea;

import java.time.LocalDate;
import java.util.UUID;

/** ARCHITECTURAL TRACE: Application Layer — DTO */
public record SubtareaDto(
        UUID id,
        String nombre,
        String categoria,
        LocalDate plazo,
        double horasEstimadas,
        boolean criticidadProveedorAlta,
        EstadoSubtarea estado,
        String nota
) {
    public static SubtareaDto desde(Subtarea subtarea) {
        return new SubtareaDto(
                subtarea.getId(),
                subtarea.getNombre(),
                subtarea.getCategoria(),
                subtarea.getPlazo(),
                subtarea.getHorasEstimadas(),
                subtarea.isCriticidadProveedorAlta(),
                subtarea.getEstado(),
                subtarea.getNota()
        );
    }
}
