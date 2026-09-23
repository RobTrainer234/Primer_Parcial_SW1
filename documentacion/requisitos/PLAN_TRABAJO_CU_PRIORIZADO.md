# Plan de trabajo priorizado por casos de uso

## Decisión principal

El trabajo pendiente se organiza en fases para cubrir los casos de uso CU1-CU18 definidos desde `GUIA DEL PROYECTO.txt`. La prioridad se ordena por dependencia: primero la base documental y la ruta crítica demostrable, luego colaboración y administración, después integraciones avanzadas, y finalmente evidencias de cierre.

## Estado general

| Estado | Significado |
|---|---|
| Hecho inicial | Existe implementación o documentación base, pero puede requerir ajuste frente a la guía completa. |
| Pendiente funcional | Falta implementar o completar comportamiento del sistema. |
| Pendiente documental | Falta trasladar, ampliar o adaptar contenido al documento académico. |
| Pendiente evidencia | Falta captura, prueba, reporte o demostración verificable. |

## Fase 0: Alinear documentación académica

**Prioridad:** inmediata.
**Objetivo:** que el documento del proyecto refleje correctamente el alcance de la guía.

| CU | Trabajo pendiente | Tipo | Prioridad | Estado |
|---|---|---|---|---|
| CU1-CU18 | Copiar `SECCION_CASOS_USO_PARA_WORD.md` al Word del proyecto. | Documental | Alta | Pendiente manual |
| CU1-CU18 | Revisar numeración, tabla de contenido y formato del Word. | Documental | Alta | Pendiente manual |
| CU1-CU18 | Agregar diagrama UML visual de casos de uso a partir del modelo textual. | Documental | Alta | Artefacto listo: `DIAGRAMA_CASOS_USO.md` |
| CU1-CU18 | Relacionar actores, casos de uso y módulos del sistema. | Documental | Alta | Artefacto listo: `MATRIZ_ACTORES_CU_MODULOS.md` |

**Resultado esperado:** la sección `2.1 FT: Captura de Requisitos` queda preparada para el documento del proyecto. Los artefactos de diagrama y matriz están listos; la copia al Word, el formato final y la actualización de tabla de contenido siguen siendo acciones manuales.

## Fase 1: Ruta crítica demostrable

**Prioridad:** máxima.
**Objetivo:** asegurar que el sistema pueda demostrarse de punta a punta.

| CU | Trabajo pendiente | Tipo | Prioridad |
|---|---|---|---|
| CU1 | Confirmar autenticación real o mecanismo demo documentado. | Funcional / evidencia | Alta — capa demo implementada |
| CU2 | Verificar creación, listado y recuperación de proyectos/modelos. | Evidencia | Alta — aliases `/projects` implementados |
| CU5 | Completar o ajustar edición semántica UML según guía: clases, atributos, operaciones, relaciones y extremos. | Funcional | Alta — mínimo semántico preservado |
| CU6 | Verificar layout visual y atajos web documentados. | Funcional / evidencia | Alta — layout puenteado en snapshot |
| CU10 | Confirmar sincronización por HTTP+SSE, no WebSocket. | Funcional / evidencia | Alta — snapshot/comandos/SSE implementados |
| CU15 | Verificar generación determinista y descarga de artefactos. | Evidencia | Alta — targets/perfiles/runs/hash implementados |

**Resultado esperado:** se puede demostrar: iniciar sesión o demo, abrir proyecto, editar modelo, sincronizar, generar artefacto y descargarlo.

**Evidencia Fase 1:** ver `documentacion/requisitos/EVIDENCIA_FASE_1_RUTA_CRITICA.md`. La implementación es una capa incremental compatible; quedan como brechas el auth productivo, historial real de versiones, operaciones UML persistentes y SSE distribuido.

## Fase 2: Administración y colaboración

**Prioridad:** alta.
**Objetivo:** cubrir las superficies colaborativas y de permisos exigidas por la guía.

| CU | Trabajo pendiente | Tipo | Prioridad |
|---|---|---|---|
| CU3 | Verificar permisos de proyecto: invitaciones, miembros, capacidades e historial. | Funcional / evidencia | Alta — capa demo implementada |
| CU4 | Verificar permisos de diagrama: colaboración, colaboradores, administrador, acceso e historial. | Funcional / evidencia | Alta — capa demo implementada |
| CU11 | Probar presencia y documentar que no modifica estado semántico. | Evidencia | Media-alta — HTTP+SSE demo implementado |
| CU12 | Probar conflictos: listar, consultar y resolver. | Funcional / evidencia | Media-alta — conflicto por baseRevision implementado |

**Resultado esperado:** el sistema demuestra administración colaborativa y resolución explícita de conflictos.

**Evidencia Fase 2:** ver `documentacion/requisitos/EVIDENCIA_FASE_2_COLABORACION_ADMIN.md`. La implementación es una capa incremental en memoria para cobertura académica/demo; quedan como brechas la persistencia productiva de permisos, autorización real, SSE distribuido y merge semántico de conflictos.

