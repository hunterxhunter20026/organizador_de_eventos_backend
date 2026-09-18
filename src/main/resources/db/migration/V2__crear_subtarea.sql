-- ARCHITECTURAL TRACE: Infrastructure Layer — Migración Flyway
CREATE TABLE subtareas (
    id                          UUID PRIMARY KEY,
    evento_id                   UUID NOT NULL REFERENCES eventos(id) ON DELETE CASCADE,
    nombre                      VARCHAR(255) NOT NULL,
    categoria                   VARCHAR(100),
    plazo                       DATE,
    horas_estimadas             DOUBLE PRECISION NOT NULL CHECK (horas_estimadas > 0),
    criticidad_proveedor_alta   BOOLEAN NOT NULL DEFAULT FALSE,
    estado                      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    nota                        VARCHAR(1000)
);

CREATE INDEX idx_subtareas_evento ON subtareas(evento_id);
CREATE INDEX idx_subtareas_plazo ON subtareas(plazo);
