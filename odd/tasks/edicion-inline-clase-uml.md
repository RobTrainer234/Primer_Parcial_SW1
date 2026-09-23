# Edición inline de clase UML

## Objetivo
Permitir editar el contenido de una clase directamente sobre la tarjeta UML del canvas, sin obligar al usuario a abandonar la clase.

## Ediciones
- Doble clic en nombre: input inline y persistencia al salir/Enter.
- Doble clic en tabla JPA: input inline y persistencia.
- Botón `+ Atributo`: agrega atributo temporal/persistible y abre edición inline.
- Doble clic en atributo: nombre, tipo y modificadores UML/JPA editables inline cuando el contrato lo permite.
- Enter guarda; Escape cancela; blur guarda.
- Inspector derecho permanece como edición detallada alternativa.

## Reglas UX
- Inputs y botones no deben iniciar el arrastre del nodo.
- La selección visual debe permanecer en la clase editada.
- Un error de persistencia debe dejar toast y no ocultar silenciosamente el dato.
- La tarjeta conserva notación UML: visibilidad, nombre, tipo y flags.
- La edición no cambia el tipo de relación ni la geometría.

## Aceptación
- Clase nueva seleccionada permite editar nombre inline.
- Se puede agregar y editar atributo sin salir del canvas.
- Cambios persisten con modelo activo y se recuperan al recargar.
- Clase sin modelo mantiene edición local honesta.
- Angular compila.
