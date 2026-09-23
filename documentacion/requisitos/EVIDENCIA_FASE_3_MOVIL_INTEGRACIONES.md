# Evidencia Fase 3 - Móvil e integraciones de entrada

## Alcance cubierto

| CU | Cobertura implementada | Límite honesto |
|---|---|---|
| CU7 | Propuestas textuales trazables: crear, listar, consultar, revisar y decidir. El flujo manual existente se conserva y se suma `POST /projects/{projectId}/proposals/ai-text/qwen` como solicitud explícita de IA textual. | Aceptar solo marca `ACCEPTED`; no muta el modelo automáticamente. Qwen texto queda desactivado por defecto con `AI_TEXT_PROPOSALS_ENABLED=false` y sólo se invoca si además `AI_TEXT_PROPOSALS_PROVIDER=qwen`. |
| CU8 | Endpoint ASR-local hacia `http://127.0.0.1:8765/transcribe?language=es-ES`, máximo 25 MiB, creando propuesta normal si transcribe o propuesta/estado controlado si no está disponible. | No se incluye servicio `faster-whisper`; debe estar levantado fuera del backend. |
| CU9 | Endpoint foto/Qwen opt-in crea propuesta placeholder trazable con `QWEN_UNCONFIGURED` por defecto. | No se afirma disponibilidad real de Qwen; la bandera `CU12_PHOTO_QWEN_ENABLED` solo deja estado opt-in pendiente. |
| CU13 | Descriptor móvil derivado del primer modelo del proyecto y cola offline en Flutter con flush a `/modelos/{modeloId}/sync/commands`. | La cola es en memoria para evitar nueva dependencia; se pierde al cerrar la app. |

## Endpoints agregados

- `POST /projects/{projectId}/proposals`
- `POST /projects/{projectId}/proposals/ai-text/qwen`
- `GET /projects/{projectId}/proposals`
- `GET /projects/{projectId}/proposals/{proposalId}`
- `GET /projects/{projectId}/proposals/{proposalId}/review`
- `POST /projects/{projectId}/proposals/{proposalId}/review-decision`
- `POST /projects/{projectId}/proposals/asr-local`
- `POST /projects/{projectId}/proposals/photo`
- `GET /projects/{projectId}/mobile/descriptor`

## Decisiones de seguridad funcional

- Las propuestas nunca modifican el modelo antes de una decisión explícita.
- En esta fase, una decisión `ACCEPTED` solo cambia el estado de la propuesta; no aplica mutación demo para no introducir cambios semánticos ocultos.
- `REJECTED` conserva trazabilidad y deja el modelo sin cambios.
- Qwen texto es opt-in doble: requiere acción explícita en Angular y configuración `AI_TEXT_PROPOSALS_ENABLED=true` con `AI_TEXT_PROPOSALS_PROVIDER=qwen`.
- Timeouts, HTTP no exitoso, respuesta inválida y límites de tamaño generan propuestas trazables con `sourceStatus` controlado en lugar de mutar el modelo.
- ASR y foto/Qwen registran fuente y estado para no confundir placeholder con integración real.
- Flutter conserva el CRUD genérico existente y agrega descriptor/cola como capacidad incremental.

## Verificación ejecutada

| Comando | Resultado |
|---|---|
| `cd frontend-case && npm run compilar` | Correcto. Angular generó bundle de producción en `frontend-case/dist/frontend-case`. |
| `cd movil && flutter analyze` | Correcto. Flutter reportó `No issues found`. |
| `mvn -f backend-case/pom.xml test` | Bloqueado por entorno: `mvn: command not found`. |
| `docker version` | Bloqueado por entorno: cliente Docker disponible, pero daemon Docker Desktop/Linux no está corriendo. |

Backend queda pendiente de compilación/ejecución hasta disponer de Maven o Docker daemon activo. La evidencia de esta fase se limita a revisión estática backend más verificación ejecutable de frontend y Flutter.
