package com.organizadoreventos.infrastructure.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Entidad JPA
 * Deliberadamente separada de {@link com.organizadoreventos.domain.evento.Evento}
 * (ADR-01): el dominio no debe conocer anotaciones de persistencia.
 */
@Entity
@Table(name = "eventos")
public class EventoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID organizadorId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String ubicacion;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SubtareaJpaEntity> subtareas = new ArrayList<>();

    protected EventoJpaEntity() { }

    public EventoJpaEntity(UUID id, UUID organizadorId, String nombre, LocalDate fecha, String ubicacion) {
        this.id = id;
        this.organizadorId = organizadorId;
        this.nombre = nombre;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
    }

    public UUID getId() { return id; }
    public UUID getOrganizadorId() { return organizadorId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public List<SubtareaJpaEntity> getSubtareas() { return subtareas; }
}
