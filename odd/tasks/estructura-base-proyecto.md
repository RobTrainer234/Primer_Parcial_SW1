# Estructura base del proyecto

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Crear la estructura inicial del monorepo para la herramienta CASE, con documentación base, planificación ejecutable y esqueletos para backend, frontend, móvil, infraestructura, evidencias y herramientas.

## Tareas

- [x] Explorar estructura actual del repositorio y detectar archivos existentes.
- [x] Crear documentación base del proyecto y planificación por fases.
- [x] Crear estructura monorepo con carpetas principales y esqueletos.
- [x] Verificar que la estructura creada sea consistente y no pise archivos existentes.

## Criterios de aceptación

- [x] Existe una organización clara para `backend-case`, `frontend-case`, `movil`, `infraestructura`, `documentacion`, `evidencias`, `herramientas` y `generados`.
- [x] La documentación inicial explica objetivo, arquitectura, fases y próximos pasos.
- [x] Los esqueletos no dependen de instalaciones externas para existir.
- [x] No se borran archivos previos.

## Verificación ejecutada

- `python` verificó la existencia de 9 archivos clave de la estructura base.
- `mvn -f backend-case/pom.xml test` no pudo ejecutarse porque Maven no está instalado en el entorno (`mvn: command not found`).
- `git status --short` no pudo ejecutarse porque esta carpeta todavía no está inicializada como repositorio Git.

## Estado

Completado.
