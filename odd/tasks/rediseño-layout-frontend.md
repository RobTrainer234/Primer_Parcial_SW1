# Rediseño de layout del frontend CASE Studio

## Objetivo
Reorganizar la interfaz web manteniendo la identidad visual actual, las acciones funcionales, los bindings Angular y los contratos HTTP existentes.

## Alcance
- Sidebar global con navegación no duplicada.
- Header contextual de proyecto/modelo y sesión.
- Progress rail del flujo Proyecto → Modelo → Entidades → Atributos → Relaciones → Validación → Generación.
- Metamodelo Core como espacio de trabajo con explorador, área central e inspector contextual.
- Canvas con herramientas, lienzo dominante e inspector.
- Mejor jerarquía para IA/Qwen, generación, almacenamiento e Inspector AST.
- Responsive desktop/tablet/móvil.

## No objetivos
- No cambiar endpoints.
- No cambiar nombres de campos, modelos o bindings.
- No eliminar botones ni capacidades existentes.
- No convertir demo/opt-in/parcial en funcionalidad productiva.
- No modificar el backend.

## Criterios de aceptación
- Angular compila con `npm run compilar`.
- Se conservan los cinco módulos y sus acciones existentes.
- La navegación no presenta sidebar y tabs superiores duplicados.
- El flujo principal de modelado tiene jerarquía visual clara.
- La pantalla conserva colores y tipografías actuales.
- Los mensajes de error, demo, opt-in y estados siguen visibles.

## Estado
En implementación.
