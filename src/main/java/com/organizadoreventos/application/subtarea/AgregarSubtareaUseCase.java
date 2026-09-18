package com.organizadoreventos.application.subtarea;

import com.organizadoreventos.application.dto.Comandos.AgregarSubtareaCommand;
import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.evento.Subtarea;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Application Layer — Use Case
 * Traza: US-005 — CASO DE USO #3 del archetype (modifica el agregado Evento)
 */
@Service
public class AgregarSubtareaUseCase {

    private final EventoRepositoryPort eventoRepository;

    public AgregarSubtareaUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public EventoDto ejecutar(AgregarSubtareaCommand comando) {
        Evento evento = eventoRepository.buscarPorId(comando.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + comando.eventoId()));

        evento.agregarSubtarea(new Subtarea(
                UUID.randomUUID(), comando.nombre(), comando.categoria(), comando.plazo(),
                comando.horasEstimadas(), comando.criticidadProveedorAlta()));

        return EventoDto.desde(eventoRepository.guardar(evento));
    }
}
