package com.organizadoreventos.domain.evento;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Entity (interna al agregado Evento)
 * Traza: US-005, US-006, US-007, US-017, US-018
 *
 * Invariante de dominio: horasEstimadas > 0 (validado en el constructor,
 * nunca delegado a la capa de infraestructura).
 */
public class Subtarea {

    private final UUID id;
    private String nombre;
    private String categoria;
    private LocalDate plazo;
    private double horasEstimadas;
    private boolean criticidadProveedorAlta;
    private EstadoSubtarea estado;
    private String nota;

    public Subtarea(UUID id, String nombre, String categoria, LocalDate plazo,
                     double horasEstimadas, boolean criticidadProveedorAlta) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la subtarea no puede estar vacío.");
        }
        if (horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser mayores a cero.");
        }
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.plazo = plazo;
        this.horasEstimadas = horasEstimadas;
        this.criticidadProveedorAlta = criticidadProveedorAlta;
        this.estado = EstadoSubtarea.PENDIENTE;
    }

    public void reprogramar(LocalDate nuevaFecha) {
        if (nuevaFecha == null) {
            throw new IllegalArgumentException("La nueva fecha no puede ser nula.");
        }
        this.plazo = nuevaFecha;
    }

    public void editar(String nombre, String categoria, double horasEstimadas) {
        if (horasEstimadas <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser mayores a cero.");
        }
        this.nombre = nombre;
        this.categoria = categoria;
        this.horasEstimadas = horasEstimadas;
    }

    public void marcarComo(EstadoSubtarea nuevoEstado, String nota) {
        this.estado = nuevoEstado;
        this.nota = nota;
    }

    public boolean estaHecha() {
        return estado == EstadoSubtarea.HECHO;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public LocalDate getPlazo() { return plazo; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public boolean isCriticidadProveedorAlta() { return criticidadProveedorAlta; }
    public EstadoSubtarea getEstado() { return estado; }
    public String getNota() { return nota; }
}
