# Backend CASE

Backend principal de la herramienta CASE.

## Responsabilidades implementadas

- Gestión de proyectos.
- Persistencia del modelo conceptual.
- Gestión de entidades, atributos y relaciones.
- Validación básica del modelo antes de generación.

## Responsabilidades pendientes

- Construcción de modelo intermedio.
- Motor determinista de generación.
- Empaquetado ZIP.
- Gestión real de artefactos generados.

## Arquitectura interna

```text
compartido/      Código común, errores y excepciones.
proyectos/       Gestión de proyectos.
modelado/        Modelos, entidades, atributos y relaciones.
validacion/      Reglas para validar el modelo.
generacion/      Motor determinista de generación pendiente.
artefactos/      ZIP generados y descargas pendiente.
configuracion/   Configuración del backend.
```

## Endpoints iniciales

### Proyectos

```http
POST   /proyectos
GET    /proyectos
GET    /proyectos/{id}
PUT    /proyectos/{id}
DELETE /proyectos/{id}
```

### Modelos

```http
POST /proyectos/{proyectoId}/modelos
GET  /proyectos/{proyectoId}/modelos
GET  /proyectos/{proyectoId}/modelo
GET  /modelos/{modeloId}
```

### Entidades y atributos

```http
POST   /modelos/{modeloId}/entidades
PUT    /entidades/{entidadId}
DELETE /entidades/{entidadId}

POST   /entidades/{entidadId}/atributos
PUT    /atributos/{atributoId}
DELETE /atributos/{atributoId}
```

### Relaciones

```http
POST   /modelos/{modeloId}/relaciones
PUT    /relaciones/{relacionId}
DELETE /relaciones/{relacionId}
```

### Validación

```http
POST /modelos/{modeloId}/validacion
GET  /modelos/{modeloId}/validacion
```

## Ejecución local

Levantar PostgreSQL:

```bash
docker compose -f ../infraestructura/local/docker-compose.yml up -d
```

Ejecutar backend:

```bash
mvn spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```
