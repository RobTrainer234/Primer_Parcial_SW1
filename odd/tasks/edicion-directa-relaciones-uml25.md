# Edición directa de relaciones UML 2.5

## Objetivo
Permitir editar sobre el canvas el verbo de una relación y la cardinalidad de ambos extremos, como en StarUML o draw.io.

## Interacción
- Cada extremo muestra un selector de multiplicidad.
- La etiqueta de la relación muestra el nombre/rol y un campo de verbo editable.
- El cambio de cardinalidad se aplica inmediatamente al modelo visual y se persiste si la relación tiene ID backend.
- El verbo se guarda al perder foco o mediante el inspector.
- El inspector mantiene edición completa como alternativa accesible.

## Restricciones
- Mantener cardinalidades soportadas por el backend actual (`UNO`, `MUCHOS`), representadas visualmente como `1` y `0..*`.
- No cambiar rutas HTTP.
- No ocultar el verbo ni reducir la relación a una sola cardinalidad.
- Mantener tipo de relación y ruta visual.

## Criterios de aceptación
- Ambos extremos tienen controles visibles.
- Se puede cambiar origen y destino independientemente.
- Se puede escribir y guardar el verbo sobre la línea.
- La etiqueta muestra el verbo actualizado.
- Angular compila correctamente.
