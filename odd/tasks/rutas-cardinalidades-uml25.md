# Rutas manipulables y cardinalidades UML 2.5

## Objetivo
Mejorar las relaciones visuales del canvas para permitir rutas rectas u ortogonales manipulables y cardinalidades visibles/editables en ambos extremos.

## Decisiones
- `RECTA`: segmento directo entre los extremos de las clases.
- `ORTOGONAL`: ruta con quiebre vertical/horizontal de 90 grados.
- El control de quiebre puede arrastrarse para modificar el layout.
- La ruta es presentación/layout y no altera la semántica.
- Cada relación conserva cardinalidad de origen y destino.
- Las cardinalidades deben mostrarse cerca de ambos extremos y editarse desde el inspector.
- El verbo permanece visible junto al nombre/rol cuando existe.

## No objetivos
- No modificar la semántica UML por mover una línea.
- No reemplazar multiplicidades por un único valor.
- No convertir el trazado local en persistencia backend sin contrato.

## Criterios de aceptación
- Se puede cambiar una relación entre recta y ortogonal.
- Una ruta ortogonal tiene un control visual arrastrable.
- Las etiquetas de cardinalidad aparecen en origen y destino.
- El inspector permite editar ambas cardinalidades.
- Las relaciones mantienen verbo y nombre.
- Angular compila correctamente.
