# Fase 1 - Ruta crítica demostrable

Fuente principal: `GUIA DEL PROYECTO.txt`, `documentacion/requisitos/CASOS_USO_MVP.md` y `documentacion/requisitos/PLAN_TRABAJO_CU_PRIORIZADO.md`.

## Objetivo

Implementar y verificar la ruta crítica demostrable de los casos de uso CU1, CU2, CU5, CU6, CU10 y CU15, preservando compatibilidad con el flujo CASE ya construido.

## Tareas

- [x] Implementar autenticación/demo mínima para CU1.
- [x] Implementar recuperación de proyecto/modelo y versiones para CU2.
- [x] Completar edición semántica UML mínima para CU5.
- [x] Separar layout visual mínimo para CU6.
- [x] Agregar sincronización HTTP + snapshot + SSE para CU10.
- [x] Agregar targets, perfiles, runs, artefactos/hash para CU15.
- [x] Actualizar frontend mínimo para exponer la ruta crítica.
- [x] Ejecutar verificación disponible y documentar evidencia.

## Criterios de aceptación

- [x] CU1 permite login, demo-login y logout documentados.
- [x] CU2 permite crear/listar/recuperar proyecto/modelo y consultar versiones.
- [x] CU5 conserva estado semántico UML mínimo.
- [x] CU6 conserva layout visual separado o claramente puenteado.
- [x] CU10 expone comandos HTTP, snapshot y stream SSE.
- [x] CU15 expone generación con target/perfil/run/artefacto/hash.
- [x] Se preservan endpoints existentes del flujo CASE.
- [ ] Backend compilado/verificado en entorno local.
- [x] Frontend compilado/verificado en entorno local.

## Estado

Implementado como capa incremental de compatibilidad, con verificación backend pendiente por limitación del entorno.

## Evidencia

- Backend: `mvn -f backend-case/pom.xml test` no pudo ejecutarse porque Maven no está disponible (`mvn: command not found`).
- Backend Docker: `docker compose -f infraestructura/local/docker-compose.yml build backend-case` no pudo ejecutarse porque Docker Desktop/daemon no está corriendo.
- Frontend: `cd frontend-case && npm run compilar` ejecutado correctamente.
- Documento técnico: `documentacion/requisitos/EVIDENCIA_FASE_1_RUTA_CRITICA.md`.

## Brechas conocidas

- Autenticación solo demo/académica; `X-User-Id` no es seguridad productiva.
- Versiones como revisión actual; no historial persistente completo.
- Operaciones UML expuestas como puente compatible (`operaciones: []`), sin persistencia propia.
- SSE en memoria/proceso local.
