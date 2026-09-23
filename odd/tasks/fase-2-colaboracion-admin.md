# Fase 2 - Administración y colaboración

Fuente principal: `GUIA DEL PROYECTO.txt`, `documentacion/requisitos/CASOS_USO_MVP.md` y `documentacion/requisitos/PLAN_TRABAJO_CU_PRIORIZADO.md`.

## Objetivo

Implementar y verificar una primera capa demo/parcial de CU3, CU4, CU11 y CU12: permisos de proyecto, diagramas/vistas, presencia y conflictos. CU3/CU4 no se consideran completos hasta persistir identidad/permisos e imponer autorización según el contrato de Fase 0.

## Tareas

- [x] Implementar demo parcial de permisos de proyecto: invitaciones, miembros, capacidades e historial para CU3.
  - Evidencia: `ServicioColaboracion` y `ControladorProjectsCompatibilidad` exponen endpoints `/projects/{projectId}/members`, `/invitations`, `/account-suggestions`, capacidades e historial; el estado es en memoria local por proceso.
- [x] Implementar demo parcial de diagramas/vistas y permisos de diagrama para CU4.
  - Evidencia: endpoints `/projects/{projectId}/diagrams...` crean vistas, colaboración, colaboradores, administrador, acceso e historial; el estado es en memoria local por proceso.
- [x] Implementar presencia por HTTP + SSE para CU11.
  - Evidencia: `POST/GET /projects/{projectId}/sync/presence?modelId=...` y SSE compatible publican evento `presence`.
- [x] Implementar conflictos mínimos de sincronización para CU12.
  - Evidencia: comandos sync con `payload.baseRevision` obsoleta registran conflictos listables/resolubles por `/projects/{projectId}/models/{modelId}/sync/conflicts`.
- [x] Actualizar frontend mínimo para demostrar administración/colaboración.
  - Evidencia: sección Angular "Fase 2 · Colaboración y administración".
- [x] Actualizar documentación/evidencia de Fase 2.
  - Evidencia: `documentacion/requisitos/EVIDENCIA_FASE_2_COLABORACION_ADMIN.md` y plan priorizado actualizados.
- [x] Ejecutar verificación disponible y registrar bloqueos.
  - Evidencia: frontend compila; backend Maven y Docker daemon bloqueados localmente.

## Criterios de aceptación

- [x] CU3 expone endpoints demo de miembros, invitaciones, capacidades e historial.
  - Pendiente para completo: persistencia y autorización del contrato Fase 0.
- [x] CU4 expone endpoints demo de diagramas/vistas, colaboradores, administrador, acceso e historial.
  - Pendiente para completo: persistencia y autorización del contrato Fase 0.
- [x] CU11 permite publicar presencia y emitir/consultar presencia sin modificar semántica.
- [x] CU12 permite listar, consultar y resolver conflictos mínimos.
- [x] Se preserva la ruta crítica de Fase 1.
  - Nota: no se removieron endpoints existentes en español ni compatibilidad `/projects` de Fase 1.
- [x] Frontend compila.
- [ ] Backend queda compilado o con bloqueo de entorno documentado.
  - Bloqueado: `mvn` no está instalado; Docker daemon no está disponible.

## Estado

Demo/parcial implementado con verificación parcial por entorno. CU3/CU4 permanecen en memoria y no deben reclamarse como completos hasta implementar persistencia y autorización académica conforme a `documentacion/arquitectura/CONTRATO_IDENTIDAD_PERMISOS_CU1_CU5.md`.
