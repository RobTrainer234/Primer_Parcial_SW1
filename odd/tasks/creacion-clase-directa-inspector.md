# Creación directa de clase UML con inspector

## Decisión
El botón `Clase UML persistible` crea inmediatamente una clase en el canvas, la selecciona y abre el inspector. Se elimina el diálogo previo de nombre/ubicación.

## Flujo
1. Pulsar `Clase UML persistible`.
2. Crear clase con nombre temporal UML/Java válido (`NuevaClase`, `NuevaClase2`, etc.) en una posición libre.
3. Seleccionarla y abrir inspector de clase.
4. Editar nombre y tabla en inspector.
5. Agregar atributos desde inspector/caja.
6. Persistir cada modificación con el modelo activo.
7. La herramienta vuelve a `select`; los clics posteriores no crean clases.

## Reglas
- No usar modal para la creación básica.
- No dejar modo `entity` persistente.
- Evitar solapamiento inicial de clases.
- Mantener clase temporal compatible con las reglas del backend.
- Explicar en la paleta que la edición ocurre en el inspector.
- Mantener relaciones y herramientas existentes.

## Aceptación
- Un clic en el botón crea exactamente una clase.
- La clase aparece seleccionada y editable inmediatamente.
- El nombre puede cambiarse desde inspector.
- Se pueden añadir atributos desde inspector.
- Un clic posterior en canvas no crea otra clase.
- Angular compila y Docker se actualiza.
