# Evidencia de Fase 1 - Ruta crítica demostrable

## Alcance implementado

Esta fase agrega una capa incremental de compatibilidad para CU1, CU2, CU5, CU6, CU10 y CU15 sin reemplazar los endpoints existentes en español (`/proyectos`, `/modelos`, `/validacion`, `/generaciones`).

## Cobertura por CU

| CU | Cobertura | Evidencia funcional |
|---|---|---|
| CU1 | Login académico/demo | `POST /auth/login`, `POST /auth/demo-login`, `POST /auth/logout`. La respuesta documenta que `X-User-Id` y `tokenDemo` son mecanismos de demo, no autenticación productiva. |
| CU2 | Recuperación de proyecto/modelo/versiones | Alias compatibles: `GET/POST /projects`, `GET /projects/{id}`, `GET /projects/{id}/model`, `GET /projects/{id}/versions`, `GET /projects/{id}/versions/{revision}`. Se preservan `/proyectos`. |
| CU5 | UML semántico mínimo | Se conserva el modelo de entidades, atributos y relaciones. La respuesta completa del modelo incluye `semantica: UML-CLASES-MINIMO` y cada entidad expone `operaciones: []` como puente compatible. |
| CU6 | Layout visual mínimo | Las posiciones `posicionX/posicionY` existentes siguen siendo la fuente de layout. `GET /modelos/{modeloId}/sync/snapshot` separa `layout` del modelo semántico para demo. |
| CU10 | Sincronización HTTP + snapshot + SSE | `GET /modelos/{modeloId}/sync/snapshot`, `POST /modelos/{modeloId}/sync/commands`, `GET /modelos/{modeloId}/sync/events` con `text/event-stream`. El stream emite snapshot inicial y cambios de modelo en proceso. |
| CU15 | Targets/perfiles/runs/artefactos | `GET /generation/targets`, `GET /generation/profiles`, `POST /generation/runs`, `GET /generation/runs/{id}`, `GET /generation/runs/{id}/artifacts`. Los endpoints previos de generación y descarga se preservan y la respuesta incluye target, perfil y SHA-256 cuando hay artefacto. |

## Frontend

La pantalla Angular mantiene el flujo anterior y agrega controles mínimos para:

- iniciar/cerrar sesión demo;
- consultar snapshot/layout;
- enviar un comando de sincronización de demo;
- mostrar target, perfil y hash de artefacto de generación.

## Verificación ejecutada

| Comando | Resultado |
|---|---|
| `cd frontend-case && npm run compilar` | Correcto. Angular generó el bundle en `frontend-case/dist/frontend-case`. |
| `mvn -f backend-case/pom.xml test` | No ejecutado por entorno: `mvn: command not found`. |
| `docker compose -f infraestructura/local/docker-compose.yml build backend-case` | No ejecutado por entorno: Docker daemon/Desktop no está corriendo. |

## Límites explícitos

- No se implementa autenticación productiva ni autorización real; el mecanismo es solo académico/demo.
- Las versiones se exponen como compatibilidad sobre la revisión actual del modelo; no hay historial persistente completo todavía.
- Las operaciones UML se exponen como lista vacía compatible; no hay edición persistente de operaciones.
- SSE es in-process; no coordina múltiples instancias del backend.
- La compilación backend queda pendiente hasta disponer de Maven o Docker Desktop activo.
