package com.organizadoreventos.application.evento;

import com.organizadoreventos.application.dto.Comandos.CrearEventoCommand;
import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.evento.EventoFactory;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ARCHITECTURAL TRACE: Application Layer — Use Case
 * Traza: US-001 — CASO DE USO #1 del archetype (Crear)
 */
@Service
public class CrearEventoUseCase {

    private final EventoRepositoryPort eventoRepository;

    public CrearEventoUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public EventoDto ejecutar(CrearEventoCommand comando) {
        Evento evento = EventoFactory.crear(comando.organizadorId(), comando.nombre(), comando.fecha(), comando.ubicacion());
        return EventoDto.desde(eventoRepository.guardar(evento));
    }
}
