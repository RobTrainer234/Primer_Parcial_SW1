# Catálogo de casos de uso derivado de la guía completa del proyecto

## Decisión principal

Este catálogo corrige el catálogo reducido anterior: el **MVP/catálogo de trabajo se deriva de todo el alcance descrito en `GUIA DEL PROYECTO.txt`**. No se declaran fuera de alcance las superficies que la guía exige o documenta como implementadas, verificadas, opt-in o limitadas. Cuando una capacidad depende de configuración o tiene evidencia parcial, se registra como tal en prioridad, estado y brecha.

## Fuente y criterio de alcance

- Fuente autoridad: `GUIA DEL PROYECTO.txt`.
- Producto: diagramador colaborativo UML 2.5 con separación entre estado semántico y layout visual.
- Superficies incluidas: web, móvil, autenticación, permisos, colaboración HTTP+SSE, presencia, voz ASR local, fotografía/Qwen opt-in, XMI, generación determinista, deployments, almacenamiento de artefactos y evidencia de pruebas.
- Transporte vigente: comandos HTTP y eventos SSE; no WebSocket.
- XMI: parcial y no lossless.
- IA textual: desactivada por defecto; propuestas sujetas a revisión.
- Foto/Qwen, S3 y Floci: integraciones opt-in, no garantía de servicio externo real.

## Estados y prioridades usadas

| Valor | Significado |
|---|---|
| MVP-crítico | Necesario para operar la ruta central del producto según la guía. |
| Requerido por guía | Superficie funcional exigida o expuesta por la guía, aunque no sea la ruta mínima de edición. |
| Opcional / opt-in / integración | Capacidad configurable, dependiente de entorno o integración externa/local explícita. |
| Limitación conocida / issue abierto | Capacidad existente con pérdida, evidencia parcial o asunto abierto documentado. |

## Actores

| Actor | Responsabilidad |
|---|---|
| Usuario autenticado | Inicia/cierra sesión y opera proyectos visibles. |
| Usuario demo | Accede por login de demostración cuando `SEED_DEMO_USERS=true`. |
| Propietario o administrador de proyecto | Gestiona permisos, miembros, invitaciones y capacidades. |
| Modelador / colaborador | Crea diagramas, edita modelo UML, layout y propuestas. |
| Administrador de diagrama | Configura colaboración, acceso y administración de una vista. |
| Cliente web | Consume comandos HTTP, snapshots y SSE. |
| Cliente móvil | Consume descriptores, opera offline y sincroniza comandos. |
| Servicio ASR local | Transcribe audio local `es-ES` para alimentar propuestas. |
| Integración Qwen / extractor fotográfico | Extrae candidatos desde imágenes cuando se habilita explícitamente. |
| Generador | Produce artefactos deterministas a partir del modelo. |
| Operador técnico / evaluador | Ejecuta salud, pruebas, evidencia, despliegues y almacenamiento. |

## Resumen ejecutivo de casos de uso

