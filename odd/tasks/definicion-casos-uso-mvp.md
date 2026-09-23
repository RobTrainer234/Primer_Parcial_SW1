# Definición de casos de uso derivados de la guía completa

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Corregir el catálogo anterior, que reducía el alcance a un MVP CU1-CU12, para que el catálogo de casos de uso represente **todas las superficies funcionales documentadas en `GUIA DEL PROYECTO.txt`**. El archivo histórico `documentacion/requisitos/CASOS_USO_MVP.md` conserva su nombre, pero su contenido queda definido como catálogo guía-derivado y no como recorte reducido.

## Tareas

- [x] Leer `GUIA DEL PROYECTO.txt` como autoridad de alcance.
- [x] Identificar las superficies funcionales completas: autenticación, proyectos, permisos, diagramas, semántica UML, layout, propuestas texto/voz/foto, ASR local, Qwen opt-in, sincronización, presencia, conflictos, móvil offline, XMI, generación, deployments, salud/evidencia y almacenamiento.
- [x] Reemplazar el catálogo reducido CU1-CU12 por un catálogo amplio guía-derivado.
- [x] Incluir tabla de trazabilidad guía/API -> CU.
- [x] Incluir prioridad/estado por CU: MVP-crítico, requerido por guía, opcional/opt-in/integración o limitación conocida/issue abierto.
- [x] Para cada CU, documentar actor, objetivo, prioridad, fuente guía, precondiciones, flujo principal, alternos/error, criterios de aceptación y estado/brecha.
- [x] Actualizar `documentacion/requisitos/REQUISITOS_INICIALES.md` para que no resuma CU1-CU12 como catálogo completo.
- [x] Evitar edición de archivos `.docx`.

## Criterios de aceptación

- [x] `documentacion/requisitos/CASOS_USO_MVP.md` declara explícitamente que el catálogo se deriva de la guía completa y no excluye funcionalidades requeridas por la guía.
- [x] Cada sección/API surface de `GUIA DEL PROYECTO.txt` mapea al menos a un CU.
- [x] El catálogo cubre autenticación, proyectos/versiones, permisos, diagramas, edición semántica, layout, propuestas multimodales, ASR, Qwen/foto, sincronización, presencia, conflictos, móvil offline, XMI, generación, deployments, salud/evidencia y almacenamiento S3/Floci opt-in.
- [x] `REQUISITOS_INICIALES.md` enlaza el catálogo guía-derivado y resume por grupos, no como CU1-CU12 reducido.
- [x] La documentación permanece en español.

## Evidencia

- Se leyó `GUIA DEL PROYECTO.txt` y se usó como autoridad de alcance.
- Se reescribió `documentacion/requisitos/CASOS_USO_MVP.md` con 18 CUs guía-derivados, tabla resumen, trazabilidad y especificación por CU.
- Se actualizó `documentacion/requisitos/REQUISITOS_INICIALES.md` con alcance corregido, actores ampliados, resumen agrupado y requisitos funcionales/no funcionales alineados a la guía.
- No se editó ningún archivo `.docx`.

## Estado

Completado: catálogo corregido de reducido MVP a guía completa.