## Fase 3: Móvil e integraciones de entrada

**Prioridad:** media-alta.
**Objetivo:** cubrir móvil, propuestas e integraciones opt-in sin prometer más de lo verificado.

| CU | Trabajo pendiente | Tipo | Prioridad |
|---|---|---|---|
| CU7 | Documentar y probar propuestas desde texto con revisión. | Funcional / evidencia | Media — capa demo implementada |
| CU8 | Integrar o documentar ASR local `faster-whisper`, `es-ES`, máximo 25 MiB. | Funcional / evidencia | Media — backend proxy/control de no disponibilidad implementado |
| CU9 | Documentar foto/Qwen como opt-in y probar si el entorno lo permite. | Funcional / evidencia | Media — placeholder opt-in trazable implementado |
| CU13 | Verificar móvil descriptor-driven y cola offline. | Funcional / evidencia | Media-alta — descriptor y cola en memoria implementados |

**Resultado esperado:** móvil y propuestas multimodales quedan demostrados o documentados con sus limitaciones reales.

**Evidencia Fase 3:** ver `documentacion/requisitos/EVIDENCIA_FASE_3_MOVIL_INTEGRACIONES.md`. La implementación conserva compatibilidad de Fases 1/2, evita mutación automática del modelo al aceptar propuestas y declara como brecha la persistencia productiva de propuestas, el servicio ASR externo y Qwen real.

## Fase 4: Intercambio, despliegue y almacenamiento

**Prioridad:** media.
**Objetivo:** cerrar capacidades técnicas requeridas por la guía.

| CU | Trabajo pendiente | Tipo | Prioridad |
|---|---|---|---|
| CU14 | Probar importación/exportación XMI y documentar pérdidas; no declararlo lossless. | Evidencia / documental | Media — capa parcial implementada |
| CU16 | Verificar endpoints o flujo de deployments. | Funcional / evidencia | Media — demo en memoria implementado |
| CU18 | Documentar almacenamiento PostgreSQL/local por defecto y S3/Floci como opt-in. | Documental / evidencia | Media — estado/configuración implementado |

**Resultado esperado:** XMI, deployments y almacenamiento quedan cubiertos con alcance realista.

**Evidencia Fase 4:** ver `documentacion/requisitos/EVIDENCIA_FASE_4_INTERCAMBIO_DESPLIEGUE_ALMACENAMIENTO.md`. La implementación conserva compatibilidad de Fases 1/2/3, declara XMI como no lossless, mantiene deployments en memoria y expone S3/Floci como placeholders opt-in sin dependencia pesada.

## Fase 5: Pruebas, evidencias y cierre académico

**Prioridad:** alta para defensa final.
**Objetivo:** demostrar que lo construido y documentado está respaldado por evidencia.

| CU | Trabajo pendiente | Tipo | Prioridad |
|---|---|---|---|
| CU17 | Ejecutar health checks y comandos de verificación disponibles. | Evidencia | Alta |
| CU1-CU18 | Crear matriz CU → prueba → evidencia. | Documental / evidencia | Alta |
| CU1-CU18 | Agregar capturas por flujo principal. | Evidencia | Alta |
| CU1-CU18 | Registrar errores encontrados y correcciones aplicadas. | Evidencia | Media-alta |
| CU1-CU18 | Preparar simulacro de defensa con guion. | Documental / evidencia | Alta |

**Resultado esperado:** el proyecto tiene respaldo verificable para la defensa: pruebas, capturas, errores corregidos y guion de demostración.

## Orden recomendado de ejecución

1. Pasar al Word la sección de casos de uso ya preparada.
2. Crear el diagrama visual UML de casos de uso.
3. Construir matriz CU → módulo → prueba → evidencia.
4. Verificar la ruta crítica: CU1, CU2, CU5, CU6, CU10 y CU15.
5. Completar administración y colaboración: CU3, CU4, CU11 y CU12.
6. Completar móvil/propuestas: CU7, CU8, CU9 y CU13.
7. Revisar XMI, deployments y almacenamiento: CU14, CU16 y CU18.
8. Ejecutar health/evidencias y preparar defensa: CU17.

## Próximo sprint recomendado

El próximo sprint debería enfocarse en integrar manualmente la fase 0 al Word y reunir evidencia de ruta crítica:

- Copiar `SECCION_CASOS_USO_PARA_WORD.md` al Word.
- Insertar en Word el diagrama renderizado desde `DIAGRAMA_CASOS_USO.md`.
- Usar `MATRIZ_ACTORES_CU_MODULOS.md` como base de trazabilidad CU → módulo → prueba → evidencia.
- Revisar numeración, formato y tabla de contenido del Word.
- Ejecutar o documentar la prueba de la ruta crítica CU1, CU2, CU5, CU6, CU10 y CU15.
