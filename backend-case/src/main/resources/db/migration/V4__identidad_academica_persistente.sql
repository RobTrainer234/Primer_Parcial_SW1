CREATE TABLE IF NOT EXISTS usuarios_academicos (
    usuario_id VARCHAR(80) PRIMARY KEY,
    nombre_visible VARCHAR(160) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS sesiones_academicas (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    creado_en TIMESTAMP NOT NULL,
    expira_en TIMESTAMP NOT NULL,
    revocado_en TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_sesiones_academicas_token ON sesiones_academicas(token);
CREATE INDEX IF NOT EXISTS ix_sesiones_academicas_usuario ON sesiones_academicas(usuario_id);

INSERT INTO usuarios_academicos (usuario_id, nombre_visible, activo, creado_en, actualizado_en) VALUES
    ('demo-admin', 'Demo Admin', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('demo-modeler', 'Demo Modeler', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('demo-viewer', 'Demo Viewer', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('demo-reviewer', 'Demo Reviewer', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (usuario_id) DO UPDATE SET
    nombre_visible = EXCLUDED.nombre_visible,
    activo = EXCLUDED.activo,
    actualizado_en = CURRENT_TIMESTAMP;