| Código | Nombre | Actor principal | Prioridad / estado | Fuente guía | Estado actual / brecha |
|---|---|---|---|---|---|
| CU1 | Autenticarse y gestionar sesión | Usuario autenticado / demo | MVP-crítico | API Autenticación; Autenticación y seguridad | Implementado; `X-User-Id` sólo bootstrap, no auth productiva. |
| CU2 | Crear, listar y recuperar proyectos/versiones | Usuario autenticado | MVP-crítico | Proyectos y recuperación; Modelo y límites de estado | Implementado; versiones y recuperación expuestas por API. |
| CU3 | Gestionar permisos, invitaciones, miembros y capacidades de proyecto | Administrador de proyecto | Requerido por guía | Permisos del proyecto | Demo/parcial: endpoints expuestos con estado en memoria; Fase 0 congela persistencia y autorización. |
| CU4 | Crear diagramas/vistas y administrar permisos de diagrama | Administrador de diagrama | Requerido por guía | Permisos del diagrama | Demo/parcial: endpoints expuestos con estado en memoria; colaboración, acceso e historial no deben presentarse como persistentes aún. |
| CU5 | Editar estado semántico UML 2.5 | Modelador | MVP-crítico | Modelo y límites de estado; Sincronización | Implementado por comandos/snapshot; debe conservar separación semántica/layout. |
| CU6 | Editar layout, geometría y usar atajos web | Modelador / cliente web | MVP-crítico | Web; Atajos; Layout | Implementado en web; propagación geométrica con evidencia específica. |
| CU7 | Crear propuestas desde texto y revisar decisión | Modelador | Requerido por guía / opt-in | IA texto; Propuestas y revisión | Texto IA desactivado por defecto; proveedor determinístico; decisión humana obligatoria. |
| CU8 | Transcribir voz local y convertirla en propuesta | Modelador + ASR local | Opcional / opt-in / integración | Voz; Contrato ASR local | Integrado con `faster-whisper`; sólo `es-ES`, audio máximo 25 MiB. |
| CU9 | Crear propuesta desde foto/Qwen y revisarla | Modelador + integración Qwen | Opcional / opt-in / integración | Qwen y fotografía; Propuestas | Endpoint configurable; Qwen no obligatorio ni evidencia de servicio real. |
| CU10 | Sincronizar comandos, snapshots y eventos SSE | Cliente web/móvil | MVP-crítico | Sincronización, conflictos y presencia | Implementado; HTTP+SSE, no WebSocket; propagación semántica medida 2–4 s. |
| CU11 | Publicar y visualizar presencia | Colaborador | Requerido por guía | Presencia; Pruebas y evidencia | E2E presencia 5/5; presencia no reemplaza estado semántico. |
| CU12 | Listar, consultar y resolver conflictos | Colaborador | Requerido por guía | Conflictos; móvil offline | Implementado por endpoints de sync/conflicts; resolución explícita. |
| CU13 | Consumir desde móvil descriptor-driven con cola offline | Cliente móvil | Requerido por guía | Móvil Flutter | Implementado como descriptor-driven; cola offline no equivale a confirmación. |
| CU14 | Importar/exportar XMI con preview/confirmación | Modelador | Limitación conocida / issue abierto | XMI; Evidencia roundtrip | Parcial, no lossless; Q9 abierto. |
| CU15 | Gestionar generación determinista, targets, perfiles, runs y artefactos | Modelador / generador | MVP-crítico | Generación determinista; API Generación | Evidencia de dos ejecuciones con mismo `artifactZipHash`; cobertura acotada. |
| CU16 | Gestionar deployments | Operador técnico | Requerido por guía | Deployments | API expone crear/listar/ver/actualizar/habilitar/deshabilitar/eliminar. |
| CU17 | Consultar salud, ejecutar comandos y conservar evidencia | Operador técnico / evaluador | Requerido por guía | Salud; Pruebas y evidencia; comandos | Comandos documentados; resultados dependen del entorno. |
| CU18 | Configurar almacenamiento local/S3/Floci para artefactos | Operador técnico | Opcional / opt-in / integración | S3 y Floci; variables | PostgreSQL por defecto; S3/Floci opt-in; Floci no prueba AWS real. |

## Trazabilidad guía/API -> casos de uso

| Sección o superficie de `GUIA DEL PROYECTO.txt` | Casos de uso |
|---|---|
| Resumen / Arquitectura general | CU2, CU5, CU6, CU10, CU13, CU15 |
| Estado conocido | CU8, CU9, CU10, CU11, CU13, CU14, CU15, CU18 |
| Backend stack, modelo, migraciones | CU2-CU5, CU10-CU18 |
| Variables de configuración | CU1, CU7-CU9, CU13, CU15, CU18 |
| API Autenticación | CU1 |
| API Proyectos y recuperación | CU2 |
| API Permisos del proyecto | CU3 |
| API Permisos del diagrama | CU4 |
| API Propuestas y revisión | CU7, CU8, CU9 |
| API Sincronización, conflictos y presencia | CU10, CU11, CU12, CU13 |
| API XMI | CU14 |
| API Generación | CU15 |
| API Deployments | CU16 |
| API Salud | CU17 |
| Autenticación y seguridad | CU1, CU3, CU4, CU9, CU18 |
| IA texto, voz, fotografía y Qwen | CU7, CU8, CU9 |
| Contrato ASR local | CU8 |
| Web y atajos | CU5, CU6, CU10, CU11, CU12 |
| Móvil Flutter descriptor-driven y offline | CU13, CU10, CU12 |
| Colaboración, presencia y sincronización | CU10, CU11, CU12 |
| XMI / evidencia roundtrip | CU14 |
| Generación determinista | CU15 |
| Despliegue y servicios | CU16, CU17, CU18 |
| Pruebas y evidencia | CU10, CU11, CU14, CU15, CU17 |
| Limitaciones y riesgos conocidos | CU1, CU7, CU8, CU9, CU10, CU14, CU15, CU18 |

