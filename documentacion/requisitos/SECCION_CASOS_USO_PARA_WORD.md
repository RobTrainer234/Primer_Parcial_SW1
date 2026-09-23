# 2.1 FT: CAPTURA DE REQUISITOS

Esta sección presenta el modelo de casos de uso del diagramador colaborativo de clases UML 2.5. El contenido fue elaborado a partir de `GUIA DEL PROYECTO.txt` y del catálogo corregido `documentacion/requisitos/CASOS_USO_MVP.md`, conservando los casos CU1 a CU18. La estructura reproduce el estilo académico del documento de referencia para facilitar su incorporación directa al documento Word del proyecto. Como apoyo visual y de trazabilidad, usar `documentacion/requisitos/DIAGRAMA_CASOS_USO.md` y `documentacion/requisitos/MATRIZ_ACTORES_CU_MODULOS.md`.

## 2.1.1 Identificar Actores y Casos de Uso

### 2.1.1.1 Identificar Actores

| Actor | Descripción |
|---|---|
| Usuario autenticado | Persona que inicia sesión y opera proyectos visibles de acuerdo con sus permisos. |
| Usuario demo | Usuario que accede por login de demostración cuando la configuración `SEED_DEMO_USERS=true` está habilitada. |
| Propietario o administrador de proyecto | Responsable de miembros, invitaciones, capacidades e historial de permisos del proyecto. |
| Administrador de diagrama | Responsable de configurar colaboración, colaboradores, administrador, acceso e historial de una vista o diagrama. |
| Modelador / colaborador | Usuario que crea y modifica el modelo UML, el layout, las propuestas, la presencia y la resolución de conflictos. |
| Cliente web | Aplicación web que consume comandos HTTP, snapshots y eventos SSE. |
| Cliente móvil | Aplicación Flutter descriptor-driven que opera capacidades del modelo y gestiona cola offline. |
| Servicio ASR local | Servicio local `faster-whisper` que transcribe audio `es-ES` para alimentar propuestas revisables. |
| Integración Qwen / extractor fotográfico | Componente configurable y opt-in que extrae candidatos desde imágenes. |
| Generador | Componente que produce artefactos deterministas a partir del modelo, objetivos y perfiles de generación. |
| Operador técnico / evaluador | Actor que verifica salud, ejecuta comandos, conserva evidencia, gestiona deployments y configura almacenamiento. |

### 2.1.1.2 Identificar Casos de Uso

| Código | Caso de uso | Actor principal |
|---|---|---|
| CU1 | Autenticarse y gestionar sesión | Usuario autenticado / usuario demo |
| CU2 | Crear, listar y recuperar proyectos/versiones | Usuario autenticado |
| CU3 | Gestionar permisos, invitaciones, miembros y capacidades de proyecto | Propietario o administrador de proyecto |
| CU4 | Crear diagramas/vistas y administrar permisos de diagrama | Administrador de diagrama |
| CU5 | Editar estado semántico UML 2.5 | Modelador / colaborador |
| CU6 | Editar layout, geometría y usar atajos web | Modelador / cliente web |
| CU7 | Crear propuestas desde texto y revisar decisión | Modelador |
| CU8 | Transcribir voz local y convertirla en propuesta | Modelador / servicio ASR local |
| CU9 | Crear propuesta desde foto/Qwen y revisarla | Modelador / integración Qwen |
| CU10 | Sincronizar comandos, snapshots y eventos SSE | Cliente web / cliente móvil |
| CU11 | Publicar y visualizar presencia | Colaborador |
| CU12 | Listar, consultar y resolver conflictos | Colaborador / cliente móvil |
| CU13 | Consumir desde móvil descriptor-driven con cola offline | Cliente móvil |
| CU14 | Importar/exportar XMI con preview/confirmación | Modelador |
| CU15 | Gestionar generación determinista, targets, perfiles, runs y artefactos | Modelador / generador |
| CU16 | Gestionar deployments | Operador técnico |
| CU17 | Consultar salud, ejecutar comandos y conservar evidencia | Operador técnico / evaluador |
| CU18 | Configurar almacenamiento local/S3/Floci para artefactos | Operador técnico |

## 2.1.2 Priorizar Casos de Uso

La priorización distingue la ruta central del producto, las superficies requeridas por la guía, las integraciones opt-in y las capacidades con limitaciones conocidas.

