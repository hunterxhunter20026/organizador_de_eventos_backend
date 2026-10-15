package co.edu.univalle.demo.service;

import co.edu.univalle.demo.model.TareaLogisticaModel;
import co.edu.univalle.demo.dto.HoyResponseDTO;
import co.edu.univalle.demo.repository.HistorialReprogramacionRepository;
import co.edu.univalle.demo.repository.TareaLogisticaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TareaLogisticaServiceTest {

    @Mock
    private TareaLogisticaRepository tareaRepository;

    @Mock
    private HistorialReprogramacionRepository historialRepository;

    @Mock
    private EventoService eventoService;

    @Mock
    private UsuarioService usuarioService;

    private TareaLogisticaService tareaService;

    @BeforeEach
    void setUp() {
        tareaService = new TareaLogisticaService(tareaRepository, historialRepository, eventoService, usuarioService);
    }

    @Test
    void reprogramarActualizaLaFechaYLasHorasEstimadas() {
        final LocalDate fechaNueva = LocalDate.of(2026, 11, 20);
        final TareaLogisticaModel tarea = TareaLogisticaModel.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Reservar salón")
                .fechaLimite(LocalDate.of(2026, 11, 10))
                .horasEstimadas(new BigDecimal("2"))
                .estado(TareaLogisticaModel.ESTADO_PENDIENTE)
                .build();
        final BigDecimal horasNuevas = new BigDecimal("4");

        given(tareaRepository.findById(1L)).willReturn(Optional.of(tarea));
        given(usuarioService.obtenerLimiteDiario(10L)).willReturn(new BigDecimal("8"));
        given(tareaRepository.findByUsuarioId(10L)).willReturn(List.of(tarea));
        given(tareaRepository.save(tarea)).willReturn(tarea);

        final TareaLogisticaModel resultado = tareaService.reprogramar(1L, 10L, fechaNueva, horasNuevas);

        assertEquals(fechaNueva, resultado.getFechaLimite());
        assertEquals(horasNuevas, resultado.getHorasEstimadas());
        verify(historialRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void volverAPendienteLimpiaLosDatosDeEjecucion() {
        final TareaLogisticaModel tarea = TareaLogisticaModel.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Confirmar invitados")
                .fechaLimite(LocalDate.of(2026, 11, 10))
                .horasEstimadas(new BigDecimal("2"))
                .estado(TareaLogisticaModel.ESTADO_POSPUESTA)
                .fechaEjecucion(LocalDate.now())
                .notaEjecucion("Esperando confirmación")
                .build();
        given(tareaRepository.findById(1L)).willReturn(Optional.of(tarea));
        given(tareaRepository.save(tarea)).willReturn(tarea);

        final TareaLogisticaModel resultado = tareaService.marcarEstado(1L, 10L, "Pendiente", null);

        assertEquals(TareaLogisticaModel.ESTADO_PENDIENTE, resultado.getEstado());
        assertNull(resultado.getFechaEjecucion());
        assertNull(resultado.getNotaEjecucion());
    }

    @Test
    void completarGestionPospuestaLaMarcaCompletada() {
        final TareaLogisticaModel tarea = TareaLogisticaModel.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Confirmar invitados")
                .fechaLimite(LocalDate.of(2026, 11, 10))
                .horasEstimadas(new BigDecimal("2"))
                .estado(TareaLogisticaModel.ESTADO_POSPUESTA)
                .notaEjecucion("Esperando confirmación")
                .build();
        given(tareaRepository.findById(1L)).willReturn(Optional.of(tarea));
        given(tareaRepository.save(tarea)).willReturn(tarea);

        final TareaLogisticaModel resultado = tareaService.marcarEstado(1L, 10L, "Completada", null);

        assertEquals(TareaLogisticaModel.ESTADO_COMPLETADA, resultado.getEstado());
        assertEquals(LocalDate.now(), resultado.getFechaEjecucion());
        assertNull(resultado.getNotaEjecucion());
    }

    @Test
    void vistaHoyActivasIncluyePendientesYPospuestas() {
        final TareaLogisticaModel pendiente = TareaLogisticaModel.builder()
                .id(1L)
                .fechaLimite(LocalDate.now())
                .horasEstimadas(new BigDecimal("1"))
                .estado(TareaLogisticaModel.ESTADO_PENDIENTE)
                .build();
        final TareaLogisticaModel pospuesta = TareaLogisticaModel.builder()
                .id(2L)
                .fechaLimite(LocalDate.now())
                .horasEstimadas(new BigDecimal("2"))
                .estado(TareaLogisticaModel.ESTADO_POSPUESTA)
                .build();
        final TareaLogisticaModel completada = TareaLogisticaModel.builder()
                .id(3L)
                .fechaLimite(LocalDate.now())
                .horasEstimadas(new BigDecimal("3"))
                .estado(TareaLogisticaModel.ESTADO_COMPLETADA)
                .build();
        given(tareaRepository.findByUsuarioId(10L)).willReturn(List.of(pendiente, pospuesta, completada));

        final HoyResponseDTO resultado = tareaService.obtenerVistaHoy(10L, null, "Activas");

        assertEquals(2, resultado.getParaHoy().size());
        assertTrue(resultado.getParaHoy().contains(pendiente));
        assertTrue(resultado.getParaHoy().contains(pospuesta));
    }
}
