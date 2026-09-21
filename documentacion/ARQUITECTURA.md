# Arquitectura del proyecto

## Decisión principal

El proyecto se organiza como monorepo con un backend CASE, un frontend web, una app móvil, infraestructura, documentación y evidencias.

## Vista de alto nivel

```text
Frontend CASE
    ↓ REST
Backend CASE
    ↓
PostgreSQL CASE
    ↓
Motor de generación
    ↓
Backend Spring Boot generado
    ↓ REST
Aplicación Flutter
    ↓
Asistente local offline
```

## Backend CASE

Arquitectura recomendada: monolito modular con arquitectura hexagonal liviana.

```text
backend-case/src/main/java/bo/edu/proyecto/caseapp/
├── compartido/
├── proyectos/
├── modelado/
├── validacion/
├── generacion/
├── artefactos/
└── configuracion/
```

Cada módulo puede dividirse internamente en:

```text
dominio/
aplicacion/
infraestructura/
presentacion/
```

## Frontend CASE

Frontend web organizado por funcionalidades.

```text
frontend-case/src/app/
├── nucleo/
├── compartido/
├── funcionalidades/
└── layout/
```

## Flutter

Aplicación móvil reutilizable.

```text
movil/lib/
├── nucleo/
├── compartido/
└── funcionalidades/
```

## Motor de generación

El generador debe ser determinista.

```text
Modelo conceptual
→ Modelo intermedio
→ Reglas de transformación
→ Plantillas
→ Archivos generados
→ ZIP
```

No se debe depender de IA generativa para producir código crítico.

## Principios técnicos

- Monorepo para simplificar defensa y evidencia.
- Backend principal como monolito modular.
- Separación entre modelo visual y modelo intermedio.
- Generación por reglas y plantillas.
- PostgreSQL como base principal.
- Swagger/OpenAPI en el backend generado.
- Flutter como consumidor del backend generado.
- Asistente local offline basado en contexto y conocimiento local.
