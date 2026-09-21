# Aplicación móvil

Aplicación Flutter reutilizable para consumir el backend generado y demostrar el asistente local offline.

## Responsabilidades

- Configurar URL del backend generado.
- Consumir endpoints REST.
- Mostrar pantallas CRUD reutilizables.
- Cargar contexto local de la aplicación.
- Responder preguntas básicas sin conexión.

## Organización

```text
nucleo/           API, configuración, rutas y almacenamiento.
compartido/       Widgets, modelos y utilidades.
funcionalidades/  Operaciones CRUD y asistente.
assets/           Contexto y conocimiento local.
```

## Próximo paso

Inicializar Flutter y crear cliente HTTP configurable.
