# Motor de generación inicial

Fuente principal: `GUIA DEL PROYECTO.txt`.

## Objetivo

Implementar la primera versión del motor determinista que transforma un modelo conceptual válido en un backend Spring Boot básico descargable como ZIP.

## Tareas

- [x] Crear modelo intermedio limpio para generación.
- [x] Implementar generación de proyecto Spring Boot por reglas.
- [x] Registrar trabajos de generación y artefactos.
- [x] Exponer endpoints de generación y descarga.
- [x] Verificar estructura generada y documentar uso.

## Alcance incluido

- Conversión de entidades, atributos y relaciones al modelo intermedio.
- Generación de `pom.xml`, aplicación principal, configuración, entidades JPA, DTO, repositorios, servicios, controladores y excepciones.
- Empaquetado ZIP.
- Registro de trabajo de generación.
- Descarga del artefacto generado.

## Fuera de alcance por ahora

- Generación perfecta de relaciones complejas en DTO/JPA.
- Frontend para invocar generación.
- Ejecución automática del backend generado.
- AWS.

## Criterios de aceptación

- [x] `POST /modelos/{modeloId}/generaciones` genera un trabajo.
- [x] Si el modelo es inválido, no genera artefacto y marca el trabajo como fallido.
- [x] Si el modelo es válido, crea un ZIP en almacenamiento local.
- [x] `GET /generaciones/{id}` consulta estado.
- [x] `GET /generaciones/{id}/artefacto` descarga el ZIP.

## Endpoints creados

```http
POST /modelos/{modeloId}/generaciones
GET  /generaciones/{generacionId}
GET  /generaciones/{generacionId}/artefacto
```

## Verificación ejecutada

- Chequeo estructural con Python: archivos clave presentes.
- Chequeo liviano de llaves y declaraciones `package`: correcto.
- `mvn -f backend-case/pom.xml test` no pudo ejecutarse porque Maven no está instalado (`mvn: command not found`).

## Estado

Completado con verificación limitada por entorno.
