package com.organizadoreventos.domain.planificacion;

import com.organizadoreventos.domain.evento.Subtarea;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ARCHITECTURAL TRACE: Test del Domain Service crítico del sistema.
 * Traza: R-01 — "pruebas unitarias del motor de reglas con casos límite:
 * exactamente en el límite, un minuto por encima".
 */
class ConflictoSobrecargaServiceTest {

    private final ConflictoSobrecargaService service = new ConflictoSobrecargaService();
    private final LocalDate hoy = LocalDate.of(2026, 9, 18);

    private Subtarea subtareaDe(double horas) {
        return new Subtarea(UUID.randomUUID(), "Tarea", "General", hoy, horas, false);
    }

    @Test
    void should_not_report_conflict_when_total_hours_equal_daily_limit() {
        // Arrange
        LimiteDiario limite = new LimiteDiario(6.0);
        List<Subtarea> subtareas = List.of(subtareaDe(3.0), subtareaDe(3.0)); // exactamente 6.0

        // Act
        var resultado = service.evaluar(hoy, subtareas, limite);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void should_report_conflict_when_total_exceeds_limit_by_one_minute() {
        // Arrange
        LimiteDiario limite = new LimiteDiario(6.0);
        double unMinutoEnHoras = 1.0 / 60.0;
        List<Subtarea> subtareas = List.of(subtareaDe(3.0), subtareaDe(3.0 + unMinutoEnHoras));

        // Act
        var resultado = service.evaluar(hoy, subtareas, limite);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().horasExcedentes()).isCloseTo(unMinutoEnHoras, org.assertj.core.data.Offset.offset(1e-6));
    }

    @Test
    void should_return_empty_when_no_subtasks_scheduled_that_day() {
        // Arrange
        LimiteDiario limite = LimiteDiario.porDefecto();

        // Act
        var resultado = service.evaluar(hoy, List.of(), limite);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void should_classify_severity_as_high_when_excess_is_three_hours_or_more() {
        // Arrange
        LimiteDiario limite = new LimiteDiario(4.0);
        List<Subtarea> subtareas = List.of(subtareaDe(7.5)); // excedente = 3.5h

        // Act
        var resultado = service.evaluar(hoy, subtareas, limite);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().severidad()).isEqualTo(Severidad.ALTA);
    }

    @Test
    void should_recalculate_from_scratch_on_every_call_without_caching() {
        // Arrange — R-01: nunca se reutiliza un resultado previo
        LimiteDiario limite = new LimiteDiario(6.0);
        List<Subtarea> primeraCarga = List.of(subtareaDe(7.0));
        List<Subtarea> segundaCarga = List.of(subtareaDe(2.0));

        // Act
        var primerResultado = service.evaluar(hoy, primeraCarga, limite);
        var segundoResultado = service.evaluar(hoy, segundaCarga, limite);

        // Assert
        assertThat(primerResultado).isPresent();
        assertThat(segundoResultado).isEmpty();
    }
}
