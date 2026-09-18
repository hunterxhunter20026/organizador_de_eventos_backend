package com.organizadoreventos.application.subtarea;

import com.organizadoreventos.application.dto.Comandos.ReprogramarSubtareaCommand;
import com.organizadoreventos.domain.evento.Evento;
import com.organizadoreventos.domain.evento.Subtarea;
import com.organizadoreventos.domain.planificacion.ConflictoSobrecargaService;
import com.organizadoreventos.domain.ports.out.EventoRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ARCHITECTURAL TRACE: Test del flujo transaccional de referencia.
 * Todas las dependencias de infraestructura están mockeadas (sin BD real).
 */
@ExtendWith(MockitoExtension.class)
class ReprogramarSubtareaUseCaseTest {

    @Mock
    private EventoRepositoryPort eventoRepository;

    private final ConflictoSobrecargaService conflictoSobrecargaService = new ConflictoSobrecargaService();

    private final UUID organizadorId = UUID.randomUUID();
    private final UUID eventoId = UUID.randomUUID();
    private final LocalDate hoy = LocalDate.of(2026, 9, 18);
    private final LocalDate nuevaFecha = hoy.plusDays(3);

    @Test
    void should_reschedule_subtask_and_report_no_conflict_when_within_default_limit() {
        // Arrange — LimiteDiario.porDefecto() = 6h (ver simplificación documentada en la clase)
        Subtarea subtarea = new Subtarea(UUID.randomUUID(), "Montaje de escenario", "Logística", hoy, 2.0, false);
        Evento evento = new Evento(eventoId, organizadorId, "Boda Andrea & Luis", hoy, "Salón Jardín");
        evento.agregarSubtarea(subtarea);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(eventoRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventoRepository.listarConSubtareasEnFecha(organizadorId, nuevaFecha)).thenReturn(List.of(evento));

        var useCase = new ReprogramarSubtareaUseCase(eventoRepository, conflictoSobrecargaService);

        // Act
        var resultado = useCase.ejecutar(new ReprogramarSubtareaCommand(eventoId, subtarea.getId(), nuevaFecha));

        // Assert
        assertThat(resultado.conflicto()).isEmpty();
        assertThat(subtarea.getPlazo()).isEqualTo(nuevaFecha);
    }

    @Test
    void should_report_conflict_when_target_day_exceeds_default_limit_after_reschedule() {
        // Arrange
        Subtarea subtareaAMover = new Subtarea(UUID.randomUUID(), "Decoración floral", "Logística", hoy, 5.0, false);
        Subtarea otraDelMismoDia = new Subtarea(UUID.randomUUID(), "Catering", "Logística", nuevaFecha, 4.0, false);

        Evento evento = new Evento(eventoId, organizadorId, "Boda Andrea & Luis", hoy, "Salón Jardín");
        evento.agregarSubtarea(subtareaAMover);
        evento.agregarSubtarea(otraDelMismoDia);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));
        when(eventoRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventoRepository.listarConSubtareasEnFecha(organizadorId, nuevaFecha)).thenReturn(List.of(evento));

        var useCase = new ReprogramarSubtareaUseCase(eventoRepository, conflictoSobrecargaService);

        // Act — mueve "Decoración floral" (5h) al día de "Catering" (4h) = 9h > límite por defecto 6h
        var resultado = useCase.ejecutar(new ReprogramarSubtareaCommand(eventoId, subtareaAMover.getId(), nuevaFecha));

        // Assert
        assertThat(resultado.conflicto()).isPresent();
        assertThat(resultado.conflicto().get().horasExcedentes()).isEqualTo(3.0);
    }
}