---

## CU1 - Autenticarse y gestionar sesión

| Campo | Definición |
|---|---|
| Actor | Usuario autenticado; usuario demo |
| Objetivo | Iniciar, cerrar o iniciar sesión de demostración para operar superficies protegidas. |
| Prioridad | MVP-crítico |
| Fuente guía | API Autenticación; Autenticación y seguridad; variables `SEED_DEMO_USERS`. |
| Estado / brecha | Implementado; demo-login sólo cuando se siembran usuarios demo; `X-User-Id` es bootstrap, no autenticación productiva. |

**Precondiciones**

- El backend está disponible.
- Para demo-login, `app.seed-demo-users=true` / `SEED_DEMO_USERS=true`.

**Flujo principal**

1. El usuario envía credenciales a `POST /auth/login`.
2. El sistema valida la identidad y crea sesión.
3. El usuario opera proyectos, diagramas y permisos según autorización.
4. El usuario cierra sesión con `POST /auth/logout`.

**Flujos alternos o de error**

- Credenciales inválidas: el sistema rechaza la sesión.
- Demo-login deshabilitado: `POST /auth/demo-login` no debe presentarse como disponible.
- Uso de `X-User-Id`: sólo válido como bootstrap de desarrollo/integración.

**Criterios de aceptación**

- Login, logout y demo-login documentan sus condiciones.
- No se presenta `X-User-Id` como seguridad productiva.
- Las operaciones posteriores dependen de permisos del proyecto o diagrama.

---

## CU2 - Crear, listar y recuperar proyectos/versiones

| Campo | Definición |
|---|---|
| Actor | Usuario autenticado |
| Objetivo | Crear el contenedor de trabajo, listar proyectos visibles y recuperar modelo o revisiones. |
| Prioridad | MVP-crítico |
| Fuente guía | API Proyectos y recuperación; Modelo y límites de estado. |
| Estado / brecha | Implementado; recuperación y versiones expuestas por API. |

**Precondiciones**

- Sesión o identidad válida.
- El usuario tiene visibilidad sobre el proyecto cuando consulta.

**Flujo principal**

1. El usuario crea un proyecto con `POST /projects`.
2. Consulta proyectos visibles con `GET /projects`.
3. Abre el modelo recuperable con `GET /projects/{projectId}/model`.
4. Lista versiones con `GET /projects/{projectId}/versions`.
5. Selecciona una revisión con `GET /projects/{projectId}/versions/{revision}`.

**Flujos alternos o de error**

- Proyecto inexistente o no visible: respuesta de error/autorización.
- Revisión inexistente: no se debe inventar estado recuperado.

**Criterios de aceptación**

- El proyecto creado es visible según permisos.
- El modelo recuperado conserva estado semántico y layout como dominios separados.
- Las revisiones permiten identificar evolución del modelo.

---

## CU3 - Gestionar permisos, invitaciones, miembros y capacidades de proyecto

| Campo | Definición |
|---|---|
| Actor | Propietario o administrador de proyecto |
| Objetivo | Controlar quién participa, con qué estado y capacidades, y auditar cambios. |
| Prioridad | Requerido por guía |
| Fuente guía | API Permisos del proyecto; Autenticación y seguridad. |
| Estado / brecha | Demo/parcial: miembros, invitaciones, capacidades e historial existen para demostración, pero están en memoria local por proceso. Deben pasar a persistencia y autorización según `documentacion/arquitectura/CONTRATO_IDENTIDAD_PERMISOS_CU1_CU5.md`. |

**Precondiciones**

- Existe un proyecto.
- El actor tiene permiso administrativo.

**Flujo principal**

1. Ejecuta comandos de permisos con `POST /projects/{projectId}/commands`.
2. Consulta invitaciones y miembros.
3. Busca cuentas sugeribles.
4. Actualiza miembro/estado con `PATCH /members/{userId}`.
5. Cambia capacidad de crear diagramas con `PUT /capabilities/create-diagram`.
6. Consulta historial por cualquiera de los aliases documentados.

**Flujos alternos o de error**

