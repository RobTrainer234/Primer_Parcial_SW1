# Contrato de identidad y permisos CU1-CU5

Este documento congela la decisión de Fase 0 para identidad académica persistente: usuarios demo sembrados, sesiones persistidas y autorización por roles/capacidades del dominio. No se incorpora OAuth, JWT ni autenticación productiva externa en esta línea de trabajo.

## 1. Alcance y no objetivos

| Tema | Decisión |
|---|---|
| Alcance | CU1-CU5: identidad/sesión, proyectos, permisos de proyecto, permisos de diagrama y edición semántica UML. |
| Persistencia objetivo | Identidad, sesión, membresías, invitaciones, capacidades, vistas/diagramas, historial/auditoría y estado semántico recuperable. |
| Usuarios base | Usuarios académicos sembrados para demostración y evaluación reproducible. |
| Transporte | HTTP para comandos/consultas y SSE para eventos cuando aplique. |
| No objetivo | OAuth, JWT de producto, SSO, recuperación de contraseña, MFA, federación de identidad o autorización delegada externa. |
| No objetivo | Seguridad productiva completa. La Fase 0 define contrato académico; fases posteriores no deben venderlo como auth enterprise. |
| No objetivo | Reemplazar endpoints existentes. Las rutas nuevas o en inglés funcionan como aliases/adapters cuando corresponda. |

## 2. Identidad y sesión

| Concepto | Contrato |
|---|---|
| Identidad académica | Usuario persistente con `userId`, nombre visible, correo/alias académico y estado. |
| Usuario demo | Usuario sembrado por configuración para evaluación. Debe existir de forma reproducible cuando `SEED_DEMO_USERS=true` o equivalente esté habilitado. |
| Login demo | `POST /auth/demo-login` inicia una sesión para un usuario demo permitido. |
| Login académico | `POST /auth/login` valida credenciales académicas persistidas, no OAuth/JWT externo. |
| Logout | `POST /auth/logout` invalida o cierra la sesión persistida. |
| Sesión | Token/cookie/identificador opaco de sesión del producto académico. No se documenta como JWT. |
| `X-User-Id` | Sólo bootstrap/adaptador de desarrollo o compatibilidad. No autoriza operaciones productivas por sí mismo. |
| Usuario efectivo | Toda operación protegida se evalúa contra la identidad resuelta por sesión válida o, en modo demo controlado, contra el adapter explícito. |

Regla: la identidad viene del servidor. El cliente no decide rol, estado ni capacidades enviando campos manipulables.

## 3. Roles y estados

### Roles de proyecto

| Rol | Uso esperado |
|---|---|
| `OWNER` | Control total del proyecto; puede administrar miembros, invitaciones, capacidades y vistas. |
| `ADMIN` | Administra colaboración del proyecto según delegación del propietario. |
| `MODELER` / `EDITOR` | Edita modelo y vistas cuando tiene acceso efectivo. |
| `VIEWER` | Consulta sin modificar semántica ni permisos. |

### Roles de diagrama/vista

| Rol | Uso esperado |
|---|---|
| `DIAGRAM_ADMIN` | Administra colaboradores, acceso y administrador de la vista. |
| `COLLABORATOR` / `EDITOR` | Edita la vista/modelo según permisos efectivos. |
| `VIEWER` | Acceso de lectura. |

### Estados

| Estado | Significado |
|---|---|
| `ACTIVE` | Puede operar según rol/capacidades. |
| `INVITED` | Invitación pendiente; no equivale a membresía activa. |
| `SUSPENDED` / `DISABLED` | Identidad o membresía bloqueada para operar. |
| `REMOVED` | Ya no tiene acceso vigente; puede conservarse en auditoría. |
| `PENDING` | Operación o invitación creada pero no consumada. |
| `ACCEPTED` / `DECLINED` | Resultado de invitación o decisión revisable cuando aplique. |

## 4. Capacidades

| Capacidad | Aplica a | Regla |
|---|---|---|
| `create_project` | Usuario académico/demo | Permite crear proyectos si la política académica lo habilita. |
| `view_project` | Proyecto | Requiere membresía activa o permiso equivalente. |
| `manage_project_permissions` | Proyecto | Requiere `OWNER` o `ADMIN`. |
| `invite_members` | Proyecto | Requiere administración de proyecto. |
| `create_diagram` | Proyecto | Puede activarse/desactivarse por miembro. |
| `manage_diagram_permissions` | Diagrama/vista | Requiere `DIAGRAM_ADMIN` o permiso heredado suficiente. |
| `edit_semantic_model` | Proyecto/vista | Requiere acceso efectivo de edición. |
| `edit_layout` | Vista | Requiere acceso efectivo de edición sobre la vista. |
| `view_audit_history` | Proyecto/vista | Requiere administración o permiso de auditoría definido. |

