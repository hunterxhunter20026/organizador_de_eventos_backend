package com.organizadoreventos.infrastructure.out.persistence;

import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import com.organizadoreventos.infrastructure.out.persistence.mapper.EventoMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Adaptador de salida (Hexagonal)
 * Implementa: domain.ports.out.EventoRepositoryPort
 * Esta clase es la CONEXIÓN real a PostgreSQL — todo lo demás en domain/
 * y application/ es independiente de si esto es JPA, MongoDB o memoria.
 */
@Repository
public class JpaEventoRepositoryAdapter implements EventoRepositoryPort {

    private final EventoJpaRepository jpaRepository;
    private final EventoMapper mapper;

    public JpaEventoRepositoryAdapter(EventoJpaRepository jpaRepository, EventoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Evento guardar(Evento evento) {
        return mapper.aDominio(jpaRepository.save(mapper.aEntidad(evento)));
    }

    @Override
    public Optional<Evento> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Evento> listarPorOrganizador(UUID organizadorId) {
        return jpaRepository.findByOrganizadorId(organizadorId).stream().map(mapper::aDominio).toList();
    }

    @Override
    public void eliminar(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Evento> listarConSubtareasEnFecha(UUID organizadorId, LocalDate fecha) {
        return jpaRepository.findDistinctByOrganizadorIdAndSubtareas_Plazo(organizadorId, fecha)
                .stream().map(mapper::aDominio).toList();
    }
}
