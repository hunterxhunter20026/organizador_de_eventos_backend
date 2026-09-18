package com.organizadoreventos.infrastructure.out.persistence;

import com.organizadoreventos.infrastructure.out.persistence.entity.EventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ARCHITECTURAL TRACE: Infrastructure Layer — Spring Data JPA
 * Interfaz técnica usada por el Adapter; NO es el puerto hexagonal
 * (ese vive en domain.ports.out.EventoRepositoryPort).
 */
public interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, UUID> {

    List<EventoJpaEntity> findByOrganizadorId(UUID organizadorId);

    List<EventoJpaEntity> findDistinctByOrganizadorIdAndSubtareas_Plazo(UUID organizadorId, LocalDate plazo);
}
