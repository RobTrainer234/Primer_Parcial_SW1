# Fase 4 - Intercambio, despliegue y almacenamiento

Fuente principal: `GUIA DEL PROYECTO.txt`, `documentacion/requisitos/CASOS_USO_MVP.md` y `documentacion/requisitos/PLAN_TRABAJO_CU_PRIORIZADO.md`.

## Objetivo

Implementar y verificar los casos de uso CU14, CU16 y CU18: XMI parcial con preview/confirmación, deployments y almacenamiento local/S3/Floci opt-in.

## Tareas

- [x] Implementar exportación/importación XMI parcial con preview y confirmación para CU14.
- [x] Implementar deployments demo para CU16.
- [x] Implementar estado/configuración de almacenamiento local/S3/Floci opt-in para CU18.
- [x] Actualizar frontend mínimo para demostrar Fase 4.
- [x] Actualizar documentación/evidencia de Fase 4.
- [x] Ejecutar verificación disponible y registrar bloqueos.

## Criterios de aceptación

- [x] CU14 permite exportar XMI, previsualizar importación y confirmar, declarando que no es lossless.
- [x] CU16 permite crear/listar/consultar/actualizar/habilitar/deshabilitar/eliminar deployment demo.
- [x] CU18 expone proveedor local por defecto y S3/Floci como opt-in no garantizado.
- [x] Frontend compila.
- [x] Backend queda compilado o con bloqueo de entorno documentado.

## Estado

Completado con verificación disponible. Frontend compila; backend queda bloqueado localmente porque `mvn` no está instalado y no existe `mvnw`. Evidencia documental: `documentacion/requisitos/EVIDENCIA_FASE_4_INTERCAMBIO_DESPLIEGUE_ALMACENAMIENTO.md`.
