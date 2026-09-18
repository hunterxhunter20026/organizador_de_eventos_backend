package com.organizadoreventos.infrastructure.out.persistence.entity;

import com.organizadoreventos.domain.evento.EstadoSubtarea;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/** ARCHITECTURAL TRACE: Infrastructure Layer — Entidad JPA */
@Entity
@Table(name = "subtareas")
public class SubtareaJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoJpaEntity evento;

    @Column(nullable = false)
    private String nombre;

    private String categoria;

    private LocalDate plazo;

    @Column(nullable = false)
    private double horasEstimadas;

    @Column(nullable = false)
    private boolean criticidadProveedorAlta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSubtarea estado;

    @Column(length = 1000)
    private String nota;

    protected SubtareaJpaEntity() { }

    public SubtareaJpaEntity(UUID id, EventoJpaEntity evento, String nombre, String categoria,
                              LocalDate plazo, double horasEstimadas, boolean criticidadProveedorAlta,
                              EstadoSubtarea estado, String nota) {
        this.id = id;
        this.evento = evento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.plazo = plazo;
        this.horasEstimadas = horasEstimadas;
        this.criticidadProveedorAlta = criticidadProveedorAlta;
        this.estado = estado;
        this.nota = nota;
    }

    public UUID getId() { return id; }
    public EventoJpaEntity getEvento() { return evento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public LocalDate getPlazo() { return plazo; }
    public void setPlazo(LocalDate plazo) { this.plazo = plazo; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(double horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public boolean isCriticidadProveedorAlta() { return criticidadProveedorAlta; }
    public EstadoSubtarea getEstado() { return estado; }
    public void setEstado(EstadoSubtarea estado) { this.estado = estado; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}