## 5. Matriz de autorización CU1-CU5

| CU | Operación | Requisito mínimo | Resultado permitido |
|---|---|---|---|
| CU1 | Login demo | Demo habilitado y usuario seed válido | Sesión académica demo. |
| CU1 | Login/logout | Credenciales/sesión válidas | Crear o cerrar sesión. |
| CU2 | Crear proyecto | Identidad activa con `create_project` | Proyecto con propietario inicial. |
| CU2 | Listar proyectos | Identidad activa | Sólo proyectos visibles para el usuario. |
| CU2 | Recuperar modelo/versiones | `view_project` | Modelo/versiones autorizadas. |
| CU3 | Listar miembros/invitaciones/sugerencias | `manage_project_permissions` o política de lectura administrativa | Datos administrativos del proyecto. |
| CU3 | Invitar/actualizar miembro | `manage_project_permissions` | Cambio persistido y auditado. |
| CU3 | Cambiar `create-diagram` | `manage_project_permissions` | Capacidad actualizada y auditada. |
| CU3 | Ver historial | `view_audit_history` | Eventos de permisos del proyecto. |
| CU4 | Crear diagrama/vista | `create_diagram` activa | Vista persistida asociada al proyecto. |
| CU4 | Configurar colaboración | `manage_diagram_permissions` | Política de colaboración actualizada. |
| CU4 | Agregar/eliminar colaborador | `manage_diagram_permissions` | Acceso efectivo actualizado y auditado. |
| CU4 | Cambiar administrador | `manage_diagram_permissions` | Nuevo administrador registrado. |
| CU4 | Consultar acceso/historial | Acceso administrativo o auditoría | Acceso efectivo e historial. |
| CU5 | Editar clases/atributos/operaciones/relaciones | `edit_semantic_model` (`editModel` en membresía activa) u `OWNER`/`ADMIN` | Comando semántico aceptado y versionable. |
| CU5 | Consultar snapshot/modelo | `view_project` o acceso de vista | Estado semántico recuperable. |

## 6. Semántica de errores

| Condición | Estado HTTP recomendado | Código semántico | Mensaje esperado |
|---|---:|---|---|
| Sin sesión o sesión inválida | 401 | `AUTH_REQUIRED` | Autenticación requerida. |
| Identidad inactiva | 403 | `USER_INACTIVE` | Usuario no habilitado para operar. |
| Permiso insuficiente | 403 | `FORBIDDEN` | No tiene permisos para esta operación. |
| Recurso inexistente o no visible | 404 | `NOT_FOUND` | Recurso no encontrado. |
| Invitación/estado inválido | 409 | `INVALID_STATE` | La transición no es válida. |
| Conflicto de edición/versionado | 409 | `CONFLICT` | El comando entra en conflicto con el estado actual. |
| Payload inválido | 400 | `VALIDATION_ERROR` | Campos inválidos o faltantes. |
| Alias removido o no soportado | 410/404 | `UNSUPPORTED_ROUTE` | Ruta no soportada por esta versión. |

Regla de privacidad: cuando el usuario no tiene visibilidad, `404` puede ser preferible a revelar existencia del recurso.

## 7. Perfil CU5 soportado

El perfil UML soportado es binario y académico: entidades, atributos, operaciones y seis tipos de relación (`ASOCIACION`, `AGREGACION`, `COMPOSICION`, `GENERALIZACION`, `DEPENDENCIA`, `REALIZACION`). No se exponen controles de clases de asociación ni asociaciones n-arias.

Las operaciones pertenecen semánticamente a una entidad, no al layout del canvas. El contrato persistente y de API usa `nombre` no vacío hasta 120 caracteres, `tipoRetorno` no vacío hasta 120, `firma` no vacía hasta 240 (por ejemplo `()` o `(monto: Decimal)`) y `visibilidad` enum `PUBLICA`, `PROTEGIDA`, `PRIVADA`, `PAQUETE`. La respuesta de entidad conserva `operaciones` como lista de textos UML y agrega `detalleOperaciones` como DTO.

## 8. Aliases y adapters de rutas

Preservar endpoints existentes es parte del contrato académico. Las rutas en español y las rutas en inglés pueden coexistir como adapters mientras el dominio interno converge.

| Superficie | Ruta canónica deseada | Aliases/adapters a preservar mientras existan |
|---|---|---|
| Proyectos | `/projects` | `/proyectos` |
| Modelo de proyecto | `/projects/{projectId}/model` | `/proyectos/{projectId}/modelo`, `/modelos/{modelId}` |
| Versiones | `/projects/{projectId}/versions` | adapters de recuperación existentes |
| Permisos proyecto | `/projects/{projectId}/members`, `/invitations`, `/permission-history` | `/projects/{projectId}/permissions/history` |
| Diagramas/vistas | `/projects/{projectId}/diagrams` | adapters de vista existentes si los hay |
| Historial diagrama | `/projects/{projectId}/diagrams/{viewId}/permission-history` | `/projects/{projectId}/diagrams/{viewId}/permissions/history` |
| Sync/eventos | `/projects/{projectId}/sync/...` | `/sync/...`, `/modelos/{modeloId}/sync/...` |

