package com.organizadoreventos.application.evento;

import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Application Layer — Use Case
 * Traza: US-004 — CASO DE USO #2 del archetype (Listar) — prueba la
 * lectura extremo a extremo (Controller → Puerto → Adaptador JPA → BD).
 */
@Service
public class ListarEventosUseCase {

    private final EventoRepositoryPort eventoRepository;

    public ListarEventosUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoDto> ejecutar(UUID organizadorId) {
        return eventoRepository.listarPorOrganizador(organizadorId).stream()
                .map(EventoDto::desde)
                .toList();
    }
}
