# Backend CASE

Backend principal de la herramienta CASE.

## Responsabilidades implementadas

- Gestión de proyectos.
- Persistencia del modelo conceptual.
- Gestión de entidades, atributos y relaciones.
- Validación básica del modelo antes de generación.
- Construcción de modelo intermedio.
- Motor determinista inicial de generación.
- Empaquetado ZIP.
- Gestión local de artefactos generados.

## Responsabilidades pendientes

- Generación avanzada de relaciones JPA completas.
- Frontend real para invocar generación.
- Pruebas automatizadas cuando Maven esté disponible.

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

### Generación

```http
POST /modelos/{modeloId}/generaciones
GET  /generaciones/{generacionId}
GET  /generaciones/{generacionId}/artefacto
```

La primera versión genera un backend Spring Boot CRUD básico. Las relaciones quedan registradas en el modelo intermedio, pero el mapeo JPA avanzado se completará en una fase posterior.

## Ejecución local

### Con Docker desde la raíz del repositorio

```bash
docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

### Con Maven local desde `backend-case/`

```bash
mvn spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Prueba rápida del flujo principal

Con el backend ejecutándose:

```bash
herramientas/scripts/probar-flujo-backend-case.sh
```

El script crea un proyecto, modelo, entidad, atributos, valida, genera y descarga un ZIP en `generados/`.