- Usuario no autorizado: operación rechazada.
- Cuenta inexistente o invitación inválida: error trazable.
- Alias de historial: deben resolver al mismo concepto funcional.
- Reinicio del backend: el estado demo en memoria puede perderse hasta implementar persistencia.

**Criterios de aceptación**

- Permite invitaciones, miembros, capacidades e historial al menos en modo demo.
- Para declararse completo debe persistir cambios y auditoría fuera de memoria local.
- No concede acceso por datos controlados sólo por el cliente.

---

## CU4 - Crear diagramas/vistas y administrar permisos de diagrama

| Campo | Definición |
|---|---|
| Actor | Administrador de diagrama; modelador con capacidad de crear diagrama |
| Objetivo | Crear vistas/diagramas y administrar colaboración, colaboradores, administrador, acceso e historial. |
| Prioridad | Requerido por guía |
| Fuente guía | API Permisos del diagrama. |
| Estado / brecha | Demo/parcial: creación de vista, colaboración, acceso e historial existen para demostración, pero están en memoria local por proceso. Persistencia y autorización quedan regidas por el contrato de Fase 0. |

**Precondiciones**

- Existe proyecto.
- El actor puede crear o administrar diagramas.

**Flujo principal**

1. Crea vista con `POST /projects/{projectId}/diagrams`.
2. Configura colaboración con `POST /diagrams/{viewId}/collaboration`.
3. Agrega o actualiza colaboradores con `PUT /collaborators/{userId}`.
4. Elimina colaboradores con `DELETE /collaborators/{userId}`.
5. Cambia administrador con `PUT /administrator`.
6. Consulta acceso efectivo e historial de permisos.

**Flujos alternos o de error**

- Capacidad de crear diagrama deshabilitada: creación rechazada.
- Colaborador sin cuenta o sin proyecto: error de validación/autorización.
- Vista inexistente: error no recuperable por el cliente.
- Reinicio del backend: vistas/permisos demo en memoria pueden perderse hasta implementar persistencia.

**Criterios de aceptación**

- La vista queda asociada al proyecto en modo demo/parcial.
- Los cambios de colaboración impactan acceso efectivo durante la sesión en memoria.
- Para declararse completo debe persistir vista, permisos e historial auditable.

---

## CU5 - Editar estado semántico UML 2.5

| Campo | Definición |
|---|---|
| Actor | Modelador / colaborador |
| Objetivo | Crear y modificar clases/entidades, atributos, operaciones, relaciones y extremos sin mezclarlo con layout. |
| Prioridad | MVP-crítico |
| Fuente guía | Modelo y límites de estado; Colaboración; Migraciones de estado semántico. |
| Estado / brecha | Implementado por modelo, comandos y snapshots; endpoints específicos se agrupan bajo sincronización. |

**Precondiciones**

- Proyecto y vista/modelo abiertos.
- Permiso de edición suficiente.

**Flujo principal**

1. El modelador abre el estado recuperable o snapshot.
2. Crea o edita clases/entidades UML.
3. Agrega atributos con tipos y propiedades.
4. Agrega operaciones cuando corresponda al modelo UML.
5. Define relaciones y extremos.
6. Envía cambios como comandos de sincronización.
7. El sistema registra revisiones/versiones.

**Flujos alternos o de error**

- Comando inválido: se rechaza o deriva a conflicto.
- Relación con extremo inexistente: se reporta inconsistencia.
- Edición sin permiso: se rechaza por permisos de proyecto/diagrama.

**Criterios de aceptación**

- La semántica UML no depende de coordenadas visuales.
- Clases, atributos, operaciones, relaciones y extremos son recuperables.
- Los cambios generan estado sincronizable y versionable.

---

## CU6 - Editar layout, geometría y usar atajos web

| Campo | Definición |
|---|---|
| Actor | Modelador / cliente web |
| Objetivo | Modificar presentación visual —nodos, posiciones y rutas— sin alterar significado UML. |
| Prioridad | MVP-crítico |
| Fuente guía | Web; Atajos; layout; reportes de geometría. |
| Estado / brecha | Implementado con evidencia E2E/geométrica; debe conservar independencia semántica-layout. |

**Precondiciones**

- Vista abierta en web.
- Elementos semánticos existentes o creables.

**Flujo principal**

