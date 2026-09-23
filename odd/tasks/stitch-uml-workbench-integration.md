# Integración Stitch CASE Studio UML Workbench

## Objetivo
Integrar la UI `stitch_case_studio_uml_workbench` al frontend Angular funcional sin reemplazar bindings ni contratos.

## Alcance
- Adoptar tokens Obsidian CAD: Geist, Space Grotesk y JetBrains Mono.
- Alinear colores, densidad, bordes y paneles con el workbench Stitch.
- Preservar canvas dinámico Angular, nodos, relaciones, inspector, drawers y estado backend.
- Mejorar la paleta UML y la lectura de clases/relaciones.
- Mantener declaración honesta de subconjunto UML 2.5.

## No alcance
- No copiar HTML estático de Stitch como pantalla aislada.
- No hardcodear entidades demo en la vista.
- No sustituir el modelo Angular por SVG estático.
- No romper endpoints, bindings, autenticación o generación.
- No incorporar aún una librería externa de canvas sin una tarea separada.

## Criterios de aceptación
- Angular compila.
- La interfaz mantiene crear/seleccionar/mover clases y editar relaciones.
- El canvas sigue siendo la superficie principal.
- Tipografía y tokens coinciden con Obsidian CAD.
- Las herramientas UML visibles siguen conectadas a métodos existentes.
- El inspector y drawers siguen funcionales.
