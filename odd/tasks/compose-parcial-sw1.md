# Seguimiento ODD: Compose Parcial_SW1

## Objetivo
Unificar frontend Angular, backend Spring Boot y PostgreSQL en un único proyecto Docker Compose llamado `Parcial_SW1`.

## Alcance
- `frontend-case` como servicio contenedorizado.
- `backend-case` como servicio contenedorizado.
- `base-datos-case` como PostgreSQL.
- Red interna y dependencias saludables.
- Qwen accesible desde el backend mediante Docker Model Runner del host.
- Volúmenes existentes preservados.

## No alcance
- No eliminar datos de PostgreSQL.
- No incorporar Qwen como cuarto servicio dentro del Compose.
- No cambiar contratos REST.
- No hacer commit.

## Criterios de aceptación
1. `docker compose -p parcial_sw1 ... up --build -d` levanta los tres servicios; Docker Compose exige nombres de proyecto en minúscula.
2. Frontend responde en `localhost:4200`.
3. Backend responde health en `localhost:8080`.
4. PostgreSQL queda healthy en `localhost:5432`.
5. Backend puede resolver PostgreSQL por nombre de servicio.
6. Qwen continúa accesible por `host.docker.internal:12434`.

## Implementación
- Agregado `frontend-case/Dockerfile` con build multi-stage: Node 20 compila Angular en modo production y nginx sirve los archivos estáticos.
- Agregado `frontend-case/nginx.conf` para servir la SPA con fallback a `index.html` y cachear assets versionados.
- Actualizado `infraestructura/local/docker-compose.yml` para declarar el proyecto `Parcial_SW1` y levantar `frontend-case`, `backend-case` y `base-datos-case` en el mismo Compose.
- Preservados y enlazados explícitamente los volúmenes existentes `local_datos_case` y `local_artefactos_case`; los volúmenes temporales `parcial_sw1_*` quedaron sin usar.
- Backend sigue usando PostgreSQL por nombre interno `base-datos-case`.
- Qwen queda fuera de Compose y se expone al backend por `http://host.docker.internal:12434/engines/v1/chat/completions` con `host-gateway` para Linux.
- IA queda deshabilitada por defecto mediante `AI_TEXT_PROPOSALS_ENABLED=false` y `CU12_PHOTO_QWEN_ENABLED=false`; se habilita solo si el entorno define otra cosa.
- Frontend queda publicado en `localhost:4200`; la URL Angular existente `http://localhost:8080` funciona para el navegador local porque Compose publica el backend en el host en ese puerto, sin cambiar contratos REST.
- Healthcheck agregado a PostgreSQL y frontend. El healthcheck frontend usa `127.0.0.1` para evitar resolución IPv6 en nginx. Backend conserva dependencia saludable de PostgreSQL; no se agregó healthcheck propio porque la imagen runtime actual no incluye una herramienta HTTP garantizada sin cambiar el Dockerfile del backend.

## Ejecución local

Desde la raíz del repositorio:

```bash
docker compose -f infraestructura/local/docker-compose.yml up --build -d
```

Accesos:

```text
frontend: http://localhost:4200
backend:  http://localhost:8080
swagger:  http://localhost:8080/swagger-ui.html
postgres: jdbc:postgresql://localhost:5432/proyecto_case
```

Para habilitar propuestas de texto con Qwen en Docker Model Runner del host:

```bash
AI_TEXT_PROPOSALS_ENABLED=true docker compose -f infraestructura/local/docker-compose.yml up --build -d
```
