# Bloquear clases locales en flujo persistible CU2

## Problema
La herramienta `Clase UML persistible` permitía crear una tarjeta local cuando había sesión pero no existía un modelo conceptual seleccionado. La tarjeta desaparecía al refrescar porque nunca se guardaba en backend.

## Corrección
- Exigir `modeloActual` para crear una clase desde la herramienta persistible.
- Mostrar un error accionable si no existe modelo seleccionado.
- Eliminar el fallback silencioso a nodo local en la ruta persistible.
- Mantener la protección de sesión y el canvas vacío si no existe modelo.

## Aceptación
- Con sesión y modelo activo, la clase se crea en backend y sobrevive al refresh.
- Con sesión pero sin modelo, no se crea ninguna tarjeta local y se informa cómo continuar.
- El build de Angular pasa.
