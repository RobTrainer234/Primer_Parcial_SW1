# Matriz actores, casos de uso y módulos CU1-CU18

## Propósito

Esta matriz complementa el catálogo `CASOS_USO_MVP.md`, la sección copy-ready `SECCION_CASOS_USO_PARA_WORD.md` y el diagrama `DIAGRAMA_CASOS_USO.md`. Su objetivo es relacionar actores, cobertura de CU, módulos/subsistemas, fase de trabajo, prioridad y evidencia necesaria.

## Leyenda

- **P:** actor principal o iniciador.
- **S:** actor secundario, servicio o cliente participante.
- **-:** sin asociación directa.

## Actores x CU

| CU | Usuario autenticado | Usuario demo | Admin proyecto | Admin diagrama | Modelador / colaborador | Cliente web | Cliente móvil | ASR local | Qwen / extractor | Generador | Operador técnico / evaluador |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| CU1 | P | P | - | - | - | - | - | - | - | - | - |
| CU2 | P | - | - | - | - | - | - | - | - | - | - |
| CU3 | - | - | P | - | - | - | - | - | - | - | - |
| CU4 | - | - | - | P | S | - | - | - | - | - | - |
| CU5 | - | - | - | - | P | - | - | - | - | - | - |
| CU6 | - | - | - | - | P | S | - | - | - | - | - |
| CU7 | - | - | - | - | P | - | - | - | - | - | - |
| CU8 | - | - | - | - | P | - | - | S | - | - | - |
| CU9 | - | - | - | - | P | - | - | - | S | - | - |
| CU10 | - | - | - | - | S | P | P | - | - | - | - |
| CU11 | - | - | - | - | P | S | S | - | - | - | - |
| CU12 | - | - | - | - | P | - | S | - | - | - | - |
| CU13 | - | - | - | - | S | - | P | - | - | - | - |
| CU14 | - | - | - | - | P | - | - | - | - | - | - |
| CU15 | - | - | - | - | P | - | - | - | - | S | - |
| CU16 | - | - | - | - | S | - | - | - | - | - | P |
| CU17 | - | - | - | - | - | - | - | - | - | - | P |
| CU18 | - | - | - | - | - | - | - | - | - | - | P |

## CU -> módulo/subsistema

| CU | Módulo/subsistema principal | Superficies o componentes relacionados | Nota de documentación/evidencia |
|---|---|---|---|
| CU1 | Autenticación y sesión | `/auth/login`, `/auth/logout`, `/auth/demo-login`; configuración `SEED_DEMO_USERS` | Documentar que `X-User-Id` es bootstrap, no autenticación productiva. |
| CU2 | Proyectos, modelos y versiones | `/projects`, `/projects/{projectId}/model`, `/versions` | Evidenciar creación/listado/recuperación y separación semántica-layout. |
| CU3 | Permisos de proyecto | Invitaciones, miembros, capacidades, historial | Evidenciar autorización y auditoría; no depender sólo de cabecera cliente. |
| CU4 | Diagramas y permisos de vista | Diagramas, colaboración, colaboradores, administrador, acceso | Evidenciar creación de vista y acceso efectivo. |
| CU5 | Modelo semántico UML | Clases, atributos, operaciones, relaciones, extremos; sync commands | Evidenciar que el cambio semántico es recuperable/versionable. |
| CU6 | Layout web y geometría | Nodos, rutas, atajos web, reportes de geometría | Evidenciar que mover figuras no cambia semántica UML. |
| CU7 | Propuestas textuales | `/proposals`, `/review`, `/review-decision`; `AI_TEXT_PROPOSALS_*` | Aclarar IA textual desactivada por defecto y revisión obligatoria. |
| CU8 | ASR local y propuestas | `tools/local-asr`, `faster-whisper`, `es-ES`, 25 MiB | Evidenciar transcripción sólo si el sidecar está disponible; no prometer multilenguaje. |
| CU9 | Foto/Qwen y propuestas | `/proposals/photo`, `CU12_PHOTO_*`, `AI_QWEN_*` | Documentar opt-in, límites, timeouts y ausencia de garantía de servicio real. |
| CU10 | Sincronización colaborativa | HTTP commands, snapshots, `text/event-stream` SSE | Mantener restricción HTTP+SSE; no WebSocket. |
| CU11 | Presencia | `/sync/presence`, SSE de presencia | Evidenciar presencia 5/5 si se cita; presencia no modifica modelo. |
| CU12 | Conflictos de sincronización | `/sync/conflicts`, `/resolve`, flush móvil/offline | Evidenciar resolución explícita, no confirmación automática. |
| CU13 | Cliente móvil Flutter | Descriptor-driven, cola offline, `PLATFORM_BASE_URL` | Distinguir prueba local Flutter de integración live. |
| CU14 | XMI | `/xmi/export`, `/xmi/import/preview`, `/xmi/import/confirm` | Documentar XMI parcial, no lossless, Q9 abierto. |
| CU15 | Generación determinista | Targets, profiles, runs, artifacts, `artifactZipHash` | Evidencia acotada: dos ejecuciones con mismo hash, no cobertura universal. |
| CU16 | Deployments | `/deployments` CRUD/estado | Verificar flujo antes de afirmar garantías productivas. |
| CU17 | Salud y evidencia técnica | `/actuator/health`, comandos backend/web/móvil, reportes E2E | Registrar entorno y resultado; no inventar detalles de health. |
| CU18 | Almacenamiento de artefactos | PostgreSQL por defecto, S3, Floci, variables `GENERATION_ARTIFACTS_*` | S3/Floci opt-in; Floci local no prueba AWS real. |

