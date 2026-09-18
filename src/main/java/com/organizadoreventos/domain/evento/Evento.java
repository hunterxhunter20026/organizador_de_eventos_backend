package com.organizadoreventos.domain.evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Aggregate Root
 * Traza: US-001, US-002, US-003, US-004, US-019, US-020
 *
 * Un Evento es dueño del ciclo de vida de sus Subtareas: toda modificación
 * a una subtarea pasa por este agregado, preservando la consistencia
 * transaccional dentro del límite del agregado (regla DDD).
 */
public class Evento {

    private final UUID id;
    private final UUID organizadorId;
    private String nombre;
    private LocalDate fecha;
    private String ubicacion;
    private final List<Subtarea> subtareas = new ArrayList<>();

    public Evento(UUID id, UUID organizadorId, String nombre, LocalDate fecha, String ubicacion) {
        this.id = id;
        this.organizadorId = organizadorId;
        this.nombre = nombre;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
    }

    public void editarDatosGenerales(String nombre, LocalDate fecha, String ubicacion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del evento no puede estar vacío.");
        }
        this.nombre = nombre;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
    }

    public void agregarSubtarea(Subtarea subtarea) {
        this.subtareas.add(subtarea);
    }

    public void eliminarSubtarea(UUID subtareaId) {
        subtareas.removeIf(s -> s.getId().equals(subtareaId));
    }

    public Subtarea obtenerSubtarea(UUID subtareaId) {
        return subtareas.stream()
                .filter(s -> s.getId().equals(subtareaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subtarea no encontrada: " + subtareaId));
    }

    /** US-019 / US-020 — (subtareas hechas / subtareas totales) */
    public double calcularProgreso() {
        if (subtareas.isEmpty()) {
            return 0.0;
        }
        long hechas = subtareas.stream().filter(Subtarea::estaHecha).count();
        return (double) hechas / subtareas.size();
    }

    public UUID getId() { return id; }
    public UUID getOrganizadorId() { return organizadorId; }
    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public String getUbicacion() { return ubicacion; }
    public List<Subtarea> getSubtareas() { return List.copyOf(subtareas); }
}
