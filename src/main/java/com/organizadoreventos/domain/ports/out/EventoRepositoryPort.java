package com.organizadoreventos.domain.ports.out;

import com.organizadoreventos.domain.evento.Evento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Puerto de salida (Hexagonal)
 * Implementado por: infrastructure.out.persistence.JpaEventoRepositoryAdapter
 */
public interface EventoRepositoryPort {

    Evento guardar(Evento evento);

    Optional<Evento> buscarPorId(UUID id);

    List<Evento> listarPorOrganizador(UUID organizadorId);

    void eliminar(UUID id);

    /**
     * Alcance de evaluación "agregado por usuario" — todas las subtareas de
     * TODOS los eventos activos del organizador cuyo plazo cae en {@code fecha}.
     * Traza: Matriz de Riesgos §1 "Alcance de la Evaluación".
     */
    List<Evento> listarConSubtareasEnFecha(UUID organizadorId, LocalDate fecha);
}
