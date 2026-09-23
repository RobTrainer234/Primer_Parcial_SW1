# Resultado de pruebas locales CU1-CU18

Fecha de ejecución: 2026-09-21

## Entorno

- PostgreSQL 16 en Docker: `base-datos-case`, puerto `5432`, healthy.
- Backend Spring Boot: `backend-case`, puerto `8080`, health `UP`.
- Frontend Angular: `http://localhost:4200`.
- Docker Model Runner: `http://127.0.0.1:12434`.
- Modelo Qwen: `hf.co/ggml-org/Qwen3.5-0.8B-GGUF:Q4_0`.

## Matriz ejecutada

| CU | Resultado local | Evidencia / límite |
|---|---|---|
| CU1 | Parcial — OK demo | `POST /auth/demo-login` respondió 200. Es autenticación demo, no productiva. |
| CU2 | OK local/demo | Verificación aislada del 2026-09-21: `POST /proyectos` creó el proyecto de prueba `id=4`; `GET /proyectos`, `GET /proyectos/4`, `GET /proyectos/4/modelo`, `GET /proyectos/4/modelos`, `GET /modelos/4`, `GET /projects`, `GET /projects/4`, `GET /projects/4/model`, `GET /projects/4/versions` y `GET /projects/4/versions/1` respondieron correctamente. Limitación: la versión compatible mantiene `creadoEn=1970-01-01T00:00:00Z`; el proyecto de prueba no fue eliminado. |
| CU3 | Demo/parcial | Miembros, invitaciones, sugerencias e historial respondieron 200. Estado en memoria; no prueba persistencia ni autorización productiva. |
| CU4 | Demo/parcial | Creación de diagrama, acceso, colaborador e historial respondieron 200/201. Estado en memoria; no prueba persistencia ni autorización productiva. |
| CU5 | Parcial | Entidades, atributos y relaciones se crearon y consultaron. Las operaciones UML todavía no están implementadas. |
| CU6 | Parcial | Posiciones `posicionX/posicionY` se conservaron en entidades y snapshot. Atajos avanzados no fueron probados como garantía completa. |
| CU7 | OK demo | Propuesta manual y propuesta Qwen creadas; revisión y decisión `ACCEPTED` respondieron correctamente. La aceptación no muta el modelo. |
| CU8 | Pendiente opt-in | El contrato ASR existe, pero no se levantó el sidecar `faster-whisper` en esta ejecución. |
| CU9 | OK placeholder | `/proposals/photo` respondió con `QWEN_UNCONFIGURED`; no se invocó extracción fotográfica real. |
| CU10 | OK demo | Snapshot y comando de sincronización respondieron 200. Eventos SSE no se midieron en esta corrida. |
| CU11 | OK demo | Presencia se publicó y listó correctamente. Estado local/no distribuido. |
| CU12 | OK demo | Se generó y consultó conflicto por `baseRevision` obsoleta. Merge semántico no implementado. |
| CU13 | Parcial | Descriptor móvil respondió 200. `flutter analyze` pasó; no existe `movil/test`, por lo que no hubo pruebas Flutter automatizadas. Cola offline continúa en memoria. |
| CU14 | OK parcial | Exportación XMI y preview respondieron 200. XMI continúa declarado parcial/no lossless. |
| CU15 | OK demo | Tras agregar clave primaria válida, generación terminó `COMPLETADO` y produjo `artifactHash`: `f66571015fc4304700f2b7331e532868861de2ea3a0ed63e084b0a60ef84f798`. |
| CU16 | OK demo | Deployment demo se creó con `DEMO_READY`; no ejecuta infraestructura real. |
| CU17 | OK | `/actuator/health` respondió `{"status":"UP"}`. |
| CU18 | OK local | `/projects/3/storage/status` respondió proveedor `local` disponible. S3/Floci no fueron probados. |

## Verificaciones de clientes

- `cd frontend-case && npm run compilar`: correcto.
- `curl.exe http://localhost:4200/`: correcto.
- `cd movil && flutter analyze`: correcto.
- `cd movil && flutter test`: bloqueado porque no existe el directorio `movil/test`.

## Bloqueos y correcciones

- Maven no está instalado en el host, pero el Dockerfile compila el backend mediante Maven dentro de la imagen.
- La reconstrucción inicial detectó en `ServicioXmi.java` llamadas a `vista.entidades()` y `vista.relaciones()` inexistentes para un record cuyos componentes son `entities` y `relations`. Se corrigieron esas dos llamadas para permitir la compilación.
- Para que el backend Docker acceda a Qwen en la PC se usó `AI_QWEN_ENDPOINT=http://host.docker.internal:12434/engines/v1/chat/completions`.
- El backend se ejecutó con `AI_TEXT_PROPOSALS_ENABLED=true` y `AI_TEXT_PROPOSALS_PROVIDER=qwen` sólo para esta prueba local; la configuración base del proyecto permanece desactivada por defecto.

## Nota de Fase 0

CU3 y CU4 no deben comunicarse como completos: la evidencia local valida flujos demo/parciales en memoria. La persistencia y autorización académica quedan congeladas en `documentacion/arquitectura/CONTRATO_IDENTIDAD_PERMISOS_CU1_CU5.md`.
