# Rediseño canvas-first UML 2.5

## Objetivo
Convertir el canvas en la superficie principal para crear y editar clases UML, atributos y relaciones, manteniendo la identidad visual CASE Studio y los contratos existentes.

## Decisiones semánticas
- UML 2.5 es el estándar de referencia; se implementa un subconjunto estructural verificable.
- Una clase UML se representa como caja con estereotipo, nombre y compartimentos.
- Los atributos pertenecen a la clase y se editan desde la caja o el inspector contextual.
- Las relaciones conectan dos clases y conservan tipo, multiplicidades y un verbo de dominio opcional/recomendado.
- El verbo no es obligatorio para todas las asociaciones, pero debe existir como campo disponible y mostrarse cuando esté definido.
- El layout (posición, tamaño y rutas) se separa del modelo semántico.

## Alcance
- Crear clase haciendo clic en el canvas.
- Seleccionar y mover clases.
- Editar nombre y estereotipo.
- Agregar/editar/eliminar atributos.
- Marcar PK, obligatorio y único.
- Crear asociaciones entre clases.
- Editar verbo, multiplicidad origen/destino y tipo de relación.
- Mostrar relaciones con etiqueta verbal y cardinalidades.
- Mantener formularios como inspector contextual, no como flujo principal.

## No objetivos de esta iteración
- Implementar todos los diagramas UML 2.5.
- Implementar edición colaborativa productiva.
- Cambiar endpoints backend sin contrato confirmado.
- Convertir datos demo del canvas en persistencia productiva.

## Criterios de aceptación
- El canvas es el punto visible principal para crear clases.
- Una clase nueva aparece como caja UML editable.
- Los atributos se muestran dentro del compartimento de la clase.
- El inspector cambia según la selección.
- Una relación muestra tipo, multiplicidades y verbo cuando existe.
- La generación y validación existentes continúan compilando.
