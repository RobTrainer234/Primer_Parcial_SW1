# Toasts para acciones principales

## Objetivo
Mostrar notificaciones temporales y no intrusivas para confirmar acciones importantes del workbench.

## Acciones cubiertas
- login y logout;
- crear proyecto y modelo;
- crear clase, atributo y relación;
- actualizar relación;
- validación UML;
- sincronización;
- generación Spring;
- exportación/importación;
- errores de API y validación de sesión.

## Comportamiento
- Toast de éxito, información o error.
- Duración breve configurable.
- Reemplaza el toast anterior sin acumular mensajes.
- Permite cierre manual.
- Posicionado en esquina superior derecha sin tapar el inspector.
- No modifica contratos ni datos.

## Aceptación
- Las acciones importantes muestran confirmación temporal.
- Los errores muestran mensaje legible.
- El toast desaparece automáticamente.
- El usuario puede cerrarlo manualmente.
- Angular compila y la interfaz conserva sus bindings.
