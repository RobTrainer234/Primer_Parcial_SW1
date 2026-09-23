# Autenticación explícita del frontend

## Objetivo
Evitar que el frontend inicie automáticamente una sesión demo después de recargar el navegador. El usuario debe iniciar sesión explícitamente para ejecutar CU1 y las operaciones protegidas de CU2.

## Alcance
- Eliminar auto-login en `ngOnInit`.
- Cargar proyectos después de un login exitoso.
- Bloquear crear/listar/recuperar proyectos y acciones de modelado sin sesión.
- Limpiar proyecto, modelo y estado operativo al cerrar sesión.
- Mantener endpoints y contratos actuales.

## No objetivos
- No convertir la autenticación demo en autenticación productiva.
- No cambiar el backend ni la API.
- No persistir tokens demo en almacenamiento local.

## Criterios de aceptación
- Después de `Ctrl+R`, no existe sesión activa.
- Se muestra el botón de iniciar sesión.
- Crear/listar proyectos requiere sesión.
- Después de login, los proyectos se cargan.
- Después de logout, el contexto activo se limpia.
- Angular compila correctamente.
