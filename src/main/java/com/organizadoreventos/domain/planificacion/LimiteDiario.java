package com.organizadoreventos.domain.planificacion;

/**
 * ARCHITECTURAL TRACE: Domain Layer — Value Object
 * Traza: US-008
 * Mitigación aplicada: R-06 — rango válido 1–16 horas, evita bloquear
 * toda la planificación con un límite irreal (ej. 0 horas).
 */
public final class LimiteDiario {

    private static final double MIN_HORAS = 1.0;
    private static final double MAX_HORAS = 16.0;

    private final double horas;

    public LimiteDiario(double horas) {
        if (horas < MIN_HORAS || horas > MAX_HORAS) {
            throw new IllegalArgumentException(
                    "El límite diario debe estar entre %s y %s horas.".formatted(MIN_HORAS, MAX_HORAS));
        }
        this.horas = horas;
    }

    public static LimiteDiario porDefecto() {
        return new LimiteDiario(6.0);
    }

    public double getHoras() { return horas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimiteDiario that)) return false;
        return Double.compare(horas, that.horas) == 0;
    }

    @Override
    public int hashCode() { return Double.hashCode(horas); }
}
