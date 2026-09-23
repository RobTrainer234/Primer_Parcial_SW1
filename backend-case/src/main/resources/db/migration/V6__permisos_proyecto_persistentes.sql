CREATE TABLE IF NOT EXISTS miembros_proyecto (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    nombre_visible VARCHAR(160) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    crear_diagrama BOOLEAN NOT NULL DEFAULT FALSE,
    editar_modelo BOOLEAN NOT NULL DEFAULT FALSE,
    gestionar_miembros BOOLEAN NOT NULL DEFAULT FALSE,
    gestionar_permisos BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL,
    CONSTRAINT ck_miembros_proyecto_role CHECK (role IN ('OWNER', 'ADMIN', 'EDITOR', 'VIEWER')),
    CONSTRAINT ck_miembros_proyecto_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REVOKED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_miembros_proyecto_usuario ON miembros_proyecto (proyecto_id, LOWER(usuario_id));
CREATE INDEX IF NOT EXISTS ix_miembros_proyecto_proyecto ON miembros_proyecto(proyecto_id);

CREATE TABLE IF NOT EXISTS invitaciones_proyecto (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    invitado_usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    invitador_usuario_id VARCHAR(80) NOT NULL REFERENCES usuarios_academicos(usuario_id),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expira_en TIMESTAMP NOT NULL,
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL,
    CONSTRAINT ck_invitaciones_proyecto_role CHECK (role IN ('OWNER', 'ADMIN', 'EDITOR', 'VIEWER')),
    CONSTRAINT ck_invitaciones_proyecto_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'REVOKED', 'EXPIRED'))
);

CREATE INDEX IF NOT EXISTS ix_invitaciones_proyecto_proyecto ON invitaciones_proyecto(proyecto_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_invitaciones_proyecto_pendiente ON invitaciones_proyecto (proyecto_id, LOWER(invitado_usuario_id)) WHERE status = 'PENDING';

CREATE TABLE IF NOT EXISTS eventos_permiso_proyecto (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    actor_usuario_id VARCHAR(80) NOT NULL,
    action VARCHAR(80) NOT NULL,
    target VARCHAR(160),
    antes TEXT,
    despues TEXT,
    operacion_id VARCHAR(80),
    ocurrido_en TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_eventos_permiso_proyecto ON eventos_permiso_proyecto(proyecto_id, ocurrido_en DESC);
CREATE INDEX IF NOT EXISTS ix_eventos_permiso_proyecto_target ON eventos_permiso_proyecto(proyecto_id, action, target);

INSERT INTO miembros_proyecto (proyecto_id, usuario_id, nombre_visible, role, status, crear_diagrama, editar_modelo, gestionar_miembros, gestionar_permisos, creado_en, actualizado_en)
SELECT p.id, 'demo-admin', u.nombre_visible, 'OWNER', 'ACTIVE', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM proyectos p
JOIN usuarios_academicos u ON u.usuario_id = 'demo-admin'
ON CONFLICT DO NOTHING;

INSERT INTO miembros_proyecto (proyecto_id, usuario_id, nombre_visible, role, status, crear_diagrama, editar_modelo, gestionar_miembros, gestionar_permisos, creado_en, actualizado_en)
SELECT p.id, p.owner_user_id, COALESCE(u.nombre_visible, p.owner_user_id), 'OWNER', 'ACTIVE', TRUE, TRUE, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM proyectos p
LEFT JOIN usuarios_academicos u ON LOWER(u.usuario_id) = LOWER(p.owner_user_id)
WHERE p.owner_user_id IS NOT NULL
ON CONFLICT DO NOTHING;