1. El usuario mueve o ajusta nodos y rutas.
2. Usa atajos relevantes: `Ctrl+S`, deshacer/rehacer, copiar/pegar, duplicar y `Delete`.
3. El cliente envía comandos de layout.
4. El sistema persiste geometría separada del modelo semántico.
5. Otros clientes reciben actualización por snapshot/SSE cuando corresponde.

**Flujos alternos o de error**

- Operación de layout sobre elemento eliminado: se rechaza o genera conflicto.
- Atajo no aplicable: no debe corromper estado.
- Desconexión: se maneja vía sincronización/cola según cliente.

**Criterios de aceptación**

- Mover una figura no cambia clases, atributos ni relaciones.
- Los atajos ejecutan acciones esperadas o fallan de forma segura.
- La geometría se recupera y sincroniza separadamente.

---

## CU7 - Crear propuestas desde texto y revisar decisión

| Campo | Definición |
|---|---|
| Actor | Modelador |
| Objetivo | Registrar propuestas textuales y aceptarlas o descartarlas mediante revisión. |
| Prioridad | Requerido por guía / opcional según configuración |
| Fuente guía | API Propuestas y revisión; IA texto. |
| Estado / brecha | `AI_TEXT_PROPOSALS_ENABLED=false` por defecto; proveedor determinístico por defecto. |

**Precondiciones**

- Proyecto existente.
- Funcionalidad textual habilitada si se espera IA activa.

**Flujo principal**

1. El usuario crea propuesta con `POST /projects/{projectId}/proposals`.
2. Consulta la propuesta con `GET /proposals/{proposalId}`.
3. Consulta revisión con `GET /review`.
4. Acepta o descarta con `POST /review-decision`.
5. Sólo una decisión aceptada modifica el trabajo según reglas del sistema.

**Flujos alternos o de error**

- IA deshabilitada: no se presenta como generación IA activa.
- Propuesta inválida: queda rechazada o pendiente de revisión.
- Revisión negativa: no debe aplicar cambios semánticos.

**Criterios de aceptación**

- Toda propuesta conserva trazabilidad y decisión.
- La configuración por defecto no promete IA remota.
- La decisión de revisión es explícita.

---

## CU8 - Transcribir voz local y convertirla en propuesta

| Campo | Definición |
|---|---|
| Actor | Modelador; servicio ASR local |
| Objetivo | Transcribir audio local y usar el texto resultante como entrada para propuestas revisables. |
| Prioridad | Opcional / opt-in / integración |
| Fuente guía | Voz; Contrato ASR local; Propuestas. |
| Estado / brecha | Integrado con `tools/local-asr`; sólo `es-ES`, máximo 25 MiB, CPU int8. |

**Precondiciones**

- Sidecar ASR local disponible en `http://127.0.0.1:8765/transcribe`.
- Modelo `faster-whisper` configurado fuera del repositorio.
- Audio en español `es-ES` y dentro del límite.

**Flujo principal**

1. El usuario envía audio al ASR local.
2. El sidecar transcribe el audio.
3. El cliente usa el texto para crear una propuesta.
4. El usuario revisa y decide la propuesta como en CU7.

**Flujos alternos o de error**

- Idioma distinto de `es-ES`: no se documenta como soportado.
- Audio mayor a 25 MiB: se rechaza.
- ASR no disponible: no debe bloquear edición manual.

**Criterios de aceptación**

- Transcripción y propuesta son etapas separadas.
- No se documenta ASR como multilingüe ni ilimitado.
- Todo cambio derivado de voz pasa por revisión.

---

## CU9 - Crear propuesta desde foto/Qwen y revisarla

| Campo | Definición |
|---|---|
| Actor | Modelador; integración Qwen/extractor fotográfico |
| Objetivo | Extraer candidatos desde una imagen como propuesta opt-in y revisable. |
| Prioridad | Opcional / opt-in / integración |
| Fuente guía | Qwen y fotografía; API Propuestas; variables `CU12_PHOTO_*` y `AI_QWEN_*`. |
| Estado / brecha | Endpoint e integración configurables; Qwen desactivable; no evidencia universal de servicio real. |

**Precondiciones**

- Integración fotográfica habilitada por configuración.
- Si se usa Qwen, endpoint/modelo/timeouts configurados.

**Flujo principal**

1. El usuario sube imagen con `POST /projects/{projectId}/proposals/photo`.
2. El extractor genera una propuesta candidata.
3. El usuario consulta propuesta y revisión.
4. Acepta o descarta mediante `review-decision`.

