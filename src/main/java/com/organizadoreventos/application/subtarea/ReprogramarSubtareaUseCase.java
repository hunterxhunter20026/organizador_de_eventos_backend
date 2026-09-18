package com.organizadoreventos.application.subtarea;

import com.organizadoreventos.application.dto.Comandos.ReprogramarSubtareaCommand;
import com.organizadoreventos.application.dto.ConflictoDto;
import com.organizadoreventos.application.dto.EventoDto;
import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.evento.Subtarea;
import com.organizadoreventos.domain.planificacion.ConflictoSobrecargaService;
import com.organizadoreventos.domain.planificacion.LimiteDiario;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ARCHITECTURAL TRACE: Application Layer — Use Case
 * FLUJO TRANSACCIONAL DE REFERENCIA del archetype:
 *
 *   SubtareaController → ReprogramarSubtareaUseCase → Subtarea.reprogramar()
 *   + ConflictoSobrecargaService.evaluar()  → EventoRepositoryPort
 *   → JpaEventoRepositoryAdapter → PostgreSQL
 *
 * [Archetype] Simplificación deliberada: al no incluir Organizador/Auth en
 * este recorte, se usa {@link LimiteDiario#porDefecto()} (6h) en vez de leer
 * el límite configurado por el usuario. Cuando agregues autenticación,
 * reemplaza esta línea por una consulta a OrganizadorRepositoryPort — el
 * resto del flujo (la parte que realmente importa: la regla de negocio y
 * la atomicidad transaccional) no cambia.
 */
@Service
public class ReprogramarSubtareaUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final ConflictoSobrecargaService conflictoSobrecargaService;

    public ReprogramarSubtareaUseCase(EventoRepositoryPort eventoRepository,
                                       ConflictoSobrecargaService conflictoSobrecargaService) {
        this.eventoRepository = eventoRepository;
        this.conflictoSobrecargaService = conflictoSobrecargaService;
    }

    public record Resultado(EventoDto evento, Optional<ConflictoDto> conflicto) { }

    @Transactional
    public Resultado ejecutar(ReprogramarSubtareaCommand comando) {
        Evento evento = eventoRepository.buscarPorId(comando.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado: " + comando.eventoId()));

        Subtarea subtarea = evento.obtenerSubtarea(comando.subtareaId());
        subtarea.reprogramar(comando.nuevaFecha());

        Evento eventoGuardado = eventoRepository.guardar(evento);

        List<Subtarea> subtareasDelDiaDestino = eventoRepository
                .listarConSubtareasEnFecha(eventoGuardado.getOrganizadorId(), comando.nuevaFecha())
                .stream()
                .flatMap(e -> e.getSubtareas().stream())
                .filter(s -> comando.nuevaFecha().equals(s.getPlazo()))
                .toList();

        Optional<ConflictoDto> conflicto = conflictoSobrecargaService
                .evaluar(comando.nuevaFecha(), subtareasDelDiaDestino, LimiteDiario.porDefecto())
                .map(ConflictoDto::desde);

        return new Resultado(EventoDto.desde(eventoGuardado), conflicto);
    }
}
