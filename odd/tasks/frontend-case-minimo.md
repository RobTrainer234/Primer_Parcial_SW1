# Frontend CASE mínimo

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Implementar una primera interfaz Angular funcional para demostrar el flujo principal del proyecto: crear proyecto, crear modelo, agregar entidad y atributos, validar, generar backend y descargar ZIP.

## Tareas

- [x] Crear configuración Angular base.
- [x] Crear cliente API para el backend CASE.
- [x] Crear modelos TypeScript del flujo mínimo.
- [x] Crear pantalla principal con formularios simples.
- [x] Verificar compilación o estructura disponible.
- [ ] Subir cambios al repositorio.

## Alcance incluido

- UI funcional sin editor visual avanzado.
- Creación de proyecto.
- Creación de modelo.
- Creación de entidad.
- Creación de atributos.
- Validación del modelo.
- Generación de backend.
- Descarga del ZIP.

## Fuera de alcance por ahora

- Canvas drag and drop.
- Relaciones desde UI.
- Autenticación.
- Diseño visual definitivo.
- Flutter.

## Criterios de aceptación

- [x] La app Angular puede apuntar a `http://localhost:8080`.
- [x] El usuario puede ejecutar el flujo principal desde una sola pantalla.
- [x] La interfaz muestra errores básicos y resultados de validación/generación.
- [x] La descarga de artefacto usa el endpoint real del backend.

## Verificación ejecutada

- `npm install`: correcto, con advertencias de auditoría npm sobre dependencias transitivas.
- `npm run compilar`: correcto.
- `docker compose -f infraestructura/local/docker-compose.yml build backend-case`: correcto tras agregar CORS.

## Estado

Implementado localmente. Pendiente commit/push.
