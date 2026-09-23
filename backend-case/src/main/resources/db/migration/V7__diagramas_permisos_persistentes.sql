CREATE TABLE IF NOT EXISTS diagramas_proyecto (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    nombre VARCHAR(160) NOT NULL,
    administrador_usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    colaboracion_habilitada BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL,
    CONSTRAINT ck_diagramas_proyecto_estado CHECK (estado IN ('ACTIVE', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS ix_diagramas_proyecto_proyecto ON diagramas_proyecto(proyecto_id, estado);

CREATE TABLE IF NOT EXISTS colaboradores_diagrama (
    id BIGSERIAL PRIMARY KEY,
    diagrama_id BIGINT NOT NULL REFERENCES diagramas_proyecto(id) ON DELETE CASCADE,
    usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    role VARCHAR(30) NOT NULL,
    puede_editar BOOLEAN NOT NULL DEFAULT FALSE,
    puede_comentar BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_colaboradores_diagrama_usuario ON colaboradores_diagrama (diagrama_id, LOWER(usuario_id));
CREATE INDEX IF NOT EXISTS ix_colaboradores_diagrama_diagrama ON colaboradores_diagrama(diagrama_id);

CREATE TABLE IF NOT EXISTS eventos_permiso_diagrama (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    diagrama_id BIGINT NOT NULL REFERENCES diagramas_proyecto(id) ON DELETE CASCADE,
    actor_usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    action VARCHAR(80) NOT NULL,
    target VARCHAR(160),
    antes TEXT,
    despues TEXT,
    operacion_id VARCHAR(80),
    ocurrido_en TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_eventos_permiso_diagrama ON eventos_permiso_diagrama(proyecto_id, diagrama_id, ocurrido_en DESC);
CREATE INDEX IF NOT EXISTS ix_eventos_permiso_diagrama_target ON eventos_permiso_diagrama(proyecto_id, diagrama_id, action, target);
