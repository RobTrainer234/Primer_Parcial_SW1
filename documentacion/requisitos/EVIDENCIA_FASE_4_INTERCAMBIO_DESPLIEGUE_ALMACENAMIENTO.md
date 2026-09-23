# Evidencia Fase 4 - Intercambio, despliegue y almacenamiento

## Alcance implementado

Fase 4 cubre CU14, CU16 y CU18 con una capa incremental compatible con las fases anteriores. La intención es académica/demo: exponer contratos verificables sin declarar soporte productivo de XMI completo, infraestructura real de despliegue ni S3/Floci operativos.

## CU14 - XMI parcial

Endpoints agregados:

- `POST /projects/{projectId}/models/{modelId}/xmi/export`
- `POST /projects/{projectId}/models/{modelId}/xmi/import/preview`
- `POST /projects/{projectId}/models/{modelId}/xmi/import/confirm`

Cobertura:

- Exporta un documento XML/XMI parcial con modelo, entidades/clases, atributos y relaciones simples por nombre.
- Preview parsea el subconjunto soportado y devuelve `warnings`, `lossless=false`, `limitation` y `previewToken`.
- Confirm requiere `confirmPartialImport=true` y un `previewToken` válido.
- Confirm crea entidades/atributos faltantes y relaciones simples cuando las entidades por nombre existen o fueron importadas.

Límites declarados:

- No es lossless.
- No conserva operaciones UML, estereotipos, diagramas visuales, namespaces externos ni semántica avanzada.
- Relaciones XMI externas se importan solo si vienen como `relation/association` con `source` y `target` por nombre soportado.
- La vista previa se guarda en memoria; se pierde al reiniciar el backend.

## CU16 - Deployments

Endpoints agregados:

- `POST /projects/{projectId}/deployments`
- `GET /projects/{projectId}/deployments`
- `GET /projects/{projectId}/deployments/{deploymentId}`
- `PUT /projects/{projectId}/deployments/{deploymentId}`
- `POST /projects/{projectId}/deployments/{deploymentId}/disable`
- `POST /projects/{projectId}/deployments/{deploymentId}/enable`
- `DELETE /projects/{projectId}/deployments/{deploymentId}`

Cobertura:

- Permite crear, listar, consultar, actualizar, habilitar/deshabilitar y eliminar deployments demo por proyecto.
- Implementación en memoria con estado `DEMO_READY`/`DISABLED`.
- Valida que el proyecto exista antes de operar.

Límites declarados:

- No ejecuta infraestructura real.
- No persiste entre reinicios.
- No hace CI/CD, provisioning ni publicación de artefactos.

## CU18 - Almacenamiento

Endpoints agregados:

- `GET /storage/status`
- `GET /projects/{projectId}/storage/status`

Cobertura:

- Mantiene generación y descarga local de ZIP por `/generaciones/{id}/artefacto`.
- Expone proveedor activo por configuración `caseapp.storage.provider`, con `local` por defecto.
- Reporta S3 y Floci como opt-in placeholders mediante `caseapp.storage.s3.bucket` y `caseapp.storage.floci.endpoint`.

Límites declarados:

- No se agregó dependencia AWS pesada.
- S3/Floci no suben ni descargan artefactos reales en esta fase.
- Si falta bucket/endpoint/credenciales, el estado lo declara como no configurado.

## Frontend mínimo

Se actualizó Angular para demostrar:

- Exportación, preview y confirmación XMI parcial.
- Lista/creación/toggle/eliminación de deployments.
- Panel de estado de almacenamiento local/S3/Floci.

## Verificación

- `cd frontend-case && npm run compilar`: exitoso. Angular generó bundle en `frontend-case/dist/frontend-case`.
- `cd backend-case && mvn test`: bloqueado por entorno local: `/usr/bin/bash: line 1: mvn: command not found`.
- `docker info`: bloqueado por entorno local: cliente instalado, pero daemon no disponible (`failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine`). No hay wrapper Maven (`mvnw`) en `backend-case`.

## Brechas restantes

- XMI completo/lossless sigue fuera de alcance.
- Deployment productivo requiere persistencia, seguridad, integración CI/CD y ejecución real.
- S3/Floci requieren credenciales, servicio disponible, dependencia cliente y pruebas de integración.
