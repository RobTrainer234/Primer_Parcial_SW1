CREATE TABLE IF NOT EXISTS operaciones_entidad (
    id BIGSERIAL PRIMARY KEY,
    entidad_id BIGINT NOT NULL REFERENCES entidades_modelo(id) ON DELETE CASCADE,
    nombre VARCHAR(120) NOT NULL,
    tipo_retorno VARCHAR(120) NOT NULL,
    firma VARCHAR(240) NOT NULL,
    visibilidad VARCHAR(20) NOT NULL DEFAULT 'PUBLICA',
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_operaciones_entidad_visibilidad CHECK (visibilidad IN ('PUBLICA', 'PROTEGIDA', 'PRIVADA', 'PAQUETE'))
);

CREATE INDEX IF NOT EXISTS ix_operaciones_entidad_entidad ON operaciones_entidad(entidad_id, id);
