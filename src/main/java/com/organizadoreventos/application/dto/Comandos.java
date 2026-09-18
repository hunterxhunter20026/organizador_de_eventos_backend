package com.organizadoreventos.application.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Application Layer — Command DTOs
 *
 * [Archetype] Este archivo trae solo los comandos de los 3 casos de uso
 * implementados en este recorte. Para extender el archetype con el resto
 * del backlog (editar/eliminar evento, marcar progreso, vista hoy,
 * configuración, auth), agrega aquí el Command correspondiente siguiendo
 * el mismo patrón — son value objects simples, sin lógica.
 */
public final class Comandos {

    private Comandos() { }

    public record CrearEventoCommand(UUID organizadorId, String nombre, LocalDate fecha, String ubicacion) { }

    public record AgregarSubtareaCommand(UUID eventoId, String nombre, String categoria,
                                          LocalDate plazo, double horasEstimadas,
                                          boolean criticidadProveedorAlta) { }

    public record ReprogramarSubtareaCommand(UUID eventoId, UUID subtareaId, LocalDate nuevaFecha) { }
}
