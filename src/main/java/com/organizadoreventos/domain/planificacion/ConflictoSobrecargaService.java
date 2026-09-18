package com.organizadoreventos.domain.planificacion;

import com.organizadoreventos.domain.evento.Subtarea;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Domain Service (sin estado)
 * Traza: US-013, US-014 · Matriz de Riesgos §1 (Trigger, Condición, Alcance, Momento)
 *
 * ADR-04: este servicio NO cachea resultados. Se invoca de forma síncrona
 * dentro de la misma transacción que persiste el cambio de horas/fecha
 * (ver ReprogramarSubtareaUseCase), cumpliendo:
 *   - R-01: recalcular la suma en cada escritura (no cachear)
 *   - R-05: el total del día se evalúa de forma atómica al guardar
 *
 * Condición de conflicto: SUM(horas_estimadas del día D, agregado por
 * usuario across todos sus eventos activos) > límite_diario_configurado.
 *
 * Se usa un epsilon para evitar falsos negativos/positivos por
 * imprecisión de punto flotante en la suma de horas (R-01: "casos límite,
 * exactamente en el límite, un minuto por encima").
 */
public class ConflictoSobrecargaService {

    private static final double EPSILON = 1e-9;

    /**
     * @param subtareasDelDia todas las subtareas (de TODOS los eventos activos
     *                        del organizador) cuyo plazo cae en {@code fecha} —
     *                        alcance "agregado por usuario", no solo por evento.
     */
    public Optional<ConflictoSobrecarga> evaluar(LocalDate fecha, List<Subtarea> subtareasDelDia, LimiteDiario limite) {
        if (subtareasDelDia.isEmpty()) {
            return Optional.empty();
        }

        double horasTotales = subtareasDelDia.stream()
                .mapToDouble(Subtarea::getHorasEstimadas)
                .sum();

        double excedente = horasTotales - limite.getHoras();

        if (excedente <= EPSILON) {
            // Exactamente en el límite (o por debajo) NO es conflicto.
            return Optional.empty();
        }

        Severidad severidad = ConflictoSobrecarga.calcularSeveridad(excedente);
        return Optional.of(new ConflictoSobrecarga(fecha, horasTotales, excedente, List.copyOf(subtareasDelDia), severidad));
    }
}