## CU -> fase/prioridad

| CU | Prioridad del catálogo | Fase de trabajo recomendada | Evidencia/documentación pendiente |
|---|---|---|---|
| CU1 | MVP-crítico | Fase 1: ruta crítica demostrable | Confirmar login real o demo y registrar restricción de `X-User-Id`. |
| CU2 | MVP-crítico | Fase 1: ruta crítica demostrable | Verificar creación/listado/recuperación. |
| CU3 | Requerido por guía | Fase 2: administración y colaboración | Verificar invitaciones, miembros, capacidades e historial. |
| CU4 | Requerido por guía | Fase 2: administración y colaboración | Verificar permisos de diagrama y acceso efectivo. |
| CU5 | MVP-crítico | Fase 1: ruta crítica demostrable | Verificar edición semántica UML. |
| CU6 | MVP-crítico | Fase 1: ruta crítica demostrable | Verificar layout y atajos web. |
| CU7 | Requerido por guía / opt-in | Fase 3: móvil e integraciones de entrada | Documentar/probar propuestas con revisión. |
| CU8 | Opcional / opt-in / integración | Fase 3: móvil e integraciones de entrada | Probar ASR local si el entorno lo permite. |
| CU9 | Opcional / opt-in / integración | Fase 3: móvil e integraciones de entrada | Probar foto/Qwen sólo con configuración explícita. |
| CU10 | MVP-crítico | Fase 1: ruta crítica demostrable | Confirmar comandos HTTP + SSE y propagación medida si se cita. |
| CU11 | Requerido por guía | Fase 2: administración y colaboración | Probar presencia y documentar que no modifica semántica. |
| CU12 | Requerido por guía | Fase 2: administración y colaboración | Probar listar/consultar/resolver conflictos. |
| CU13 | Requerido por guía | Fase 3: móvil e integraciones de entrada | Verificar descriptor-driven, cola offline e integración live cuando exista entorno. |
| CU14 | Limitación conocida / issue abierto | Fase 4: intercambio, despliegue y almacenamiento | Probar XMI y documentar pérdidas; no declararlo lossless. |
| CU15 | MVP-crítico | Fase 1: ruta crítica demostrable | Verificar generación, descarga y hash del artefacto. |
| CU16 | Requerido por guía | Fase 4: intercambio, despliegue y almacenamiento | Verificar endpoints/flujo de deployments. |
| CU17 | Requerido por guía | Fase 5: pruebas, evidencias y cierre académico | Ejecutar health/checks disponibles y guardar evidencia. |
| CU18 | Opcional / opt-in / integración | Fase 4: intercambio, despliegue y almacenamiento | Documentar PostgreSQL por defecto y S3/Floci opt-in. |

## Notas de cobertura y restricciones

- La matriz cubre explícitamente CU1-CU18 sin saltos de identificador.
- Los artefactos de fase 0 quedan listos para copiar/renderizar en Word, pero la copia al `.docx`, la actualización de tabla de contenido y el formato final son acciones manuales pendientes.
- La colaboración debe describirse como comandos HTTP + SSE; no WebSocket.
- XMI debe describirse como parcial y no lossless.
- Qwen/fotografía, S3 y Floci son opt-in; no deben documentarse como dependencias obligatorias ni como evidencia de disponibilidad real.
- `X-User-Id` no es autenticación de producción.
