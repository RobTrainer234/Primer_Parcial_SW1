# Corrección CU1: aislamiento de sesión

## Problema
La interfaz puede mostrar clases, relaciones, modelo y usuario demo aunque el header indique que no existe sesión.

## Corrección
- No ejecutar login demo automáticamente en `ngOnInit`.
- Iniciar la aplicación con contexto vacío.
- Limpiar nodos, relaciones, notas, selección, proyectos, modelo, presencia, conflictos y drawers al cerrar sesión.
- Mantener login demo como acción explícita del usuario.
- Mantener guardas de sesión para operaciones protegidas.

## Aceptación
- Usuario no autenticado: no ve proyectos ni datos de modelo.
- Usuario no autenticado: canvas vacío.
- Recargar sin sesión: permanece desconectado.
- Login explícito: carga únicamente proyectos/modelos autorizados.
- Logout: elimina contexto visual y semántico local.
- Intentar operar sin sesión: solicita autenticación y no muta backend.