**Flujos alternos o de error**

- Qwen deshabilitado o no disponible: se informa sin asumir servicio remoto.
- Imagen inválida o demasiado grande: se rechaza.
- Extracción ambigua: queda como propuesta no aplicada hasta decisión.

**Criterios de aceptación**

- Foto/Qwen son opt-in explícitos.
- La extracción no modifica el modelo sin revisión.
- Los límites de request, response, timeouts y caché quedan configurables.

---

## CU10 - Sincronizar comandos, snapshots y eventos SSE

| Campo | Definición |
|---|---|
| Actor | Cliente web; cliente móvil; colaborador |
| Objetivo | Enviar operaciones, obtener snapshots y recibir eventos de sincronización por SSE. |
| Prioridad | MVP-crítico |
| Fuente guía | API Sincronización; Colaboración; Arquitectura. |
| Estado / brecha | Implementado; propagación semántica medida aproximadamente 2–4 s en escenario validado. |

**Precondiciones**

- Modelo/proyecto existente.
- Cliente conoce `projectId` y/o `modelId` según endpoint.

**Flujo principal**

1. El cliente envía comando por `/sync/commands`, `/sync/operations` o variantes con proyecto.
2. Cuando corresponde, vacía lote con `/sync/flush`.
3. Obtiene snapshot con `/sync/snapshot` o variante por proyecto.
4. Abre SSE en `/sync/events` o variante por proyecto.
5. Procesa evento inicial `snapshot`, eventos de sincronización y presencia.

**Flujos alternos o de error**

- Comando conflictivo: deriva a CU12.
- `afterRevision` atrasado o inválido: cliente debe recuperar snapshot.
- Transporte SSE no disponible: no se debe afirmar WebSocket como alternativa vigente salvo implementación futura.

**Criterios de aceptación**

- El stream SSE usa `text/event-stream`.
- Snapshot y eventos permiten reconstruir estado sincronizable.
- El catálogo y documentación dicen HTTP+SSE, no WebSocket.

---

## CU11 - Publicar y visualizar presencia

| Campo | Definición |
|---|---|
| Actor | Colaborador; cliente web/móvil |
| Objetivo | Publicar actividad de participantes y visualizarla sin confundirla con estado del modelo. |
| Prioridad | Requerido por guía |
| Fuente guía | API Presencia; Colaboración; Evidencia presencia. |
| Estado / brecha | Verificado en alcance medido; E2E presencia 5/5. |

**Precondiciones**

- Proyecto/modelo abierto.
- Cliente identificado para publicar presencia.

**Flujo principal**

1. El cliente publica presencia con `POST /projects/{projectId}/sync/presence?modelId=...`.
2. Otros clientes reciben presencia por SSE.
3. La UI visualiza actividad/participantes.
4. El sistema mantiene presencia separada del estado semántico.

**Flujos alternos o de error**

- Cliente desconectado: presencia puede expirar o dejar de publicarse.
- Presencia sin permiso o proyecto inválido: se rechaza.

**Criterios de aceptación**

- La presencia se propaga por el stream de eventos.
- La presencia no modifica clases, relaciones ni layout.
- La documentación conserva la evidencia medida y sus límites.

---

## CU12 - Listar, consultar y resolver conflictos

| Campo | Definición |
|---|---|
| Actor | Colaborador; cliente móvil offline |
| Objetivo | Detectar conflictos de sincronización, consultarlos y registrar resolución explícita. |
| Prioridad | Requerido por guía |
| Fuente guía | API Conflictos; móvil offline; Colaboración. |
| Estado / brecha | Implementado por endpoints de conflictos; resolución no debe ser implícita. |

**Precondiciones**

- Existe modelo con operaciones concurrentes o lote offline.
- Cliente tiene permiso de consulta/resolución.

**Flujo principal**

1. Lista conflictos con `GET /sync/conflicts`.
2. Consulta un conflicto con `GET /sync/conflicts/{operationId}`.
3. Decide resolución en la UI o cliente.
4. Registra resolución con `POST /resolve`.
5. Continúa sincronización tras resolución.

**Flujos alternos o de error**

- Conflicto inexistente o ya resuelto: respuesta adecuada.
- Resolución inválida: se rechaza y conserva conflicto.
- Cola móvil envía comandos incompatibles: se listan para revisión.

**Criterios de aceptación**

