CREATE TABLE proyectos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(1000),
    estado VARCHAR(30) NOT NULL,
    creado_en TIMESTAMP NOT NULL,
    actualizado_en TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ux_proyectos_nombre_lower ON proyectos (LOWER(nombre));

CREATE TABLE modelos_conceptuales (
    id BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    nombre VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE entidades_modelo (
    id BIGSERIAL PRIMARY KEY,
    modelo_id BIGINT NOT NULL REFERENCES modelos_conceptuales(id) ON DELETE CASCADE,
    nombre VARCHAR(120) NOT NULL,
    posicion_x INTEGER NOT NULL,
    posicion_y INTEGER NOT NULL
);

CREATE TABLE atributos_entidad (
    id BIGSERIAL PRIMARY KEY,
    entidad_id BIGINT NOT NULL REFERENCES entidades_modelo(id) ON DELETE CASCADE,
    nombre VARCHAR(120) NOT NULL,
    tipo_dato VARCHAR(40) NOT NULL,
    clave_primaria BOOLEAN NOT NULL,
    obligatorio BOOLEAN NOT NULL,
    valor_unico BOOLEAN NOT NULL
);

CREATE TABLE relaciones_modelo (
    id BIGSERIAL PRIMARY KEY,
    modelo_id BIGINT NOT NULL REFERENCES modelos_conceptuales(id) ON DELETE CASCADE,
    entidad_origen_id BIGINT NOT NULL REFERENCES entidades_modelo(id) ON DELETE CASCADE,
    entidad_destino_id BIGINT NOT NULL REFERENCES entidades_modelo(id) ON DELETE CASCADE,
    nombre VARCHAR(120) NOT NULL,
    cardinalidad_origen VARCHAR(20) NOT NULL,
    cardinalidad_destino VARCHAR(20) NOT NULL
);

CREATE TABLE trabajos_generacion (
    id BIGSERIAL PRIMARY KEY,
    modelo_id BIGINT NOT NULL REFERENCES modelos_conceptuales(id) ON DELETE CASCADE,
    estado VARCHAR(40) NOT NULL,
    iniciado_en TIMESTAMP,
    finalizado_en TIMESTAMP,
    mensaje_error VARCHAR(2000)
);

CREATE TABLE artefactos_generados (
    id BIGSERIAL PRIMARY KEY,
    trabajo_generacion_id BIGINT NOT NULL REFERENCES trabajos_generacion(id) ON DELETE CASCADE,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(1000) NOT NULL,
    creado_en TIMESTAMP NOT NULL
);
