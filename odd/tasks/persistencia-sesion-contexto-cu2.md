# Persistencia de sesión y contexto CU2

## Problema
Al recargar el frontend se pierde la sesión en memoria y no se recuperan proyecto, modelo ni clases aunque estén persistidos en PostgreSQL.

## Corrección
- Persistir sesión activa explícita en `localStorage`.
- Persistir ids de proyecto y modelo seleccionados.
- Restaurar sesión al iniciar sólo si existe una sesión previamente iniciada; no volver al login demo automático.
- Cargar proyectos del backend y recuperar modelo por id guardado.
- Verificar que el modelo pertenece al proyecto restaurado.
- Limpiar sesión/contexto persistido al hacer logout.
- Si el proyecto/modelo fue eliminado o no es accesible, limpiar la referencia y mostrar toast informativo.

## Seguridad/alcance
- No mostrar datos sin sesión.
- No persistir un modelo completo ni reemplazar backend; sólo identidad demo/contexto e ids.
- No alterar volúmenes Docker.

## Aceptación
- Login explícito + crear proyecto/modelo/clase + Ctrl+F5 conserva sesión y datos.
- Cambiar proyecto actualiza contexto restaurado.
- Logout elimina sesión persistida y el canvas queda vacío.
- Proyecto/modelo inexistente se maneja sin romper la app.
- Angular compila y Docker frontend responde.
