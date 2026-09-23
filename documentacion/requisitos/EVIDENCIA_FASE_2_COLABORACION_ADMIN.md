# Evidencia Fase 2 - Colaboración y administración

## Alcance demo/parcial implementado

La Fase 2 agrega una capa incremental demo/parcial compatible con los endpoints en inglés derivados de la guía, sin eliminar las rutas existentes en español ni los endpoints de compatibilidad de Fase 1.

## CU3 - Permisos de proyecto

Endpoints expuestos:

- `POST /projects/{projectId}/commands`
- `GET /projects/{projectId}/invitations`
- `GET /projects/{projectId}/members`
- `GET /projects/{projectId}/account-suggestions`
- `PATCH /projects/{projectId}/members/{userId}`
- `PUT /projects/{projectId}/members/{userId}/capabilities/create-diagram`
- `GET /projects/{projectId}/permission-history`
- `GET /projects/{projectId}/permissions/history`

Cobertura demo/parcial: miembros demo, invitaciones demo, sugerencias de cuenta, actualización de rol/capacidad e historial administrativo en memoria local por proceso. No equivale a permisos persistentes completos.

## CU4 - Diagramas/vistas y acceso

Endpoints expuestos:

- `POST /projects/{projectId}/diagrams`
- `POST /projects/{projectId}/diagrams/{viewId}/collaboration`
- `PUT /projects/{projectId}/diagrams/{viewId}/collaborators/{userId}`
- `DELETE /projects/{projectId}/diagrams/{viewId}/collaborators/{userId}`
- `PUT /projects/{projectId}/diagrams/{viewId}/administrator`
- `GET /projects/{projectId}/diagrams/{viewId}/access`
- `GET /projects/{projectId}/diagrams/{viewId}/permission-history`
- `GET /projects/{projectId}/diagrams/{viewId}/permissions/history`

Cobertura demo/parcial: creación de vista, sesión colaborativa demo, colaboradores, administrador, consulta de acceso e historial por vista en memoria local por proceso. No equivale a administración persistente completa.

## CU11 - Presencia

Endpoints expuestos:

- `POST /projects/{projectId}/sync/presence?modelId=...`
- `GET /projects/{projectId}/sync/presence?modelId=...`
- `GET /sync/events?modelId=...`
- `GET /projects/{projectId}/sync/events?modelId=...`

Cobertura: registro/listado de presencia local y publicación de evento SSE `presence` mediante el servicio SSE existente.

## CU12 - Conflictos

Endpoints expuestos:

- `GET /projects/{projectId}/models/{modelId}/sync/conflicts`
- `GET /projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}`
- `POST /projects/{projectId}/models/{modelId}/sync/conflicts/{operationId}/resolve`

Cobertura: detección mínima de conflicto cuando `POST /modelos/{modeloId}/sync/commands` recibe `payload.baseRevision` menor a la revisión actual. El conflicto queda registrado con `operationId`, puede listarse, consultarse y resolverse.

## Frontend

La pantalla Angular agrega una sección "Fase 2 · Colaboración y administración" para demostrar:

- carga de miembros, invitaciones, sugerencias e historial;
- alternar capacidad `create-diagram`;
- crear diagrama/vista y agregar colaborador demo;
- enviar/listar presencia y abrir SSE;
- crear conflicto demo por `baseRevision` obsoleta, listar y resolver.

## Limitaciones documentadas y contrato vigente

- Persistencia de CU3/CU4/CU11/CU12: memoria local por proceso. Se pierde al reiniciar el backend.
- CU3/CU4 deben etiquetarse como demo/parcial hasta implementar persistencia y autorización según `documentacion/arquitectura/CONTRATO_IDENTIDAD_PERMISOS_CU1_CU5.md`.
- No hay OAuth/JWT productivo: los usuarios demo son identificadores académicos y la identidad persistente académica es el camino aprobado.
- SSE es local a una instancia, no distribuido.
- La resolución de conflictos es explícita y registra decisión; no hace merge semántico del modelo.
- La detección automática de conflictos cubre comandos sync con `baseRevision` obsoleta; no compara operación por operación.

## Verificación

- `cd frontend-case && npm run compilar`: exitoso. Angular generó bundle de producción.
- `cd backend-case && mvn test`: bloqueado localmente porque `mvn` no está instalado (`command not found`).
- `docker version`: bloqueado localmente porque el cliente existe pero el daemon Docker Desktop/Linux no está disponible.
