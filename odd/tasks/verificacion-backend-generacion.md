# Verificación backend y generación

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Hacer que el backend CASE y el motor de generación sean más verificables aunque el entorno local no tenga Maven instalado.

## Tareas

- [x] Corregir casos borde del generador inicial.
- [x] Agregar Dockerfile para compilar/ejecutar backend CASE sin Maven local.
- [x] Agregar script de prueba API para el flujo proyecto → modelo → validación → generación.
- [x] Agregar GitHub Actions para compilar el backend en GitHub.
- [x] Verificar estructura y documentar uso.

## Criterios de aceptación

- [x] El generador produce DTO válidos incluso en entidades con pocos campos.
- [x] Existe una forma Docker de compilar/ejecutar backend CASE.
- [x] Existe un script reproducible de prueba del flujo principal.
- [x] GitHub puede ejecutar `mvn test` aunque la máquina local no tenga Maven.

## Archivos principales

- `backend-case/Dockerfile`
- `infraestructura/local/docker-compose.yml`
- `herramientas/scripts/probar-flujo-backend-case.sh`
- `.github/workflows/backend-case.yml`
- `backend-case/src/main/java/bo/edu/proyecto/caseapp/generacion/aplicacion/GeneradorProyectoSpring.java`

## Verificación ejecutada

- GitHub Actions `Backend CASE`: correcto.
- Docker build de `backend-case`: correcto.
- `docker compose -f infraestructura/local/docker-compose.yml up --build -d`: correcto.
- `curl http://localhost:8080/actuator/health`: `UP`.
- `herramientas/scripts/probar-flujo-backend-case.sh`: correcto.
- Descarga de ZIP generado: correcta.
- Compilación del backend generado con Docker/Maven: correcta.
- `mvn -f backend-case/pom.xml test` local no pudo ejecutarse porque Maven no está instalado (`mvn: command not found`), pero Docker y GitHub Actions cubrieron esa verificación.

## Corrección aplicada

La primera prueba real encontró que el controlador generado recibía tipos desplazados por un argumento extra en `.formatted(...)`. Se corrigió el orden de placeholders del controlador generado y se verificó compilando el backend generado.

## Estado

Completado y verificado con Docker + GitHub Actions.
