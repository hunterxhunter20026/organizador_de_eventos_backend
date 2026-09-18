-- ARCHITECTURAL TRACE: Infrastructure Layer — Migración Flyway
CREATE TABLE eventos (
    id              UUID PRIMARY KEY,
    organizador_id  UUID NOT NULL,
    nombre          VARCHAR(255) NOT NULL,
    fecha           DATE NOT NULL,
    ubicacion       VARCHAR(255) NOT NULL
);

CREATE INDEX idx_eventos_organizador ON eventos(organizador_id);
