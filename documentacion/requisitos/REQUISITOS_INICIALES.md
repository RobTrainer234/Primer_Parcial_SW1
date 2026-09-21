# Requisitos iniciales

## Actores

| Actor | Descripción |
|---|---|
| Desarrollador o modelador | Crea proyectos, modela entidades, valida modelos y genera backend. |
| Usuario móvil | Consume la aplicación móvil y consulta el asistente local. |

## Casos de uso mínimos

| Código | Caso de uso | Prioridad |
|---|---|---|
| CU1 | Gestionar proyecto | Alta |
| CU2 | Crear y editar modelo conceptual | Alta |
| CU3 | Gestionar entidades | Alta |
| CU4 | Gestionar atributos y claves | Alta |
| CU5 | Gestionar relaciones y cardinalidades | Alta |
| CU6 | Guardar y recuperar modelo | Alta |
| CU7 | Validar modelo conceptual | Alta |
| CU8 | Generar backend Spring Boot | Alta |
| CU9 | Descargar backend generado | Alta |
| CU10 | Probar API generada | Alta |
| CU11 | Consumir backend desde Flutter | Media |
| CU12 | Consultar asistente local offline | Media |

## Requisitos funcionales principales

- Crear, consultar, editar y eliminar proyectos.
- Crear modelos conceptuales.
- Crear entidades con atributos, tipos, claves y restricciones.
- Crear relaciones con cardinalidades uno a uno, uno a muchos y muchos a muchos.
- Guardar y recuperar diagramas.
- Validar consistencia del modelo antes de generar.
- Generar backend Spring Boot con PostgreSQL y Swagger.
- Descargar el backend generado como ZIP.
- Consumir la API generada desde Flutter.
- Consultar un asistente local sin conexión.

## Requisitos no funcionales principales

- El generador debe ser determinista.
- El backend generado debe compilar sin correcciones manuales estructurales.
- La versión local debe poder demostrarse sin depender de Internet.
- El proyecto debe conservar evidencias de pruebas.
- La arquitectura debe permitir evolución sin microservicios en el MVP.
