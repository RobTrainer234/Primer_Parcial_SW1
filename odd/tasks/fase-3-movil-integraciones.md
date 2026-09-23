# Fase 3 - Móvil e integraciones de entrada

Fuente principal: `GUIA DEL PROYECTO.txt`, `documentacion/requisitos/CASOS_USO_MVP.md` y `documentacion/requisitos/PLAN_TRABAJO_CU_PRIORIZADO.md`.

## Objetivo

Implementar y verificar los casos de uso CU7, CU8, CU9 y CU13: propuestas desde texto, voz/ASR local, foto/Qwen opt-in y móvil descriptor-driven con cola offline.

## Tareas

- [x] Implementar propuestas desde texto y revisión para CU7.
- [x] Implementar voz/ASR local como entrada opt-in hacia propuestas para CU8.
- [x] Implementar foto/Qwen opt-in como entrada hacia propuestas para CU9.
- [x] Implementar descriptor móvil y cola offline mínima para CU13.
- [x] Actualizar frontend y móvil para demostrar Fase 3.
- [x] Actualizar documentación/evidencia de Fase 3.
- [x] Ejecutar verificación disponible y registrar bloqueos.

## Criterios de aceptación

- [x] CU7 permite crear propuesta, consultar revisión y aceptar/rechazar sin aplicar cambios sin decisión. Evidencia: `ServicioPropuestas` marca `ACCEPTED`/`REJECTED` sin mutar el modelo.
- [x] CU8 valida contrato ASR local y crea propuesta desde transcripción o falla controladamente. Evidencia: `POST /projects/{projectId}/proposals/asr-local`, `es-ES`, 25 MiB, `ASR_UNAVAILABLE` si no conecta.
- [x] CU9 documenta y controla opt-in de foto/Qwen, sin asumir servicio real. Evidencia: `POST /projects/{projectId}/proposals/photo` crea placeholder con `QWEN_UNCONFIGURED` por defecto.
- [x] CU13 permite cargar descriptor, encolar comandos offline y preparar flush/conflictos. Evidencia: `GET /projects/{projectId}/mobile/descriptor` y Flutter `OfflineQueueService`; cola en memoria documentada como limitación.
- [x] Frontend compila. Evidencia: `cd frontend-case && npm run compilar` completó correctamente.
- [x] Flutter analiza o se registra bloqueo concreto. Evidencia: `cd movil && flutter analyze` completó sin issues.
- [x] Backend queda compilado o con bloqueo de entorno documentado. Evidencia: `cd backend-case && mvn test` falla por `mvn: command not found`; `docker version` falla por daemon no disponible.

## Estado

Completado con brechas documentadas: propuestas en memoria, ASR externo no incluido, Qwen real no integrado y cola móvil en memoria sin persistencia local.