### Ciclo #1: Núcleo del producto

| Prioridad | Casos de uso | Justificación |
|---|---|---|
| MVP-crítico | CU1, CU2, CU5, CU6, CU10, CU15 | Habilitan acceso, proyectos, edición UML, layout, colaboración HTTP+SSE y generación de artefactos. |
| Requerido por guía | CU3, CU4, CU11, CU12, CU17 | Completan administración, presencia, conflictos y verificación del producto. |

### Ciclo #2: Extensiones, integraciones y operación avanzada

| Prioridad | Casos de uso | Justificación |
|---|---|---|
| Requerido por guía | CU7, CU13, CU16 | Cubren propuestas, cliente móvil y deployments documentados por la guía. |
| Opcional / opt-in / integración | CU8, CU9, CU18 | Dependen de configuración explícita: ASR local, Qwen/fotografía y S3/Floci. |
| Limitación conocida | CU14 | XMI existe con preview y confirmación, pero el intercambio no es lossless. |

## 2.1.3 Especificar Casos de Uso

### CU1: Autenticarse y gestionar sesión

- **Propósito:** Permitir que el usuario acceda al sistema, opere según permisos y cierre sesión.
- **Resumen:** El sistema expone login, logout y login demo condicionado por configuración.
- **Actores:** Usuario autenticado, usuario demo.
- **Actor iniciador:** Usuario autenticado o usuario demo.
- **Precondición:** Backend disponible; para demo-login, `SEED_DEMO_USERS=true`.
- **Postcondición:** Sesión iniciada o finalizada; acceso posterior sujeto a permisos.
- **Flujo principal:** 1) El usuario envía credenciales. 2) El sistema valida la identidad. 3) El usuario opera proyectos autorizados. 4) El usuario cierra sesión.
- **Excepción:** Credenciales inválidas o demo-login deshabilitado. `X-User-Id` sólo se considera bootstrap de desarrollo/integración, no autenticación productiva.

### CU2: Crear, listar y recuperar proyectos/versiones

- **Propósito:** Gestionar el contenedor de trabajo y recuperar modelos o revisiones.
- **Resumen:** El usuario crea proyectos, lista proyectos visibles y consulta modelo/versiones.
- **Actores:** Usuario autenticado.
- **Actor iniciador:** Usuario autenticado.
- **Precondición:** Usuario con sesión o identidad válida y visibilidad sobre el proyecto.
- **Postcondición:** Proyecto, modelo o revisión disponible para consulta y edición autorizada.
- **Flujo principal:** 1) Crear proyecto. 2) Listar proyectos visibles. 3) Abrir modelo recuperable. 4) Consultar versiones. 5) Seleccionar revisión.
- **Excepción:** Proyecto no visible, inexistente o revisión no encontrada.

### CU3: Gestionar permisos, invitaciones, miembros y capacidades de proyecto

- **Propósito:** Administrar participación, capacidades e historial del proyecto.
- **Resumen:** El administrador controla miembros, invitaciones, capacidades y auditoría.
- **Actores:** Propietario o administrador de proyecto.
- **Actor iniciador:** Administrador de proyecto.
- **Precondición:** Proyecto existente y actor con permiso administrativo.
- **Postcondición:** Permisos actualizados y trazables.
- **Flujo principal:** 1) Consultar miembros e invitaciones. 2) Buscar cuentas sugeribles. 3) Actualizar miembro o estado. 4) Cambiar capacidad de crear diagramas. 5) Consultar historial.
- **Excepción:** Actor no autorizado, cuenta inexistente o invitación inválida.

### CU4: Crear diagramas/vistas y administrar permisos de diagrama

- **Propósito:** Crear vistas y controlar acceso específico al diagrama.
- **Resumen:** El administrador configura colaboración, colaboradores, administrador, acceso e historial de una vista.
- **Actores:** Administrador de diagrama, modelador con capacidad de crear diagrama.
- **Actor iniciador:** Administrador de diagrama.
- **Precondición:** Proyecto existente y actor con capacidad suficiente.
- **Postcondición:** Vista creada o permisos de diagrama actualizados.
- **Flujo principal:** 1) Crear vista. 2) Configurar colaboración. 3) Agregar o actualizar colaboradores. 4) Cambiar administrador. 5) Consultar acceso e historial.
- **Excepción:** Capacidad deshabilitada, colaborador inválido o vista inexistente.