La documentación debe nombrar claramente cuándo una ruta es canónica y cuándo es alias de compatibilidad.

## 8. Campos de auditoría

Todo cambio de identidad, permisos, colaboración, capacidades y semántica versionable debe poder auditarse con estos campos mínimos:

| Campo | Descripción |
|---|---|
| `auditId` | Identificador del evento. |
| `occurredAt` | Fecha/hora del cambio. |
| `actorUserId` | Usuario efectivo que ejecutó la operación. |
| `targetType` | `PROJECT`, `DIAGRAM`, `MEMBERSHIP`, `INVITATION`, `MODEL`, etc. |
| `targetId` | Identificador del recurso afectado. |
| `action` | Acción de dominio ejecutada. |
| `before` | Estado anterior cuando corresponda. |
| `after` | Estado nuevo cuando corresponda. |
| `reason` | Motivo opcional o comentario de sistema. |
| `requestId` | Correlación técnica para trazabilidad. |
| `sourceRoute` | Ruta/adapter que recibió la operación. |

## 9. Separación semántica/layout

| Dominio | Qué contiene | Qué no debe contener |
|---|---|---|
| Semántica UML | Clases, atributos, operaciones, relaciones, extremos, tipos, multiplicidades y metadatos del modelo. | Coordenadas visuales, tamaño de nodos, rutas de conectores o zoom. |
| Layout visual | Posiciones, dimensiones, rutas, orden visual, viewport y preferencias de presentación. | Cambios de significado UML. |

Reglas:

- Mover una figura no cambia clases, atributos, operaciones ni relaciones.
- Crear/eliminar una relación semántica no depende de coordenadas visuales.
- Snapshots y versiones deben dejar claro qué parte corresponde a semántica y qué parte a layout.
- Presencia, selección o cursores colaborativos no son estado semántico.

## 10. Perfil UML soportado para CU5

CU5 se limita al perfil UML académico de clases necesario para el MVP:

| Constructo | Soporte Fase 0-5 |
|---|---|
| Clase / entidad | Soportado. |
| Atributo | Soportado con nombre, tipo y propiedades básicas. |
| Operación | Contrato soportado; implementación puede madurar por fase y debe declararse parcial si falta. |
| Asociación | Soportada como relación entre extremos. |
| Agregación/composición | Soporte permitido si el modelo distingue tipo de relación. |
| Herencia/generalización | Soporte permitido si está modelado como relación semántica. |
| Multiplicidad/navegabilidad | Soporte esperado cuando existan extremos. |
| Comentarios/notas | Opcional; no bloquea CU5 base. |

## 11. Constructos no soportados o fuera del perfil CU5

No prometer soporte completo para estos elementos salvo evidencia posterior:

- Diagramas UML fuera de clases como secuencia, actividad, estado, componentes o despliegue.
- Restricciones OCL completas.
- Perfiles UML personalizados/stereotypes avanzados.
- Roundtrip XMI lossless.
- Ingeniería inversa completa desde código.
- Merge semántico automático avanzado de cambios concurrentes.
- Autorización productiva OAuth/JWT/SSO.

## 12. Puertas de aceptación por fase

| Fase | Puerta de aceptación |
|---|---|
| Fase 0 | Este contrato existe, está referenciado por la planificación y no promete OAuth/JWT productivo. La documentación distingue demo/parcial de completo. |
| Fase 1 | CU1-CU2 operan con identidad académica persistente o demo explícita; los proyectos/versiones respetan visibilidad. |
| Fase 2 | CU3-CU4 persisten permisos, miembros, invitaciones, capacidades, vistas e historial; si siguen en memoria, el estado debe decir demo/parcial. |
| Fase 3 | Clientes web/móvil consumen identidad y permisos sin confiar en `X-User-Id` como autorización de producto. |
| Fase 4 | Importación/exportación, generación, storage y despliegue respetan permisos efectivos y auditan operaciones relevantes. |
| Fase 5 | Validación final demuestra separación semántica/layout, autorización por matriz y estados honestos por CU; no hay claims de auth productiva no implementada. |

## 13. Cierre de Fase 0

Fase 0 queda cerrada sólo si las fases siguientes tratan este documento como contrato canónico. Cualquier ampliación de autenticación productiva, OAuth/JWT, roles o perfil UML requiere cambio explícito del contrato antes de implementarse.
