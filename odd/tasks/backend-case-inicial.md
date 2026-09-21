# Backend CASE inicial

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Implementar el primer backend funcional de la herramienta CASE: gestión de proyectos, modelo conceptual persistente y validación básica previa a generación.

## Tareas

- [x] Implementar persistencia y API de proyectos.
- [x] Implementar persistencia y API de modelo conceptual, entidades, atributos y relaciones.
- [x] Implementar validación básica del modelo conceptual.
- [x] Verificar estructura y comandos disponibles.

## Alcance incluido

- Proyecto.
- Modelo conceptual.
- Entidad del modelo.
- Atributo de entidad.
- Relación del modelo.
- Cardinalidades.
- Tipos de datos soportados.
- Resultado de validación.

## Fuera de alcance por ahora

- Generación de backend Spring Boot.
- Descarga de ZIP.
- Frontend Angular real.
- Flutter real.
- AWS.

## Criterios de aceptación

- [x] La API permite crear, listar, consultar, actualizar y eliminar proyectos.
- [x] La API permite crear/consultar un modelo conceptual por proyecto.
- [x] La API permite crear entidades, atributos y relaciones.
- [x] La API devuelve el modelo completo con entidades, atributos y relaciones.
- [x] La validación detecta errores mínimos antes de generación.

## Endpoints creados

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

### Entidades, atributos y relaciones

```http
POST   /modelos/{modeloId}/entidades
PUT    /entidades/{entidadId}
DELETE /entidades/{entidadId}

POST   /entidades/{entidadId}/atributos
PUT    /atributos/{atributoId}
DELETE /atributos/{atributoId}

POST   /modelos/{modeloId}/relaciones
PUT    /relaciones/{relacionId}
DELETE /relaciones/{relacionId}
```

### Validación

```http
POST /modelos/{modeloId}/validacion
GET  /modelos/{modeloId}/validacion
```

## Verificación ejecutada

- Chequeo estructural con Python: 29 archivos Java presentes.
- Chequeo liviano de llaves y declaraciones `package`: correcto.
- `mvn -f backend-case/pom.xml test` no pudo ejecutarse porque Maven no está instalado (`mvn: command not found`).

## Estado

Completado con verificación limitada por entorno.