### CU5: Editar estado semántico UML 2.5

- **Propósito:** Modificar el significado UML del modelo sin mezclarlo con la geometría visual.
- **Resumen:** El modelador crea o modifica clases, atributos, operaciones, relaciones y extremos.
- **Actores:** Modelador / colaborador.
- **Actor iniciador:** Modelador.
- **Precondición:** Proyecto/modelo abierto y permiso de edición suficiente.
- **Postcondición:** Estado semántico persistente, versionable y sincronizable.
- **Flujo principal:** 1) Abrir modelo o snapshot. 2) Editar elementos UML. 3) Enviar comandos de sincronización. 4) Registrar revisión.
- **Excepción:** Comando inválido, relación inconsistente o edición sin permiso.

### CU6: Editar layout, geometría y usar atajos web

- **Propósito:** Modificar la presentación visual sin alterar el estado semántico.
- **Resumen:** El usuario mueve nodos, ajusta rutas y usa atajos web de edición.
- **Actores:** Modelador, cliente web.
- **Actor iniciador:** Modelador.
- **Precondición:** Vista abierta con elementos editables.
- **Postcondición:** Layout persistido y sincronizable, separado de la semántica UML.
- **Flujo principal:** 1) Ajustar geometría. 2) Usar atajos aplicables. 3) Enviar comandos de layout. 4) Sincronizar cambios.
- **Excepción:** Elemento eliminado, atajo no aplicable o desconexión del cliente.

### CU7: Crear propuestas desde texto y revisar decisión

- **Propósito:** Registrar propuestas textuales y decidir su aplicación.
- **Resumen:** Las propuestas se crean, consultan y aceptan o descartan explícitamente.
- **Actores:** Modelador.
- **Actor iniciador:** Modelador.
- **Precondición:** Proyecto existente; función habilitada si se espera IA activa.
- **Postcondición:** Propuesta trazable; cambios aplicados sólo si son aceptados.
- **Flujo principal:** 1) Crear propuesta textual. 2) Consultar propuesta. 3) Revisar estado. 4) Aceptar o descartar.
- **Excepción:** IA textual desactivada por defecto (`AI_TEXT_PROPOSALS_ENABLED=false`) o propuesta inválida.

### CU8: Transcribir voz local y convertirla en propuesta

- **Propósito:** Usar voz como entrada para propuestas revisables.
- **Resumen:** El ASR local transcribe audio y el texto resultante alimenta el flujo de propuestas.
- **Actores:** Modelador, servicio ASR local.
- **Actor iniciador:** Modelador.
- **Precondición:** Sidecar ASR local disponible; audio `es-ES` de hasta 25 MiB.
- **Postcondición:** Texto transcrito disponible o error controlado; cualquier cambio queda sujeto a revisión.
- **Flujo principal:** 1) Enviar audio al ASR. 2) Transcribir. 3) Crear propuesta textual. 4) Revisar decisión.
- **Excepción:** Audio fuera de límite, idioma no soportado o ASR no disponible.

### CU9: Crear propuesta desde foto/Qwen y revisarla

- **Propósito:** Extraer candidatos desde imágenes mediante integración configurable.
- **Resumen:** La fotografía genera una propuesta que debe revisarse antes de modificar el modelo.
- **Actores:** Modelador, integración Qwen / extractor fotográfico.
- **Actor iniciador:** Modelador.
- **Precondición:** Integración fotográfica habilitada; Qwen configurado si se utiliza.
- **Postcondición:** Propuesta candidata aceptada, descartada o pendiente.
- **Flujo principal:** 1) Subir imagen. 2) Extraer candidato. 3) Consultar revisión. 4) Aceptar o descartar.
- **Excepción:** Qwen deshabilitado/no disponible, imagen inválida o extracción ambigua. Qwen es opt-in y no evidencia por sí mismo servicio real.

### CU10: Sincronizar comandos, snapshots y eventos SSE

