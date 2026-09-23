# Requisitos iniciales

## Alcance corregido

El catálogo operativo ya no se limita a CU1-CU12 ni a un MVP reducido. La fuente autoridad para esta corrección es `GUIA DEL PROYECTO.txt`, que describe un diagramador colaborativo UML 2.5 con web, móvil, autenticación, permisos, colaboración HTTP+SSE, presencia, voz local, foto/Qwen opt-in, XMI parcial, generación determinista, deployments, almacenamiento de artefactos y evidencia de pruebas.

El detalle completo está en [`CASOS_USO_MVP.md`](CASOS_USO_MVP.md). Ese documento mantiene el nombre histórico del archivo, pero el contenido representa el **catálogo derivado de la guía completa**. La sección académica lista para copiar al documento del proyecto está en [`MODELO_CASOS_USO.md`](MODELO_CASOS_USO.md).

## Actores principales

| Actor | Descripción |
|---|---|
| Usuario autenticado / demo | Inicia sesión, accede a proyectos visibles y opera según permisos. |
| Propietario o administrador de proyecto | Gestiona invitaciones, miembros, capacidades e historial de permisos. |
| Administrador de diagrama | Administra vistas, colaboradores, acceso efectivo e historial del diagrama. |
| Modelador / colaborador | Edita modelo UML, layout, propuestas, sincronización, presencia y conflictos. |
| Cliente web | Consume comandos HTTP, snapshots y eventos SSE. |
| Cliente móvil | Consume capacidades descriptor-driven y sincroniza una cola offline. |
| Servicio ASR local | Transcribe voz `es-ES` para alimentar propuestas revisables. |
| Integración Qwen / extractor fotográfico | Extrae propuestas desde imágenes cuando se habilita explícitamente. |
| Generador | Ejecuta generación determinista y publica artefactos. |
| Operador técnico / evaluador | Ejecuta salud, pruebas, evidencia, deployments y almacenamiento. |

## Resumen agrupado de casos de uso

| Grupo | Casos de uso del catálogo guía-derivado |
|---|---|
| Identidad y proyectos | CU1 Autenticación/sesión/demo; CU2 proyectos, recuperación y versiones. |
| Permisos y colaboración administrativa | CU3 permisos de proyecto; CU4 diagramas/vistas y permisos de diagrama. |
| Modelado UML y layout | CU5 edición semántica UML; CU6 layout, geometría y atajos web. |
| Propuestas multimodales | CU7 texto; CU8 voz ASR local; CU9 foto/Qwen opt-in. |
| Sincronización colaborativa | CU10 comandos/snapshots/SSE; CU11 presencia; CU12 conflictos. |
| Móvil | CU13 consumo descriptor-driven y cola offline. |
| Intercambio y generación | CU14 XMI preview/confirm/export no lossless; CU15 generación determinista, targets, perfiles, runs y artefactos. |
| Operación | CU16 deployments; CU17 salud/evidencia/comandos; CU18 almacenamiento PostgreSQL/S3/Floci opt-in. |

## Requisitos funcionales principales

- Autenticar usuarios, cerrar sesión y habilitar login demo sólo bajo configuración de usuarios demo.
- Crear, listar, recuperar proyectos y consultar versiones/revisiones.
- Gestionar permisos de proyecto: invitaciones, miembros, sugerencias de cuentas, capacidades e historial.
- Crear diagramas/vistas y administrar colaboración, colaboradores, administrador, acceso efectivo e historial.
- Editar el modelo UML semántico: clases/entidades, atributos, operaciones, relaciones y extremos.
- Mantener separado el estado semántico del layout visual: nodos, posiciones y rutas.
- Soportar atajos web de guardado, deshacer/rehacer, copiar/pegar, duplicar y eliminar cuando apliquen.
- Crear propuestas desde texto y someterlas a revisión antes de aplicar cambios.
- Transcribir voz mediante ASR local `faster-whisper` (`es-ES`, 25 MiB máximo) y convertirla en propuesta revisable.
- Crear propuestas desde foto/Qwen como integración opt-in configurable y revisable.
- Sincronizar comandos HTTP, snapshots y eventos SSE; no documentar WebSocket como transporte vigente.
- Publicar y visualizar presencia sin confundirla con estado semántico.
- Listar, consultar y resolver conflictos explícitamente.
- Consumir el sistema desde móvil Flutter descriptor-driven con cola offline y flush posterior.
- Importar XMI con preview, confirmar importación y exportar XMI, declarando que no es lossless.
- Gestionar targets, perfiles, ejecuciones, artefactos y descarga de generación determinista.
- Crear, listar, consultar, actualizar, habilitar, deshabilitar y eliminar deployments.
- Consultar salud del backend y conservar evidencia de pruebas y comandos por componente.
- Configurar almacenamiento de artefactos local/PostgreSQL por defecto y S3/Floci sólo como opt-in.

## Requisitos no funcionales y restricciones principales

- El generador debe ser determinista dentro del alcance evidenciado; la evidencia disponible no prueba todos los modelos, targets o perfiles.
- La arquitectura debe preservar la separación entre semántica UML y layout visual.
- La colaboración vigente se basa en comandos HTTP y SSE/eventos.
- La documentación debe distinguir diseño previsto, configuración y evidencia medida.
- XMI tiene limitación conocida: no es roundtrip completo y Q9 sigue abierto.
- Texto IA está desactivado por defecto; Qwen, fotografía, S3 y Floci requieren opt-in explícito.
- Floci local no demuestra conectividad ni disponibilidad de AWS real.
- `X-User-Id` es identidad de bootstrap para desarrollo/integración, no autenticación productiva.
- Los comandos de verificación pueden depender de Docker, Testcontainers, Node, Flutter, sidecars o entorno live.