- Los conflictos son visibles y consultables.
- Resolver requiere acción explícita.
- El sistema no confirma automáticamente comandos offline conflictivos.

---

## CU13 - Consumir desde móvil descriptor-driven con cola offline

| Campo | Definición |
|---|---|
| Actor | Cliente móvil; usuario móvil |
| Objetivo | Operar capacidades del modelo desde Flutter guiado por descriptores y tolerar desconexión mediante cola offline. |
| Prioridad | Requerido por guía |
| Fuente guía | Móvil Flutter; comandos mobile; sincronización. |
| Estado / brecha | Implementado descriptor-driven; validación local y live dependen del entorno. |

**Precondiciones**

- App Flutter disponible.
- `PLATFORM_BASE_URL` configurado para pruebas live si corresponde.

**Flujo principal**

1. La app obtiene o usa descriptor de capacidades/modelo.
2. Renderiza operaciones según descriptor.
3. En conexión, envía comandos al backend.
4. Sin conexión, guarda comandos en cola local.
5. Al reconectar, envía lote/flush y procesa conflictos.

**Flujos alternos o de error**

- Backend no disponible: comandos quedan en cola si la operación lo permite.
- Conflicto de flush: deriva a CU12.
- Prueba local Flutter no equivale a integración live completa.

**Criterios de aceptación**

- La UI móvil responde al descriptor, no a pantallas hardcodeadas como única fuente.
- La cola offline conserva comandos pendientes.
- La confirmación final pertenece al protocolo de sincronización.

---

## CU14 - Importar/exportar XMI con preview/confirmación

| Campo | Definición |
|---|---|
| Actor | Modelador |
| Objetivo | Exportar XMI e importar con preview y confirmación consciente de pérdidas. |
| Prioridad | Limitación conocida / issue abierto |
| Fuente guía | API XMI; evidencia roundtrip; limitaciones Q9. |
| Estado / brecha | Parcial, no lossless; Q9 abierto. |

**Precondiciones**

- Proyecto y modelo existentes.
- Archivo XMI disponible para importación o modelo para exportación.

**Flujo principal**

1. Exporta con `POST /xmi/export` cuando necesita intercambio.
2. Para importar, ejecuta `POST /xmi/import/preview`.
3. Revisa cambios y pérdidas potenciales.
4. Confirma con `POST /xmi/import/confirm` si acepta el resultado.

**Flujos alternos o de error**

- XMI inválido: preview rechaza.
- Pérdidas no aceptables: el usuario no confirma.
- Relaciones/geometría pueden perderse según evidencia medida.

**Criterios de aceptación**

- La importación tiene preview antes de confirmación.
- La documentación no promete roundtrip completo.
- Se explicita que clases/atributos/operaciones conservan mejor que relaciones/geometría según evidencia.

---

## CU15 - Gestionar generación determinista, targets, perfiles, runs y artefactos

| Campo | Definición |
|---|---|
| Actor | Modelador; generador |
| Objetivo | Configurar objetivo/perfil, ejecutar generación determinista y descargar artefactos. |
| Prioridad | MVP-crítico |
| Fuente guía | API Generación; Generación determinista; evidencia generation-e2e. |
| Estado / brecha | Evidencia positiva acotada: dos ejecuciones con mismo `artifactZipHash` y backend CRUD generado. |

**Precondiciones**

- Modelo suficiente para generación.
- Target y perfil disponibles.

**Flujo principal**

1. Crea target con `POST /generation-targets`.
2. Lista targets y perfiles.
3. Inicia generación con `POST /generations`.
4. Consulta run con `GET /generations/{runId}`.
5. Descarga artefactos con `GET /generations/{runId}/artifacts`.
6. Compara reproducibilidad mediante hash cuando corresponde.

**Flujos alternos o de error**

- Modelo inválido o perfil incompatible: run falla con estado rastreable.
- Artefacto ausente: no se debe simular descarga.
- Evidencia de dos ejecuciones no prueba todos los modelos/perfiles.

**Criterios de aceptación**

- La generación es determinista para escenarios evidenciados.
- Targets, perfiles, runs y artefactos son consultables.
- El ZIP corresponde al run solicitado.

---

## CU16 - Gestionar deployments