- **Propósito:** Mantener colaboración mediante comandos HTTP, snapshots y eventos SSE.
- **Resumen:** Los clientes envían operaciones, recuperan estado y consumen eventos `text/event-stream`.
- **Actores:** Cliente web, cliente móvil, colaborador.
- **Actor iniciador:** Cliente web o móvil.
- **Precondición:** Proyecto/modelo existente y cliente con identificadores necesarios.
- **Postcondición:** Estado sincronizado dentro del alcance medido.
- **Flujo principal:** 1) Enviar comando HTTP. 2) Procesar lote si corresponde. 3) Obtener snapshot. 4) Consumir stream SSE. 5) Aplicar eventos.
- **Excepción:** Comando conflictivo, revisión atrasada o stream no disponible. No se documenta WebSocket como transporte vigente.

### CU11: Publicar y visualizar presencia

- **Propósito:** Informar actividad de participantes sin modificar el modelo.
- **Resumen:** La presencia se publica y se distribuye por el stream de eventos.
- **Actores:** Colaborador, cliente web, cliente móvil.
- **Actor iniciador:** Colaborador.
- **Precondición:** Proyecto/modelo abierto y cliente identificado.
- **Postcondición:** Actividad visible para otros clientes.
- **Flujo principal:** 1) Publicar presencia. 2) Recibir presencia por SSE. 3) Visualizar participantes. 4) Mantener presencia separada del estado semántico.
- **Excepción:** Cliente desconectado, proyecto inválido o falta de permiso.

### CU12: Listar, consultar y resolver conflictos

- **Propósito:** Resolver conflictos de sincronización de forma explícita.
- **Resumen:** El sistema lista conflictos, permite consultarlos y registra una resolución.
- **Actores:** Colaborador, cliente móvil.
- **Actor iniciador:** Colaborador o cliente móvil.
- **Precondición:** Operaciones concurrentes u offline pendientes.
- **Postcondición:** Conflicto resuelto o conservado si la resolución no es válida.
- **Flujo principal:** 1) Listar conflictos. 2) Consultar detalle. 3) Elegir resolución. 4) Registrar resolución. 5) Continuar sincronización.
- **Excepción:** Conflicto inexistente, ya resuelto o resolución inválida.

### CU13: Consumir desde móvil descriptor-driven con cola offline

- **Propósito:** Operar el sistema desde Flutter con tolerancia a desconexión.
- **Resumen:** La app móvil se guía por descriptores, encola comandos offline y sincroniza al reconectar.
- **Actores:** Cliente móvil, usuario móvil.
- **Actor iniciador:** Usuario móvil.
- **Precondición:** Aplicación móvil disponible y backend configurable.
- **Postcondición:** Comandos enviados o encolados; conflictos tratados por CU12.
- **Flujo principal:** 1) Obtener descriptor. 2) Renderizar capacidades. 3) Enviar comandos en línea. 4) Encolar comandos offline. 5) Sincronizar al reconectar.
- **Excepción:** Backend no disponible, conflicto de flush o prueba local sin integración live.

### CU14: Importar/exportar XMI con preview/confirmación

- **Propósito:** Intercambiar modelos por XMI declarando sus limitaciones.
- **Resumen:** La importación usa preview y confirmación; la exportación produce XMI desde el modelo.
- **Actores:** Modelador.
- **Actor iniciador:** Modelador.
- **Precondición:** Modelo existente o archivo XMI disponible.
- **Postcondición:** Exportación generada o importación confirmada conscientemente.
- **Flujo principal:** 1) Exportar XMI o iniciar preview de importación. 2) Revisar cambios y pérdidas. 3) Confirmar si el resultado es aceptable.
- **Excepción:** XMI inválido o pérdidas no aceptadas. XMI no es lossless y Q9 permanece abierto.

### CU15: Gestionar generación determinista, targets, perfiles, runs y artefactos

- **Propósito:** Ejecutar generación y descargar artefactos asociados al modelo.
- **Resumen:** El usuario configura targets/perfiles, inicia runs y descarga artefactos.
- **Actores:** Modelador, generador.
- **Actor iniciador:** Modelador.
- **Precondición:** Modelo suficiente, target y perfil disponibles.
- **Postcondición:** Run consultable y artefactos disponibles si la generación finaliza correctamente.
- **Flujo principal:** 1) Crear/listar targets. 2) Consultar perfiles. 3) Iniciar generación. 4) Consultar run. 5) Descargar artefactos.
- **Excepción:** Modelo inválido, perfil incompatible o artefacto ausente. La evidencia de determinismo es positiva pero acotada.

