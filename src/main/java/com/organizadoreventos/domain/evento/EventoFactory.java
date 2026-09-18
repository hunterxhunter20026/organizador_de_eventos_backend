package com.organizadoreventos.domain.evento;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Factory pattern (GoF)
 * Traza: US-001
 *
 * Centraliza las invariantes de creación de un Evento para que ningún
 * adaptador (Controller, otro Use Case) pueda construir un Evento inválido.
 */
public final class EventoFactory {

    private EventoFactory() { }

    public static Evento crear(UUID organizadorId, String nombre, LocalDate fecha, String ubicacion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del evento es obligatoria.");
        }
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("La ubicación del evento es obligatoria.");
        }
        return new Evento(UUID.randomUUID(), organizadorId, nombre, fecha, ubicacion);
    }
}