| Campo | Definición |
|---|---|
| Actor | Operador técnico; modelador autorizado |
| Objetivo | Crear, listar, consultar, actualizar, habilitar, deshabilitar y eliminar deployments. |
| Prioridad | Requerido por guía |
| Fuente guía | API Deployments. |
| Estado / brecha | API expuesta; garantías productivas dependen del entorno. |

**Precondiciones**

- Proyecto existente.
- Actor autorizado.

**Flujo principal**

1. Crea deployment con `POST /deployments`.
2. Lista deployments.
3. Consulta detalle por `deploymentId`.
4. Actualiza con `PUT /deployments/{deploymentId}`.
5. Deshabilita o habilita con endpoints específicos.
6. Elimina con `DELETE /deployments/{deploymentId}` cuando corresponda.

**Flujos alternos o de error**

- Deployment inexistente: error.
- Estado incompatible para habilitar/deshabilitar: rechazo explícito.
- Falta de permisos: operación denegada.

**Criterios de aceptación**

- Todas las operaciones CRUD/estado documentadas tienen CU asociado.
- Los cambios de estado son observables.
- No se promete alta disponibilidad AWS por la sola existencia del endpoint.

---

## CU17 - Consultar salud, ejecutar comandos y conservar evidencia

| Campo | Definición |
|---|---|
| Actor | Operador técnico; evaluador |
| Objetivo | Comprobar salud del sistema y ejecutar comandos documentados de backend, web, móvil y evidencia. |
| Prioridad | Requerido por guía |
| Fuente guía | Salud; comandos backend/web/mobile; Pruebas y evidencia. |
| Estado / brecha | Comandos documentados; algunos requieren Docker, Testcontainers o entorno live. |

**Precondiciones**

- Entorno correspondiente disponible: Java/Maven, Docker, Node, Flutter o sidecars según comando.

**Flujo principal**

1. Consulta `GET /actuator/health`.
2. Ejecuta comandos backend (`docker compose`, `mvn test`, `mvn verify`) cuando el entorno lo permite.
3. Ejecuta comandos web (`npm run test:run`, typecheck, lint, build) cuando corresponde.
4. Ejecuta comandos móvil (`flutter test`, integración live) cuando corresponde.
5. Conserva reportes E2E, presencia, geometría, generación y XMI como evidencia.

**Flujos alternos o de error**

- Docker no disponible: `mvn verify` con Testcontainers puede fallar por entorno.
- Integración live móvil sin backend: no prueba integración completa.
- Health sin detalles: no se deben inventar diagnósticos internos.

**Criterios de aceptación**

- La salud del backend es consultable.
- Los comandos exactos están documentados por componente.
- La evidencia distingue resultado medido de diseño previsto.

---

## CU18 - Configurar almacenamiento local/S3/Floci para artefactos

| Campo | Definición |
|---|---|
| Actor | Operador técnico |
| Objetivo | Configurar almacenamiento de artefactos en PostgreSQL por defecto o S3/Floci como integración opt-in. |
| Prioridad | Opcional / opt-in / integración |
| Fuente guía | Variables `GENERATION_ARTIFACTS_*`, `FLOCI_*`, S3 y Floci. |
| Estado / brecha | PostgreSQL por defecto; S3/Floci opt-in; Floci no demuestra AWS real. |

**Precondiciones**

- Generación de artefactos habilitada.
- Configuración de store definida (`postgres` por defecto o `s3`).

**Flujo principal**

1. Usa almacenamiento por defecto en PostgreSQL para artefactos.
2. Si necesita S3, configura bucket, región, endpoint y prefijo.
3. Si usa entorno local AWS-like, habilita perfil/puerto Floci.
4. Ejecuta generación y descarga artefactos desde CU15.

**Flujos alternos o de error**

- Credenciales o endpoint S3 inválidos: almacenamiento falla y se reporta.
- Floci habilitado: sólo prueba integración local compatible, no AWS real.
- Servicio externo ausente: no se debe presentar como disponibilidad productiva.

**Criterios de aceptación**

- El modo de almacenamiento queda explícito por configuración.
- Las opciones opt-in no son obligatorias para el flujo base.
- La documentación conserva la restricción: Floci local no prueba AWS real.

---

## Criterio de cierre del catálogo

El catálogo se considera corregido cuando cada sección y superficie de `GUIA DEL PROYECTO.txt` queda trazada al menos a un CU, y cada CU registra prioridad, fuente, estado y brecha sin degradar funcionalidades requeridas por la guía a “fuera de alcance”.
