# Proyecto CASE para generación de backend

Este repositorio contiene la base del proyecto de Software I: una herramienta CASE para modelar conceptualmente una base de datos y generar automáticamente un backend Spring Boot consumible desde Flutter.

La fuente principal de alcance es `GUIA DEL PROYECTO.txt`.

## Objetivo principal

Construir una herramienta capaz de ejecutar este flujo:

```text
Diseñar modelo conceptual
→ Validar modelo
→ Generar backend Spring Boot
→ Ejecutar con PostgreSQL
→ Probar Swagger/OpenAPI
→ Consumir desde Flutter
→ Usar asistente local offline
→ Demostrar versión local y AWS
```

## Estructura del repositorio

```text
backend-case/       Backend principal de la herramienta CASE.
frontend-case/      Frontend web del editor conceptual.
movil/              Aplicación Flutter reutilizable.
generados/          Salida de backends generados durante pruebas.
infraestructura/    Configuración local y AWS.
documentacion/      Documentación académica y técnica.
evidencias/         Capturas, pruebas, despliegues y simulacros.
herramientas/       Plantillas y scripts auxiliares.
odd/                Seguimiento de trabajo del proyecto.
```

## Prioridad de desarrollo

1. Modelo conceptual.
2. Validación.
3. Modelo intermedio.
4. Generador determinista.
5. Backend generado ejecutable.
6. Flutter consumidor.
7. Asistente local offline.
8. Despliegue local y AWS.
9. Documentación y evidencias.

## Estado actual

Estructura base creada. Los módulos todavía son esqueletos preparados para implementación.

## Próximo paso recomendado

Implementar el `backend-case` con gestión de proyectos y modelo conceptual persistente.
