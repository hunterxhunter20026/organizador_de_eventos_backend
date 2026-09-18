package com.organizadoreventos.infrastructure.out.persistence.mapper;

import com.organizadoreventos.domain.evento.EstadoSubtarea;
import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.evento.Subtarea;
import com.organizadoreventos.infrastructure.out.persistence.entity.EventoJpaEntity;
import com.organizadoreventos.infrastructure.out.persistence.entity.SubtareaJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Mapper (patrón requerido por ADR-01)
 * Traduce entre el modelo de dominio puro y las entidades JPA anotadas,
 * evitando que el framework de persistencia "contamine" el dominio.
 *
 * Nota de implementación: el estado/nota de una Subtarea se rehidrata
 * reutilizando el método de negocio {@code marcarComo}, evitando exponer
 * setters públicos en el dominio solo para servir a la persistencia.
 */
@Component
public class EventoMapper {

    public EventoJpaEntity aEntidad(Evento evento) {
        EventoJpaEntity entidad = new EventoJpaEntity(
                evento.getId(), evento.getOrganizadorId(), evento.getNombre(), evento.getFecha(), evento.getUbicacion());

        List<SubtareaJpaEntity> subtareasEntidad = evento.getSubtareas().stream()
                .map(s -> new SubtareaJpaEntity(
                        s.getId(), entidad, s.getNombre(), s.getCategoria(), s.getPlazo(),
                        s.getHorasEstimadas(), s.isCriticidadProveedorAlta(), s.getEstado(), s.getNota()))
                .toList();
        entidad.getSubtareas().addAll(subtareasEntidad);
        return entidad;
    }

    public Evento aDominio(EventoJpaEntity entidad) {
        Evento evento = new Evento(entidad.getId(), entidad.getOrganizadorId(), entidad.getNombre(),
                entidad.getFecha(), entidad.getUbicacion());

        for (SubtareaJpaEntity se : entidad.getSubtareas()) {
            Subtarea subtarea = new Subtarea(se.getId(), se.getNombre(), se.getCategoria(), se.getPlazo(),
                    se.getHorasEstimadas(), se.isCriticidadProveedorAlta());
            rehidratarEstado(subtarea, se.getEstado(), se.getNota());
            evento.agregarSubtarea(subtarea);
        }
        return evento;
    }

    private void rehidratarEstado(Subtarea subtarea, EstadoSubtarea estado, String nota) {
        if (estado != EstadoSubtarea.PENDIENTE) {
            subtarea.marcarComo(estado, nota);
        }
    }
}