### CU16: Gestionar deployments

- **Propósito:** Administrar deployments asociados al proyecto.
- **Resumen:** El operador crea, consulta, actualiza, habilita, deshabilita o elimina deployments.
- **Actores:** Operador técnico, modelador autorizado.
- **Actor iniciador:** Operador técnico.
- **Precondición:** Proyecto existente y actor autorizado.
- **Postcondición:** Deployment creado, actualizado, consultado o con estado modificado.
- **Flujo principal:** 1) Crear deployment. 2) Listar y consultar. 3) Actualizar. 4) Habilitar o deshabilitar. 5) Eliminar cuando corresponda.
- **Excepción:** Deployment inexistente, estado incompatible o falta de permisos.

### CU17: Consultar salud, ejecutar comandos y conservar evidencia

- **Propósito:** Verificar el sistema y conservar evidencia técnica.
- **Resumen:** El operador consulta salud, ejecuta comandos documentados y registra resultados.
- **Actores:** Operador técnico, evaluador.
- **Actor iniciador:** Operador técnico o evaluador.
- **Precondición:** Entorno técnico disponible según comando: Java/Maven, Docker, Node, Flutter o sidecars.
- **Postcondición:** Evidencia técnica conservada y distinguida de supuestos de diseño.
- **Flujo principal:** 1) Consultar health. 2) Ejecutar comandos de backend/web/móvil cuando corresponda. 3) Conservar reportes E2E, presencia, geometría, generación y XMI.
- **Excepción:** Dependencias de entorno ausentes, Testcontainers sin Docker o integración live sin backend.

### CU18: Configurar almacenamiento local/S3/Floci para artefactos

- **Propósito:** Definir dónde se almacenan los artefactos de generación.
- **Resumen:** PostgreSQL es el modo por defecto; S3 y Floci son opciones explícitas de configuración.
- **Actores:** Operador técnico.
- **Actor iniciador:** Operador técnico.
- **Precondición:** Generación de artefactos habilitada y store configurado.
- **Postcondición:** Artefactos almacenados según configuración disponible.
- **Flujo principal:** 1) Usar PostgreSQL por defecto. 2) Configurar S3 si se requiere. 3) Habilitar Floci para pruebas locales opt-in. 4) Ejecutar generación y descarga desde CU15.
- **Excepción:** Credenciales o endpoint inválidos; servicio externo ausente. Floci local no demuestra AWS real.

## 2.1.4 Estructurar Modelo de Casos de Uso

El modelo se estructura por paquetes funcionales para facilitar su representación en un diagrama UML de casos de uso.

| Paquete funcional | Casos de uso incluidos |
|---|---|
| Identidad y proyectos | CU1, CU2 |
| Permisos y administración | CU3, CU4 |
| Modelado UML y layout | CU5, CU6 |
| Propuestas multimodales | CU7, CU8, CU9 |
| Colaboración y sincronización | CU10, CU11, CU12, CU13 |
| Intercambio, generación y operación | CU14, CU15, CU16, CU17, CU18 |

Relaciones principales del modelo:

- CU1 habilita el acceso a las operaciones protegidas, pero `X-User-Id` no debe considerarse autenticación de producción.
- CU5 y CU6 se mantienen separados porque la guía distingue estado semántico UML y layout visual.
- CU10 es la base colaborativa mediante comandos HTTP y eventos SSE; no se modela WebSocket como transporte vigente.
- CU8 y CU9 alimentan el flujo de propuestas CU7, sin aplicar cambios directamente al modelo.
- CU13 utiliza sincronización de CU10 y puede derivar en resolución de conflictos CU12.
- CU14 conserva la restricción académica y técnica de XMI parcial, no lossless.
- CU15 puede depender de CU18 para el almacenamiento de artefactos; S3/Floci son opt-in.

Para el diagrama de casos de uso en Word se recomienda ubicar el sistema como frontera central “Diagramador colaborativo UML 2.5”, agrupar los paquetes anteriores dentro de la frontera y conectar cada actor con los CU indicados en la sección 2.1.1.2. El artefacto `DIAGRAMA_CASOS_USO.md` contiene el diagrama Mermaid listo para renderizar, y `MATRIZ_ACTORES_CU_MODULOS.md` contiene la matriz de actores, módulos, fases, prioridades y notas de evidencia.
